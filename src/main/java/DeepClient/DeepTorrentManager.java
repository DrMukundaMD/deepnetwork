package DeepClient;

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
    private DeepClientManager DM;
    private ArrayList<String> hashes;
    private boolean[] segmentFlags;
    private DeepPeerManager peers;
    private int numOfSegments;
    private String filename;
    private String server;
    private boolean done;
    private boolean on;
    private int port;

    DeepTorrentManager(String filename, String server, int port, BlockingQueue<Request> fromDM, DeepClientManager DM){
        on = true;
        done = false;
        this.DM = DM;
        this.port = port;
        this.fromDM = fromDM;
        this.server = server;
        this.filename = filename;
        peers = new DeepPeerManager();

        startup();

//        numOfSegments = hashes.size();
//        segmentFlags = new boolean[numOfSegments];
//
//        for(int i = 0; i < numOfSegments; ++i)
//            segmentFlags[i] = false;

        //writeT(hashes);
    }

    @Override
    public void run(){
        DeepLogger.log("~DTM " + filename + " Started~");
        hashes = getHashes();
        numOfSegments = hashes.size();
        DeepLogger.log("~Hashes: " + hashes.toString() + "\nSize: " + numOfSegments);
        segmentFlags = new boolean[numOfSegments];
        for(int i = 0; i < numOfSegments; i++)
            segmentFlags[i] = false;

        while(!done && on){
            boolean cycle = false;
            String peer = null;

            if (peers.isEmpty()){
                requestPeers();
                cycle = true;
            } else
                peer = getPeer();

            if(!cycle){
                int segment = getNextSegment();
                requestSegment(peer, segment);
            }

//            try {
//                sleep(10000);
//            } catch (InterruptedException e){
//                DeepLogger.log(e.getMessage());
//            }

            update();
        }

        if(done) {
            MergeFilePieces.merge(filename);
            DM.closeThread(true, filename);
        }
        System.out.println("~DTM " + filename + " Closed~");
    }

    // -- Segments --

    private void addSegment(int num, byte[] segment){
        DeepLogger.log("Added segment " + num + " size:" + segment.length);
        DeepLogger.log("segment hash: " + DeepHash.getHash(segment));
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
            DeepLogger.log("segment added" + num);
        }
    }

    public BlockingQueue<Request> getFromDM() {
        return fromDM;
    }

    public byte[] getSegment(int num){

        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));
        //todo maybe add hash check here for fault tolerance?
        //todo can't use this. need to use gson
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

    private ArrayList<String> getHashes(){
        ArrayList<String> retval = null;
        GetTorrentFileRequest request = new GetTorrentFileRequest(filename);

        // port cycle
        ObjectInputStream stream = portCycle(server, port, request);

        try {
            Object response = stream.readObject();

            if(response instanceof GetTorrentFileResponse){
                retval = ((GetTorrentFileResponse) response).getTorrent();
            }

            if(response instanceof UnknownRequestResponse){
                DeepLogger.log("UnknownRequest in DTM: " + filename);
            }
            stream.close();
        }catch (ClassNotFoundException | IOException e){
            DeepLogger.log(e.getMessage());
        }

        return retval;
    }

    private void requestSegment(String host, int segment){
        if(host != null) {
            try {
                GetFilePieceRequest request = new GetFilePieceRequest(filename, segment);

                // port cycle
                ObjectInputStream stream = portCycle(host, port, request);

                try {
                    Object response = stream.readObject();

                    if (response instanceof GetFilePieceResponse) {
                        GetFilePieceResponse r = (GetFilePieceResponse) response;
                        addSegment(r.getPiece(), r.getSegment());
                    }

                    if (response instanceof UnknownRequestResponse) {
                        DeepLogger.log("Error: UnknownRequestResponse in requestSegment for torrent: " + filename);
                    }
                } catch (ClassNotFoundException e) {
                    DeepLogger.log(e.getMessage());
                }

                //Close
                stream.close();

            } catch (IOException e) {
                e.printStackTrace();
                DeepLogger.log(e.getMessage());
            }
        }else{
            DeepLogger.log("No host");
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
                DeepLogger.log("UnknownRequest in DTM: " + filename);
            }
        stream.close();
        }catch (ClassNotFoundException | IOException e){
            DeepLogger.log(e.getMessage());
        }

        //Close
    }

    // -- Control --

    private void startup(){
        File file = new File(TorrentFolder.getSegments(), filename);
        if(!file.isDirectory()){
            file.mkdir();
        }
    }

    private ObjectInputStream portCycle(String host, int port, Request request){
        try{
            while(true) {
                // create stuff
                Socket serverMain = new Socket(host, port);
                ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());
                output.writeObject(request);

                // write request & get response
                ObjectInputStream input = new ObjectInputStream(serverMain.getInputStream());
                Object newPort = input.readObject();

                // close socket
                input.close();
                output.close();
                serverMain.close();

                // get new port
                PortResponse pr;

                if (newPort instanceof PortResponse) {
                    pr = (PortResponse) newPort;
                    if (pr.getPort() != 0) {
                        serverMain = new Socket(host, pr.getPort());
                        return new ObjectInputStream(serverMain.getInputStream());
                    }
                }
                DeepLogger.log("PortCycle infinite loop.");
            }

        } catch (IOException | ClassNotFoundException e){
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
                System.out.println("DTM " +filename + " shutdown by request.");
            }
        }

        boolean done = true;

        for(int i = 0; i < numOfSegments; i++){
            if(!segmentFlags[i])
                done = false;
        }

        if(done){
            DeepLogger.log("~"+filename+" done.");
            this.done = true;
        }
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
