package DeepServer;

import DeepNetwork.*;
import DeepThread.*;

import java.net.ServerSocket;

public class DeepServerManager implements ServerThreadStuff {
    private int maxThreads;
    private static ServerPeers peers;
    private static int numberOfThreads;

    public DeepServerManager(int maxThreads){
        numberOfThreads = 0;
        this.maxThreads = maxThreads;
        peers = new ServerPeers();
    }

    public PortResponse reception(Object obj){

        if (numberOfThreads == maxThreads){
            return new PortResponse(0);
        }

        if (obj instanceof DeepNetwork.Request || obj instanceof Response){
            openThread();
        }

        if (obj instanceof Request) {
            ServerPort s = GetPort.getPort();
            Thread deepThread = getRequestThread((Request) obj, s.getS());
            deepThread.start();
            return new PortResponse(s.getPort());
        }

        if (obj instanceof Response) {
            Thread deepThread = getResponseThread((Response) obj);
            deepThread.start();
        }

        return new PortResponse(-1);
    }

    private synchronized void openThread(){
        DeepLogger.log("DeepServer Thread opened");
        numberOfThreads++;
    }

    public synchronized void closeThread(){
        DeepLogger.log("DeepServer Thread closed");
        numberOfThreads--;
    }

    private Thread getRequestThread(Request r, ServerSocket s){
        if(r instanceof GetTorrentListRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new TorrentListThread(this, s);
        }

        if(r instanceof GetTorrentFileRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new TorrentFileThread(this, s, r);
        }

        if(r instanceof GetPeersRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new GetPeersThread(this, s, r, peers);
        }

        if(r instanceof GetFilePieceRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new GetFilePieceThread(this, s, r);
        }

        return new UnknownRequestThread(this, s);
    }

    private Thread getResponseThread(Response r){
        if(r instanceof LogPeer){
            return new LogPeerThread(peers, r);
        }

        if(r instanceof Log){
            return new LogThread(r);
        }

        return null;
    }
}
