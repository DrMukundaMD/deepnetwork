package DeepThread;

import DeepClient.ClientThreadStuff;
import DeepServer.ServerThreadStuff;
import DeepNetwork.UnknownRequestResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class UnknownRequestThread extends Thread{
    private ServerSocket responseSocket;
    private ServerThreadStuff callingThread;
    private ClientThreadStuff callingThread_;

    public UnknownRequestThread(ServerThreadStuff callingThread, ServerSocket responseSocket){
        this.callingThread = callingThread;
        this.responseSocket = responseSocket;
    }

    public UnknownRequestThread(ClientThreadStuff callingThread, ServerSocket responseSocket){
        this.callingThread_ = callingThread;
        this.responseSocket = responseSocket;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response
            UnknownRequestResponse response = new UnknownRequestResponse();

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

        if(callingThread != null)
            callingThread.closeThread();
        else
            callingThread_.closeThread(true, "");
    }
}
