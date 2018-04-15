package DeepManager;

import DeepNetwork.*;
import DeepThread.DeepLogger;
import DeepThread.MergeFilePieces;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class DeepTorrentManager extends Thread{
    private BlockingQueue<Request> fromDM;
    private DeepManager DM;
    private ArrayList<String> hashes;
    private boolean[] segmentFlags;
    private DeepPeerManager peers;
    private int numOfSegments;
    private String filename;
    private String server;
    private boolean done;
    private boolean on;
    private int port;

    DeepTorrentManager(String filename, String server, int port, BlockingQueue<Request> fromDM, DeepManager DM){
        on = true;
        done = false;
        this.DM = DM;
        this.port = port;
        this.fromDM = fromDM;
        this.server = server;
        this.filename = filename;
        peers = new DeepPeerManager();

//        numOfSegments = hashes.size();
//        segmentFlags = new boolean[numOfSegments];
//
//        for(int i = 0; i < numOfSegments; ++i)
//            segmentFlags[i] = false;

        //writeT(hashes);
    }

    @Override
    public void run(){
        System.out.println("~DTM " + filename + " Started~");
        hashes = new ArrayList<>();
        //todo get hashes

        while(!done && on){
            boolean done = false;

//            String peer = getPeer();
//
//            if (peer == null){
//                requestPeers();
//                done = true;
//            }
//
//            if(!done){
//                int segment = getNextSegment();
//                requestSegment(peer, segment);
//            }
            try {
                sleep(1000);
            } catch (InterruptedException e){
                DeepLogger.log(e.getMessage());
            }
            update();
        }

        if(done)
            DM.closeThread(true, filename);
            //MergeFilePieces.merge(filename);
        System.out.println("~DTM " + filename + " Closed~");
    }

    // -- Segments --

    private void addSegment(int num, byte[] segment){
        if(DeepHash.compareHash(segment, hashes.get(num))) {
            Gson gson = new Gson();
            File segmentFile = new File(TorrentFolder.getSegments(), filename);
            File file = new File(segmentFile, Integer.toString(num));

            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(segment, writer);
            } catch (Exception e) {
                DeepLogger.log(e.getMessage());
            }
            segmentFlags[num] = true;
        }
    }

    public byte[] getSegment(int num){

        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));
        //todo maybe add hash check here for fault tolerance?
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            if((bStream.read(buffer)) > 0) {
                inputStream.close();
                bStream.close();
                return buffer;
            }

        } catch (Exception e){
            //e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        return null;
    }

    private int getNextSegment(){
        //todo
        for(int i = 0; i < numOfSegments; ++i)
            if(!segmentFlags[i])
                return i;
        return -1;
    }

    private void requestSegment(String host, int segment){
        try{
            // create stuff
            Socket serverMain = new Socket(host, port);
            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());
            GetFilePieceRequest request = new GetFilePieceRequest(filename, segment);

            // write request
            output.writeObject(request);

            // get new port
            DataInputStream input = new DataInputStream(serverMain.getInputStream());
            int newPort = input.readInt();

            // close socket
            input.close();
            output.close();
            serverMain.close();

            // open connection on new port
            serverMain = new Socket(host, newPort);

            // get response
            ObjectInputStream stream = new ObjectInputStream(serverMain.getInputStream());

            try {
                Object response = stream.readObject();

                if(response instanceof GetFilePieceResponse){
                    GetFilePieceResponse r = (GetFilePieceResponse) response;
                    addSegment(r.getPiece(), r.getSegment());
                }

                if(response instanceof UnknownRequestResponse){
                    DeepLogger.log("Error: UnknownRequestResponse in requestSegment for torrent: " + filename);
                }
            }catch (ClassNotFoundException e){
                DeepLogger.log(e.getMessage());
            }

            //Close
            stream.close();
            serverMain.close();

        } catch (IOException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }

    // -- Peers --

    private void setPeers(ArrayList<String> peers){
        this.peers.setPeers(peers);
    }

    private String getPeer(){
        return peers.getPeer();
    }


    private void requestPeers(){
        // create request
        GetPeersRequest request = new GetPeersRequest(filename);

        // port cycle
        ObjectInputStream stream = portCycle(server, port, request);

        try {
            Object response = stream.readObject();

            if(response instanceof GetPeersResponse){
                setPeers(((GetPeersResponse) response).getPeers());
            }

            if(response instanceof UnknownRequestResponse){
                //do stuff
                int x = 0;
            }
        stream.close();
        }catch (ClassNotFoundException | IOException e){
            DeepLogger.log(e.getMessage());
        }

        //Close
    }

    // -- Control --

    private ObjectInputStream portCycle(String host, int port, Request request){
        try{
            // create stuff
            Socket serverMain = new Socket(host, port);
            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());

            // write request
            output.writeObject(request);

            // get new port
            DataInputStream input = new DataInputStream(serverMain.getInputStream());
            int newPort = input.readInt();

            // close socket
            input.close();
            output.close();
            serverMain.close();

            // open connection on new port
            serverMain = new Socket(host, newPort);

            return new ObjectInputStream(serverMain.getInputStream());

        } catch (IOException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        return null;
    }

    private void update(){

        boolean close = false;

        if(fromDM.size() > 0) {
            Request r = fromDM.poll();

            //set priority

            if (r instanceof ShutDownRequest) {
                close = true;
            }
        }

        boolean done = true;

        for(int i = 0; i < numOfSegments; ++i){
            if(!segmentFlags[i])
                done = false;
        }

        if(done){ this.done = true; }
        if(close){this.on = false;}
    }

    public String getFilename() {
        return filename;
    }


    private void writeT(ArrayList<String> torrent){

        Gson gson = new Gson();
        File file = new File(TorrentFolder.getTorrents(), filename);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(torrent, writer);
        }
        catch (Exception e){
            DeepLogger.log(e.getMessage());
        }
    }

}
