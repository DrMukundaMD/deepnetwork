package DeepNetwork;

public class PingRequest extends Request{
    private static final String type = "PingRequest";
    private long time;

    public PingRequest (long time){ this.time = time; }

    public long getTime() { return time; }

    @Override
    public String type(){ return type; }
}
