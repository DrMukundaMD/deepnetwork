package DeepManager;

import DeepThread.DeepLogger;
import DeepThread.RequestPeersThread;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class DeepManager extends Thread implements ThreadStuff{
    private static DeepManager DM;
    private HashMap<String, DeepTorrentManager> torrents;
    private HashMap<String, ArrayList<String>> peers;
    private Queue<String> requestQueue;
    private String server;
    private int port;

    private DeepManager(boolean isServerFlag) {
        torrents = new HashMap<>();
        peers = new HashMap<>();
        requestQueue = new ArrayDeque<>();
        server = "ada";
        port = 6345;
    }

    public static synchronized DeepManager getInstance() {
        File file = new File(""); //todo

        if (DM == null)
            if(file.exists()){
                Gson gson = new Gson();
                try (FileReader reader = new FileReader(file)) {
                    DM = gson.fromJson(reader,DeepManager.class);
                }
                catch (Exception e){
                    e.printStackTrace();
                    DeepLogger.log(e.getMessage());
                }
            } else
                DM = new DeepManager(false);
        return DM;
    }

    @Override
    public void run(){
        while(true){

            // get torrents list --


            //get rid of?
            while(!requestQueue.isEmpty()){
                String file = requestQueue.poll();
                boolean done = false;

                // If torrent doesn't exist, start it
                if(!torrents.containsKey(file))
                    startTorrent(file, new ArrayList<>()); //todo*

                DeepTorrentManager dtm = torrents.get(file);

                if(dtm.isDone()){
                    done = true;
                }

                // if we need a new peer list
                if(dtm.needsPeers() && !done){
                    Thread thread = new RequestPeersThread(DM, dtm, server, port);
                    done = true;
                }

                if(!done){
                    //Thread thread = new RequestSegmentThread();
                    done = true;
                }

            }

            //call back for UI
        }
    }

    @Override
    public synchronized void closeThread(){
        //todo
        //if file still needs requesting
    }

    private void startTorrent(String filename, ArrayList<String> hashes) {
        if (!torrents.containsKey(filename))
            torrents.put(filename, new DeepTorrentManager(filename, hashes));
        else
            DeepLogger.log("DeepManager: startTorrent: Torrent Manager " + filename + " already created.");
    }

}