package DeepThread;

import DeepNetwork.LogPeer;
import DeepNetwork.Response;

public class LogPeerThread extends Thread{
    private ServerPeers peers;
    private LogPeer response;

    public LogPeerThread(ServerPeers peers, Response response){
        this.peers = peers;
        this.response = (LogPeer) response;
    }

    @Override
    public void run(){
        peers.add(response.getFilename(), response.getHostname());
    }
}
