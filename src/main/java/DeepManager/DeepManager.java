package DeepManager;

import DeepThread.DeepLogger;

import java.util.ArrayList;
import java.util.HashMap;

public class DeepManager {
    private static HashMap<String, DeepTorrentManager> torrents;
    private static HashMap<String, ArrayList<String>> peers;

    public DeepManager(boolean isServerFlag){
        torrents = new HashMap<>();
        peers = new HashMap<>();
    }

    public void startTorrent(String filename, ArrayList<String> hashes){
        if(!torrents.containsKey(filename))
            torrents.put(filename, new DeepTorrentManager(filename, hashes));
        else
            DeepLogger.log("DeepManager: startTorrent: Torrent Manager "+filename+" already created.");
    }

    public int getNeededSegment(String filename){
        if(!torrents.containsKey(filename)){
            DeepLogger.log("DeepManager: getNeededSegments: Torrent Manager "+ filename +" not created.");
        }
        return torrents.get(filename).getSegments();
    }

    public void getPeers(String filename){
        if(!peers.containsKey(filename) || peers.get(filename) == null ){
            DeepLogger.log("DeepManager: getPeers: No peers for "+ filename );
        }
    }
}