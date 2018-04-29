package DeepNetwork;

public class GetFileInfoResponse extends Response {
    private static final String TYPE = "GetFileInfoResponse";
    private String filename;
    private boolean[] flags;

    public GetFileInfoResponse(String filename, boolean[] flags){
        this.filename = filename;
        this.flags = flags;
    }

    public String getFilename(){ return filename; }
    public boolean[] getFlags(){ return flags; }

    @Override
    public String type(){ return TYPE; }
}