package DeepThread;

import DeepManager.ThreadStuff;
import DeepNetwork.GetPeersRequest;
import DeepNetwork.GetPeersResponse;
import DeepNetwork.GetTorrentFileResponse;
import DeepNetwork.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class GetPeersThread extends Thread{
    private GetPeersRequest request;
    private ThreadStuff callingThread;
    private ServerSocket responseSocket;
    private BlockingQueue<ArrayList<String>> peers;

    public GetPeersThread(ThreadStuff callingThread, ServerSocket responseSocket, Request request, BlockingQueue<ArrayList<String>> peers){
        this.callingThread = callingThread;
        this.responseSocket = responseSocket;
        this.request = (GetPeersRequest) request;
        this.peers = peers;
    }

    // Server code?
    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response
            // todo use

            ArrayList<String> list = peers.take();

            GetPeersResponse response = new GetPeersResponse(request.getFilename(), new ArrayList<>(list));

            peers.add(list);

            //Reply
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
            stream.writeObject(response);

            //Close
            stream.close();
            socket.close();

        } catch (IOException | InterruptedException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        callingThread.closeThread(true, "");
    }
}
