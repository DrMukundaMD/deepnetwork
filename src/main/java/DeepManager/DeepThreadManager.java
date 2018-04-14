package DeepManager;

import DeepNetwork.*;
import DeepThread.GetPeersThread;
import DeepThread.TorrentFileThread;
import DeepThread.TorrentListThread;
import DeepThread.UnknownRequestThread;

import java.net.ServerSocket;

public class DeepThreadManager implements ThreadStuff{
    private static int numberOfThreads;
    private int maxThreads;

    public DeepThreadManager(int maxThreads){
        numberOfThreads = 0;
        this.maxThreads = maxThreads;
    }

    public int reception(Object obj){

        if (numberOfThreads == maxThreads){
            return -1;
        }

        if (obj instanceof DeepNetwork.Request || obj instanceof Response){
            openThread();
        }

        if (obj instanceof Request) {
            ServerPort s = GetPort.getPort();
            Thread deepThread = getRequestThread((Request) obj, s.getS());
            deepThread.start();
            return s.getPort();
        }

        if (obj instanceof Response) {
            Thread deepThread = getResponseThread((Response) obj);
            deepThread.start();
        }

        return -1;
    }

    private synchronized void openThread(){
        numberOfThreads++;
    }

    public synchronized void closeThread(){
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
            return new GetPeersThread(this, s, r);
        }

        return new UnknownRequestThread(this, s);
    }

    private Thread getResponseThread(Response r){
        if(r instanceof LogPeer){
            //return new LogThread(s);
            int x = 0;
        }
        return null;
    }
}
