package DeepNetwork;

import java.time.Instant;

public class Log extends Response {
    private String type;
    private String source;
    private String dest;
    private Instant time;

    public Log(String type, String source, String dest, Instant time){
        this.type = type;
        this.source = source;
        this.dest = dest;
        this.time = time;
    }

    public String getType() { return type; }
    public String getSource() { return source; }
    public String getDest() { return dest; }
    public Instant getTime() { return time; }

    @Override
    public String type() { return type; }
}
