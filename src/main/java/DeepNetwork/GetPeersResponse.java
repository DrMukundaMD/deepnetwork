package DeepNetwork;

import java.util.ArrayList;

public class GetPeersResponse extends Response {
    private static final String TYPE = "GetPeersResponse";
    private String filename;
    private ArrayList<String> peers;

    public GetPeersResponse(String filename, ArrayList<String> peers){
        this.filename = filename;
        this.peers = peers;
    }

    public String getFilename(){ return filename; }
    public ArrayList<String> getPeers() { return peers; }

    public void setFilename(String filename){ this.filename = filename; }
    public void setPeers(ArrayList<String> peers) { this.peers = peers; }

    @Override
    public String type(){ return TYPE; }
}
