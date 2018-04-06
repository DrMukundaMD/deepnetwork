package DeepNetwork;

import java.util.ArrayList;

public class GetTorrentFileResponse extends Response {
    private static final String TYPE = "GetTorrentFileResponse";
    private String filename;
    private ArrayList<String> torrent;

    public GetTorrentFileResponse(String filename, ArrayList<String> torrent){
        this.filename = filename;
        this.torrent = torrent;
    }

    public String getFilename() { return filename; }
    public ArrayList<String> getTorrent() { return torrent; }

    @Override
    public String type() { return TYPE; }
}