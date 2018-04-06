package DeepNetwork;

public class GetFileInfoResponse extends Response {
    private static final String TYPE = "GetFileInfoResponse";
    private String filename;
    private boolean[] flags;
    private int num;

    public GetFileInfoResponse(String filename, boolean[] flags, int num){
        this.filename = filename;
        this.flags = flags;
        this.num = num;
    }

    public String getFilename(){ return filename; }
    public boolean[] getFlags(){ return flags; }
    public int getNum(){ return num; }

    public void setFilename(String filename){ this.filename = filename; }
    public void setFileInfo(boolean[] flags, int num){
        this.flags = flags;
        this.num = num;
    }


    @Override
    public String type(){ return TYPE; }
}