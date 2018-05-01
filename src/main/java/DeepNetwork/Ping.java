package DeepNetwork;

public class Ping implements Comparable<Ping>{
    private String host;
    private long ping;
    private long start;

    public Ping (String host, long ping, long start){
        this.host = host;
        this.ping = ping;
        this.start = start;
    }

    public void reset (String host, long ping, long start){
        this.host = host;
        this.ping = ping;
        this.start = start;
    }

    public long getPing() { return ping; }

    public long getStart() { return start; }

    public String getHost() { return host; }

    @Override
    public int compareTo(Ping ping){
        return (int) (this.ping - ping.getPing());
    }
}
