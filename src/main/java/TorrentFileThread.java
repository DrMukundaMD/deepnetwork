import DeepNetwork.GetTorrentFileResponse;
import DeepNetwork.GetTorrentListResponse;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TorrentFileThread extends Thread {
    private ServerSocket responseSocket;

    TorrentFileThread(ServerSocket re){
        this.responseSocket = re;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Retrieve data todo
            GetTorrentFileResponse response = new GetTorrentFileResponse("","");

            //Send data back
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
