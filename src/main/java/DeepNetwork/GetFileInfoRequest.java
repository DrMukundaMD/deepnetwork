package DeepNetwork;

public class GetFileInfoRequest extends Request{
    private static final String TYPE = "GetFileInfoRequest";
    private String filename;

    public GetFileInfoRequest(String filename){ this.filename = filename;}

    public String getFilename(){ return filename; }

    public void setFilename(String filename){ this.filename = filename; }

    @Override
    public String type(){ return TYPE; }
}