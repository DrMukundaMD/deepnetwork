package DeepThread;

import DeepNetwork.GetTorrentListResponse;
import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;

import java.io.*;
import java.util.ArrayList;

public class TorrentList {
//    private static File file;
    private static ArrayList<String> torrents;

    public TorrentList(ArrayList<String> torrents){
//        File file = new File(TorrentFolder.getTorrents(),".torrents");
        TorrentList.torrents = torrents;
    }

//    public static void add(String torrent){
//        torrents.add(torrent);
//    }

//    public static void remove(String torrent){
//        torrents.remove(torrent);
//    }

    public static synchronized GetTorrentListResponse get(){ return new GetTorrentListResponse(torrents); }

}
