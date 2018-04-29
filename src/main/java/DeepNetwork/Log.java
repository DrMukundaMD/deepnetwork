package DeepNetwork;

import java.time.Instant;

public class Log extends Response {
    private String type = "LOG";
    private String hostname;
    private String msg;
    private Instant time;

    public Log(String hostname, String msg, Instant time){
        this.hostname = hostname;
        this.msg = msg;
        this.time = time;
    }

    public String getHostname() { return hostname; }
    public String getMsg() { return msg; }
    public String getLog() { return hostname + " : " + time + " : " + msg;}
    public Instant getTime() { return time; }

    @Override
    public String type() { return type; }
}
