import DeepNetwork.GetPeersResponse;
import DeepNetwork.GetTorrentFileResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GetPeersThread extends Thread{
    private ServerSocket responseSocket;

    GetPeersThread(ServerSocket responseSocket){
        this.responseSocket = responseSocket;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response todo
            GetPeersResponse response = new GetPeersResponse("",new ArrayList<String>());

            //Reply
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
