package DeepThread;

import DeepNetwork.Ping;
import DeepNetwork.PingResponse;
import DeepNetwork.Response;
import DeepServer.ServerThreadStuff;

import java.util.concurrent.ConcurrentHashMap;

public class PingResponseThread extends Thread{
    private PingResponse response;
    private ServerThreadStuff callingThread;
    private ConcurrentHashMap<String, Ping> pingMap;

    public PingResponseThread(ServerThreadStuff callingThread, Response response, ConcurrentHashMap<String, Ping> pingMap){
        this.pingMap = pingMap;
        this.callingThread = callingThread;
        this.response = (PingResponse) response;
    }

    @Override
    public void run(){
        long time = System.currentTimeMillis() - response.getTime();

        if(pingMap.containsKey(response.getHost())){
            Ping ping = pingMap.get(response.getHost());
            ping.reset(response.getHost(), time, System.currentTimeMillis());
            DeepLogger.log("Don't know how that happened: Ping");
        } else {
            Ping ping = new Ping(response.getHost(), time, System.currentTimeMillis());
            pingMap.put(response.getHost(), ping);
        }

        callingThread.closeThread();
    }
}
