package DeepManager;

import DeepThread.DeepLogger;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class DeepManager extends Thread {
    private static DeepManager DM;
    private HashMap<String, DeepTorrentManager> torrents;
    private HashMap<String, ArrayList<String>> peers;
    private Queue<String> requestQueue;

    private DeepManager(boolean isServerFlag) {
        torrents = new HashMap<>();
        peers = new HashMap<>();
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
            while(!requestQueue.isEmpty()){
                String file = requestQueue.poll();


            }
        }
    }

    private void startTorrent(String filename, ArrayList<String> hashes) {
        if (!torrents.containsKey(filename))
            torrents.put(filename, new DeepTorrentManager(filename, hashes));
        else
            DeepLogger.log("DeepManager: startTorrent: Torrent Manager " + filename + " already created.");
    }


}