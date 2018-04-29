package DeepClient;

import DeepNetwork.*;
import DeepThread.*;

import java.net.ServerSocket;

public class DeepClientServerManager implements ClientThreadStuff {
    private static int numberOfThreads;
    private int maxThreads;
    private String host;
//    private static Peers peers; //todo do I need this?

    DeepClientServerManager(int maxThreads){
        numberOfThreads = 0;
        this.maxThreads = maxThreads;
//        peers = new Peers();
        this.host = "";
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
            Thread thread = getRequestThread((Request) obj, s.getS());
            thread.start();
            int x=0;
            return new PortResponse(s.getPort());
        }

        if (obj instanceof Response) {
            return new PortResponse(0);
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

        if(r instanceof GetFilePieceRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new GetFilePieceThread(this, s, r);
        }

        return new UnknownRequestThread(this, s);
    }

    private Thread getResponseThread(Response r){
        if(r instanceof LogPeer){
            LogPeer lp = (LogPeer) r;
        }
        return null;
    }

    @Override
    public void closeThread(boolean flag, String filename) {

    }

    @Override
    public void contact(String filename) {

    }
}
