package DeepNetwork;

import java.util.ArrayList;

public class GetTorrentListResponse extends Response {
    private static final String TYPE = "GetTorrentListResponse";
    private ArrayList<String> files;

    public GetTorrentListResponse(ArrayList<String> files){ this.files = files;}

    public ArrayList<String> getFiles() { return files; }
    public void setFiles(ArrayList<String> files) { this.files = files; }

    @Override
    public String type() { return TYPE; }
}
