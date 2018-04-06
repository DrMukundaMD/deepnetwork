package DeepNetwork;

public class GetFilePieceResponse extends Request{
    private static final String TYPE = "GetFilePieceResponse";
    private String filename;
    private int piece;
    private byte[] segment;

    public GetFilePieceResponse(String filename, int piece, byte[] segment){
        this.filename = filename;
        this.piece = piece;
        this.segment = segment;
    }

    public String getFile(){ return filename; }
    public int getPiece(){ return piece; }
    public byte[] getSegment(){ return segment; }

    public void setFile(String file_name){ this.filename = file_name; }
    public void setPiece(int piece){ this.piece = piece; }
    public void setSegment(byte[] segment){ this.segment = segment; }

    @Override
    public String type(){ return TYPE; }
}