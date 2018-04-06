package DeepNetwork;

public class GetPeersRequest extends Request{
    private static final String TYPE = "GetPeersRequest";
    private String filename;

    public GetPeersRequest(String filename){ this.filename = filename; }

    public String getFilename(){ return filename; }
    public void setFilename(String filename){ this.filename = filename; }

    @Override
    public String type(){ return TYPE; }
}
