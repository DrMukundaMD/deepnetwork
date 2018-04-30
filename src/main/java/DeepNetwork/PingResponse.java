package DeepNetwork;

public class PingResponse extends Response{
    private static final String type = "PingResponse";
    private long time;
    private String host;

    public PingResponse (long time, String host){
        this.time = time;
        this.host = host;
    }

    public long getTime() { return time; }

    public String getHost() { return host; }

    @Override
    public String type(){ return type; }
}
