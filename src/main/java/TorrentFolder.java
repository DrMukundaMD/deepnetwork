import java.io.File;

public class TorrentFolder {
    private static File toTorrent;
    private static File torrents;
    private static File segments;

    //Makes directories
    TorrentFolder(){
        toTorrent = new File("to_torrent");
        torrents = new File(".torrents");
        segments = new File(".segments");

        if(checkTorrentFolder(toTorrent))
            toTorrent = createTorrentFolder(toTorrent);

        if(checkTorrentFolder(torrents))
            torrents = createTorrentFolder(torrents);

        if(checkTorrentFolder(segments))
            segments = createTorrentFolder(segments);
    }

    private static boolean checkTorrentFolder(File folder) {
        return !folder.isDirectory();
    }

    private static File createTorrentFolder(File folder){
        if(!folder.mkdir())
            System.out.println("Unable to create " + folder.getName() + "directory");
        return folder;
    }

    static File getToTorrent() {
        return toTorrent;
    }

    static File getTorrents() {
        return torrents;
    }

    static File getSegments() {
        return segments;
    }

    static boolean isTorrent(String filename) {
        File file = new File(torrents, filename + ".txt");
        return !file.exists();
    }
}
