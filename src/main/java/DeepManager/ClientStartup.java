package DeepManager;

import DeepNetwork.GetPort;
import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;

public class ClientStartup {
    public static void main(String[] args){
        new DeepLogger(System.currentTimeMillis());
        new TorrentFolder();
        new GetPort();
    }
}
