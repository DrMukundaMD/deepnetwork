package DeepNetwork;

public class LogPeer extends Response{
    private static final String type = "LogPeer";
    private String filename;
    private String hostname;

    public LogPeer(String filename, String hostname){
        this.filename = filename;
        this.hostname = hostname;
    }

    public String getFilename() {
        return filename;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public String type() { return type; }
}
