package DeepNetwork;

public class GetTorrentFileResponse extends Response {
    private static final String TYPE = "GetTorrentFileResponse";
    private String filename;
    private String torrent;

    public GetTorrentFileResponse(String filename, String torrent){
        this.filename = filename;
        this.torrent = torrent;
    }

    public String getFilename() { return filename; }
    public String getTorrent() { return torrent; }

    @Override
    public String type() { return TYPE; }
}