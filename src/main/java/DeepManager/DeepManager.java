package DeepManager;

import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DeepManager extends Thread implements ThreadStuff{
    private static DeepManager DM;
    private HashMap<String, DeepTorrentManager> torrents;
    private BlockingQueue<String> UIQueue;
    private BlockingQueue<String> doneQueue;
    private String server;
    private int port;

    public DeepManager(boolean isServerFlag, BlockingQueue<String> UIQueue) {
        torrents = new HashMap<>();
        doneQueue = new LinkedBlockingQueue<>();
        this.UIQueue = UIQueue;
        server = "ada";
        port = 6345;
    }

    public static synchronized DeepManager getInstance(boolean flag, BlockingQueue<String> UI) {
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
                DM = new DeepManager(flag, UI);

        return DM;
    }

    @Override
    public void run(){
        boolean on = true;
        while(on){ //user is not ended
            try{
                String msg = UIQueue.take();

                if(msg.equals("get"));

            } catch (InterruptedException e){
                DeepLogger.log(e.getMessage());
            }
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