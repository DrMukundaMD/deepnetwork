package DeepNetwork;

import java.net.ServerSocket;

public class ServerPort {
    private int port;
    private ServerSocket s;

    ServerPort(int port, ServerSocket s){
        this.port = port;
        this.s = s;
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getS() {
        return s;
    }
}
