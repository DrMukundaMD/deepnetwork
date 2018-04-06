package DeepNetwork;

public class GetTorrentFileRequest extends Request{
    private static final String TYPE = "GetTorrentFileRequest";
    private String filename;

    public GetTorrentFileRequest(String filename){ this.filename = filename; }

    public String getFilename(){ return filename; }
    public void setFilename(String filename){ this.filename = filename; }

    @Override
    public String type(){ return TYPE; }
}