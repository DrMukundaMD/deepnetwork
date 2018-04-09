import DeepNetwork.GetPort;
import DeepThread.DeepLogger;
import DeepThread.MergeFilePieces;
import DeepThread.TorrentFolder;
import DeepThread.TorrentList;

import java.io.File;
import java.util.ArrayList;

public class ServerStartup {
    public static void main(String[] args){
        //create to_torrent and .torrents dir
        new TorrentFolder();
        ArrayList<String> torrents = MakeTorrents.makeAllTorrents();
        new TorrentList(torrents);
        new GetPort();
        new DeepLogger(System.currentTimeMillis());
    }
}