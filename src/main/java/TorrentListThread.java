import DeepNetwork.GetTorrentListResponse;
import DeepNetwork.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TorrentListThread extends Thread{
    //private Request request;
    private ServerSocket responseSocket;


    TorrentListThread(ServerSocket re){
        //this.request = request;
        this.responseSocket = re;
    }

    @Override
    public void run(){
        try{
            //Set up new socket
            //responseSocket = new ServerSocket(port);
            //responseSocket.setSoTimeout(10000);
            //Accept connections
            Socket socket = responseSocket.accept();
            //Get data
            GetTorrentListResponse response = new GetTorrentListResponse(TorrentList.get());
            //Write data back
            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
            stream.writeObject(response);
            //Close
            stream.close();
            socket.close();

        } catch (IOException e){
            e.printStackTrace();

        }
    }
}
