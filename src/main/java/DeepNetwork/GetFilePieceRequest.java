package DeepNetwork;

public class GetFilePieceRequest extends Request{
    private static final String TYPE = "GetFilePieceRequest";
    private String filename;
    private int piece;

    public GetFilePieceRequest(String filename, int piece){
        this.filename = filename;
        this.piece = piece;
    }

    public String getFile(){ return filename; }
    public int getPiece(){ return piece; }

    public void setFile(String file_name){ this.filename = file_name; }
    public void setPiece(int piece){ this.piece = piece; }

    @Override
    public String type(){ return TYPE; }
}
