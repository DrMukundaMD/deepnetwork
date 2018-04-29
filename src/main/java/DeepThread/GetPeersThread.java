package DeepThread;

import DeepServer.ServerThreadStuff;
import DeepNetwork.GetPeersRequest;
import DeepNetwork.GetPeersResponse;
import DeepNetwork.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class GetPeersThread extends Thread{
    private GetPeersRequest request;
    private ServerThreadStuff callingThread;
    private ServerSocket responseSocket;
    private ServerPeers peers;

    public GetPeersThread(ServerThreadStuff callingThread, ServerSocket responseSocket, Request request, ServerPeers peers){
        this.callingThread = callingThread;
        this.responseSocket = responseSocket;
        this.request = (GetPeersRequest) request;
        this.peers = peers;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response

            GetPeersResponse response = new GetPeersResponse(request.getFilename(), peers.get(request.getFilename()));

            //Reply
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
            stream.writeObject(response);

            //Close
            stream.close();
            socket.close();

        } catch (IOException  e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        callingThread.closeThread();
    }
}
