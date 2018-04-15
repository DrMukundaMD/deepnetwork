package DeepThread;

import java.io.File;

public class TorrentFolder {
    private static File toTorrent;
    private static File torrents;
    private static File segments;
    private static File done;

    //Makes directories
    public TorrentFolder(){
        toTorrent = new File("to_torrent");
        torrents = new File(".torrents");
        segments = new File(".segments");
        done = new File("downloads");

        getToTorrent();
        getTorrents();
        getSegments();
        getDone();
    }

    private static boolean checkTorrentFolder(File folder) {
        return !folder.isDirectory();
    }

    private static File createTorrentFolder(File folder){
        if(!folder.mkdir())
            System.out.println("Unable to create " + folder.getName() + "directory");
        return folder;
    }

    public static File getToTorrent() {
        if(checkTorrentFolder(toTorrent))
            toTorrent = createTorrentFolder(toTorrent);
        return toTorrent;
    }

    public static File getTorrents() {
        if(checkTorrentFolder(torrents))
            torrents = createTorrentFolder(torrents);
        return torrents;
    }

    public static File getSegments() {
        if(checkTorrentFolder(segments))
            segments = createTorrentFolder(segments);
        return segments;
    }

    public static File getDone() {
        if(checkTorrentFolder(done))
            done = createTorrentFolder(done);
        return done;
    }

    public static boolean isTorrent(String filename) {
        File file = new File(torrents, filename + ".txt");
        return !file.exists();
    }
}
