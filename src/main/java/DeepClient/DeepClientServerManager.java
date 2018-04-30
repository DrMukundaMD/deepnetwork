package DeepClient;

import DeepNetwork.*;
import DeepServer.ServerThreadStuff;
import DeepThread.*;

import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class DeepClientServerManager implements ServerThreadStuff {
    private static int numberOfThreads;
    private int maxThreads;
    private ConcurrentHashMap queue;
//    private String host;

    DeepClientServerManager(int maxThreads, ConcurrentHashMap queue){
        numberOfThreads = 0;
        this.queue = queue;
        this.maxThreads = maxThreads;
//        this.host = "";
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
            Thread thread = getResponseThread((Response) obj);
            thread.start();
        }

        return new PortResponse(-1);
    }

    private synchronized void openThread(){
//        DeepLogger.log("DeepServer Thread opened");
        numberOfThreads++;
    }

    public synchronized void closeThread(){
//        DeepLogger.log("DeepServer Thread closed");
        numberOfThreads--;
    }

    private Thread getRequestThread(Request r, ServerSocket s){

        if(r instanceof GetFilePieceRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new GetFilePieceThread(this, s, r);
        }

        if( r instanceof GetFileInfoRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new GetFileInfoThread(this, s, r);
        }

        if( r instanceof PingRequest){
            DeepLogger.log("~Request " + r.type() + "~");
            return new PingRequestThread(this, s, r);
        }

        return new UnknownRequestThread(this, s);
    }

    private Thread getResponseThread(Response r){
        if(r instanceof PingResponse){
            DeepLogger.log("~Response " + r.type() + "~");
            return new PingResponseThread(this, r, queue);
        }

        return null;
    }
}
