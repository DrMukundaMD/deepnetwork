import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;

import java.io.File;
import java.util.ArrayList;

public class MakeTorrents {

    public static ArrayList<String> makeAllTorrents(){
        //iterate through to_torrents and compile torrents
        String filename;
        ArrayList<String> torrents = new ArrayList<>();
        try {
            for (File file : TorrentFolder.getToTorrent().listFiles()) {
                if (file.isFile()) {
                    filename = file.getName();
                    torrents.add(filename);
                    if(TorrentFolder.isTorrent(filename))
                        CreateTorrentFile.create(file);
                }

            }
        } catch (Exception e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
        return torrents;
    }
}
