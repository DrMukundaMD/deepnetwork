import DeepNetwork.GetTorrentListResponse;

import java.io.*;
import java.util.ArrayList;

public class TorrentList {
    //private static File file;
    private static ArrayList<String> torrents;

    TorrentList(ArrayList<String> torrents){
        File file = new File(TorrentFolder.getTorrents(),".torrents");
        this.torrents = torrents;

        try{
            file.createNewFile();
            FileOutputStream stream = new FileOutputStream(file);
            ObjectOutputStream objectStream = new ObjectOutputStream(stream);
            objectStream.writeObject(torrents);
            stream.close();
            objectStream.close();
        } catch (IOException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }
    //todo this shit might be awesome with a gson object
    public static void add(String torrent){
        torrents.add(torrent);
    }

    public static void remove(String torrent){
        torrents.remove(torrent);
    }

    public static synchronized GetTorrentListResponse get(){ return new GetTorrentListResponse(torrents); }

}
