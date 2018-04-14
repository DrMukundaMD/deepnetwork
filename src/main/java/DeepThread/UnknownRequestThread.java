package DeepThread;

import DeepManager.ThreadStuff;
import DeepNetwork.UnknownRequestResponse;
import DeepThread.DeepLogger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class UnknownRequestThread extends Thread{
    private ServerSocket responseSocket;
    private ThreadStuff callingThread;

    public UnknownRequestThread(ThreadStuff callingThread, ServerSocket responseSocket){
        this.callingThread = callingThread;
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

        callingThread.closeThread();
    }
}
