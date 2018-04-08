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
        done = new File("done");

        if(checkTorrentFolder(toTorrent))
            toTorrent = createTorrentFolder(toTorrent);

        if(checkTorrentFolder(torrents))
            torrents = createTorrentFolder(torrents);

        if(checkTorrentFolder(segments))
            segments = createTorrentFolder(segments);

        if(checkTorrentFolder(done))
            done = createTorrentFolder(done);
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
        return toTorrent;
    }

    public static File getTorrents() {
        return torrents;
    }

    public static File getSegments() {
        return segments;
    }

    public static File getDone() { return done; }

    public static boolean isTorrent(String filename) {
        File file = new File(torrents, filename + ".txt");
        return !file.exists();
    }
}
