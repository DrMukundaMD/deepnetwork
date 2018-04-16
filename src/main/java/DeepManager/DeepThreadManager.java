package DeepManager;

import DeepNetwork.*;
import DeepThread.*;

import java.net.ServerSocket;

public class DeepThreadManager implements ThreadStuff{
    private static int numberOfThreads;
    private int maxThreads;
    private static Peers peers;

    public DeepThreadManager(int maxThreads){
        numberOfThreads = 0;
        this.maxThreads = maxThreads;
        peers = new Peers();
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
        numberOfThreads++;
    }

    public synchronized void closeThread(boolean flag, String filename){
        // Add some static object management here?
        numberOfThreads--;
    }

    private Thread getRequestThread(Request r, ServerSocket s){
        if(r instanceof GetTorrentListRequest){
            return new TorrentListThread(this, s);
        }

        if(r instanceof GetTorrentFileRequest){
            return new TorrentFileThread(this, s, r);
        }

        if(r instanceof GetPeersRequest){
            return new GetPeersThread(this, s, r, peers.get(((GetPeersRequest) r).getFilename()));
        }

        if(r instanceof GetFilePieceRequest){
            return new GetFilePieceThread(this, s, r);
        }

        return new UnknownRequestThread(this, s);
    }

    private Thread getResponseThread(Response r){
        if(r instanceof LogPeer){
            peers.add(((LogPeer) r).getFilename(),((LogPeer) r).getHostname());
        }
        return null;
    }
}
