package DeepManager;

import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
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
    //todo make concurrent?
    private Queue<String> doneQueue;
    private String server;
    private int port;

    private DeepManager(boolean isServerFlag) {
        torrents = new HashMap<>();
        doneQueue = new ArrayDeque<>();
        server = "ada";
        port = 6345;
    }

    public static synchronized DeepManager getInstance() {
        File file = new File(TorrentFolder.getSegments(), ".dm");

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
        while(true){ //user is not ended

            // user.request1 (get new torrent list)

            // user.request2 (get torrent)

            // check done queue
            if(!doneQueue.isEmpty()){
                //tell ui
                while(!doneQueue.isEmpty()){
                    String s = doneQueue.poll();
                }

            }

            //call back for UI
        }
    }

    @Override
    public synchronized void closeThread(boolean flag, String file){
        //todo
        //if file still needs requesting
        if(flag){
            doneQueue.add(file);
        }

    }

    private void startTorrent(String filename, ArrayList<String> hashes) {
        if (!torrents.containsKey(filename))
            torrents.put(filename, new DeepTorrentManager(filename, hashes, server, port));
        else
            DeepLogger.log("DeepManager: startTorrent: Torrent Manager " + filename + " already created.");
    }

}