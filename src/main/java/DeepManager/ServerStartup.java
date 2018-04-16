package DeepManager;

import DeepNetwork.GetPort;
import DeepThread.*;

import java.util.ArrayList;

public class ServerStartup {
    public static void main(String[] args){
        //create to_torrent and .torrents dir
        new TorrentFolder();
        ArrayList<String> torrents = MakeTorrents.makeAllTorrents();
        System.out.println(torrents.toString());
        new TorrentList(torrents);
        new GetPort();
        new DeepLogger(System.currentTimeMillis());
    }
}