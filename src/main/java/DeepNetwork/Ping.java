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

    public int compareTo(Ping ping){
        if(this.ping - ping.ping < 0){
            return -1;
        } else if (this.ping - ping.ping > 0){
            return 1;
        } else
            return 0;
    }
}
