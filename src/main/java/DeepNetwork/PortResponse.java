package DeepNetwork;

public class PortResponse extends Response{
    private static final String type = "PortResponse";
    private int port;

    public PortResponse(int port){ this.port = port; }

    public int getPort() { return port; }

    @Override
    public String type(){ return type; }
}
