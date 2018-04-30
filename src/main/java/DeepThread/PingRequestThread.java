package DeepThread;

import DeepNetwork.PingRequest;
import DeepNetwork.PingResponse;
import DeepNetwork.Request;
import DeepServer.ServerThreadStuff;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class PingRequestThread extends Thread{
    private ServerSocket responseSocket;
    private PingRequest request;
    private ServerThreadStuff callingThread;

    public PingRequestThread(ServerThreadStuff callingThread, ServerSocket s, Request request){
        this.responseSocket = s;
        this.callingThread = callingThread;
        this.request = (PingRequest) request;
    }

    @Override
    public void run(){
        try {
            //Accept
            Socket socket = responseSocket.accept();

            String host = InetAddress.getLocalHost().getHostName();

            //Set Response
            PingResponse response = new PingResponse(request.getTime(), host);

            //Reply
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
            stream.writeObject(response);

            //Close
            stream.close();
            socket.close();
        } catch (IOException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());

        }

        callingThread.closeThread();
    }
}
