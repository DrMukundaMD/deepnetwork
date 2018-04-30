package DeepClient;

import DeepNetwork.*;
import DeepThread.DeepLogger;
import DeepThread.MergeFilePieces;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class DeepTorrentManager extends Thread{
    private transient BlockingQueue<Request> fromDM;
    private transient DeepClientManager DM;
    private ArrayList<String> hashes;
    private boolean[] segmentFlags;
    private DeepPeerManager peers;
    private int numOfSegments;
    private String filename;
    private String webServer;
    private boolean done;
    private boolean on;
    private int webServerPort;
    private int clientServerPort;
    private long lastPing;
    private static long pingResetTime = 60000;
    private ConcurrentHashMap<String, Ping> pingMap;
    private Queue<Ping> pingQueue;

    DeepTorrentManager(String fn, String ws, int wsPort, int csPort, BlockingQueue<Request> fromDM,
                       DeepClientManager DM, ConcurrentHashMap<String, Ping> pingMap){
        on = true;
        done = false;
        this.pingMap = pingMap;
        this.DM = DM;
        webServerPort = wsPort;
        clientServerPort = csPort;
        this.fromDM = fromDM;
        this.webServer = ws;
        this.filename = fn;
        peers = new DeepPeerManager();
        startup();
        pingQueue = new PriorityQueue<>();
    }

    @Override
    public void run(){
        DeepLogger.log("~DTM " + filename + " Started~");
        lastPing = 0;
        this.on = true;
        this.done = false;

        // checks cached files or requests hash
        hashes = getHashes();

        // verifies cached segments are valid
        check();
        update();

        while(!done && on){
//            boolean cycle = false;
            String peer = peerAndPing();

//            ping();

            if(peer != null){
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
            logAsPeer();
            MergeFilePieces.merge(filename);
            DM.closeThread(true, filename);
        } else {
            DM.closeThread(false, filename);
        }
        System.out.println("~DTM " + filename + " Closed~");
    }

    // -- Segments --

    private void addSegment(int num, byte[] segment, String host){
        if(segment != null) {
            if (DeepHash.compareHash(segment, hashes.get(num))) {
                Gson gson = new Gson();
                File segmentFolder = new File(TorrentFolder.getSegments(), filename);
                File file = new File(segmentFolder, Integer.toString(num));

                try (FileWriter writer = new FileWriter(file)) {
                    gson.toJson(segment, writer);
                } catch (Exception e) {
                    DeepLogger.log(e.getMessage());
                }
                segmentFlags[num] = true;
                writeFlags(segmentFlags);
                DeepLogger.log("~Segment # " + num + " downloaded from " + host + "~");
            } else
                DeepLogger.log("~Segment #" + num + " corrupted from " + host + "~");
        }else
            DeepLogger.log("~Segment #" + num + " null from" + host + "~");

    }

    public BlockingQueue<Request> getFromDM() {
        return fromDM;
    }

    private int getNextSegment(){
        //todo
        for(int i = 0; i < numOfSegments; ++i)
            if(!segmentFlags[i])
                return i;
        return -1;
    }

    private ArrayList<String> getHashes(){
        ArrayList<String> hashes = readT();

        if(hashes == null) { //Do not have cached hashes
            GetTorrentFileRequest request = new GetTorrentFileRequest(filename);

            // port cycle
            ObjectInputStream stream = portCycle(webServer, webServerPort, request);

            try {
                Object response = stream.readObject();

                if(response instanceof GetTorrentFileResponse){
                    hashes = ((GetTorrentFileResponse) response).getTorrent();
                    writeT(hashes);
                    numOfSegments = hashes.size();
                    DeepLogger.log("~" + filename + " hashes received. Size: " + numOfSegments + "~");
                }

                if(response instanceof UnknownRequestResponse){
                    DeepLogger.log("UnknownRequest in DTM: " + filename + " getHashes()");
                }

                stream.close();
            }catch (ClassNotFoundException | IOException e){
                DeepLogger.log(e.getMessage());
            }
        } else {
            numOfSegments = hashes.size();
            DeepLogger.log("~" + filename + " hashes read. Size: " + numOfSegments + "~");
        }

        return hashes;
    }

    private void requestSegment(String host, int segment){
        if(host != null) {
            try {
                GetFilePieceRequest request = new GetFilePieceRequest(filename, segment);

                // port cycle
                ObjectInputStream stream;
                if(host.equals(webServer))
                    stream = portCycle(host, webServerPort, request);
                else
                    stream = portCycle(host, clientServerPort, request);

                try {
                    Object response = stream.readObject();

                    if (response instanceof GetFilePieceResponse) {
                        GetFilePieceResponse r = (GetFilePieceResponse) response;
                        addSegment(r.getPiece(), r.getSegment(), host);
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
        ObjectInputStream stream = portCycle(webServer, webServerPort, request);

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

    private void logAsPeer(){
        try {
            String host = InetAddress.getLocalHost().getHostName();
            LogPeer logPeer = new LogPeer(filename, host);
            Socket serverMain = new Socket(webServer, webServerPort);
            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());
            output.writeObject(logPeer);
            output.close();
            serverMain.close();
        } catch (IOException e){
            DeepLogger.log(e.getMessage());
        }

    }

    // -- Control --

    private void startup(){
        File segmentFolder = new File(TorrentFolder.getSegments(), filename);
        File torrentFolder = new File(TorrentFolder.getTorrents(), filename);

        if(!segmentFolder.isDirectory()){
            segmentFolder.mkdir();
        }

        if(!torrentFolder.isDirectory()){
            torrentFolder.mkdir();
        }
    }

    private void check(){
        if(segmentFlags == null ) {
            segmentFlags = new boolean[numOfSegments];
        }

        for(int i = 0; i < numOfSegments; i++){
            segmentFlags[i] = false;
        }

        File segmentFile = new File(TorrentFolder.getSegments(), filename);

        for (int i = 0; i < numOfSegments; i++) {
            File file = new File(segmentFile, Integer.toString(i));

            if(file.exists() && file.isFile()) {
                Gson gson = new Gson();
                byte[] buffer;

                try (FileReader reader = new FileReader(file)) {
                    buffer = gson.fromJson(reader, byte[].class);

                    if(DeepHash.compareHash(buffer, hashes.get(i))) {
                        segmentFlags[i] = true;
//                        DeepLogger.log("~Piece # " + i + "valid~");
                    }
                } catch (IOException e){
                    DeepLogger.log(e.getMessage());
                }
            }

        }

        writeFlags(segmentFlags);
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

        boolean isdone = checkSegments();

        if(isdone){
            check();

            if(checkSegments()) {
                DeepLogger.log("~" + filename + " done.");
                this.done = true;
            } else{
                this.done = false;
            }
        }

        if(close){this.on = false;}
    }

    private String peerAndPing(){
        String peer;

        if (peers.isEmpty()){
            requestPeers();
        }

        if(peers.checkHost(webServer))
            return peers.getPeer();

        if(lastPing == 0 || System.currentTimeMillis() - lastPing > pingResetTime){

            for(String p: peers.getArray()){
                ping(p);
                pingQueue.add(pingMap.get(p));
            }

            lastPing = System.currentTimeMillis();

            int count = 0;
            ArrayList<String> list = null;

            while(count < 4 && pingQueue.size() > 0){
                list = new ArrayList<>();
                Ping ping = pingQueue.poll();
                list.add(ping.getHost());
                DeepLogger.log("Host: " + ping.getHost() + " Ping: " + ping.getPing());
                count++;
            }

            peers.setPriority(list);
        }

        peer = peers.getPeer();

        return peer;
    }

    private void ping(String host){
        PingRequest request = new PingRequest(System.currentTimeMillis());
        ObjectInputStream stream  = portCycle(host, clientServerPort, request);

        try {
            Object response = stream.readObject();

            if (response instanceof PingResponse) {
                PingResponse r = (PingResponse) response;
                long time = (System.currentTimeMillis() - r.getTime()) / 2;

                if(pingMap.containsKey(r.getHost())){
                    Ping ping = pingMap.get(r.getHost());
                    ping.reset(r.getHost(), time, System.currentTimeMillis());
                } else {
                    Ping ping = new Ping(r.getHost(), time, System.currentTimeMillis());
                    pingMap.put(r.getHost(), ping);
                }
            }

            if (response instanceof UnknownRequestResponse) {
                DeepLogger.log("Error: UnknownRequestResponse in requestSegment for torrent: " + filename);
            }

            stream.close();
        } catch (ClassNotFoundException | IOException e) {
            DeepLogger.log(e.getMessage());
        }
    }

    private boolean checkSegments(){

        for(int i = 0; i < numOfSegments; i++){
            if(!segmentFlags[i]) {
//                DeepLogger.log("Missing piece # " + i);
                return false;
            }
        }

        return true;
    }

    public String getFilename() {
        return filename;
    }

    private void writeT(ArrayList<String> torrent){

        Gson gson = new Gson();
        File torrentFolder = new File(TorrentFolder.getTorrents(), filename);
        File file = new File(torrentFolder, "hash");

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(torrent, writer);
        }
        catch (Exception e){
            DeepLogger.log(e.getMessage());
        }
    }

    private ArrayList<String> readT(){

        Gson gson = new Gson();
        ArrayList<String> hashes = null;
        File torrentFolder = new File(TorrentFolder.getTorrents(), filename);
        File file = new File(torrentFolder, "hash");

        if(file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                hashes = gson.fromJson(reader, ArrayList.class);
            } catch (IOException e) {
                DeepLogger.log(e.getMessage());
            }
        }

        return hashes;
    }

    private void writeFlags(boolean[] flags){
        Gson gson = new Gson();
        File torrentFolder = new File(TorrentFolder.getTorrents(), filename);
        File file = new File(torrentFolder, "flags");

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(flags, writer);
        }
        catch (Exception e){
            DeepLogger.log(e.getMessage());
        }
    }

}
