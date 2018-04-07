import DeepNetwork.GetTorrentFileRequest;
import DeepNetwork.GetTorrentFileResponse;
import DeepNetwork.Request;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TorrentFileThread extends Thread {
    private ServerSocket responseSocket;
    private GetTorrentFileRequest request;

    TorrentFileThread(ServerSocket responseSocket, Request request){
        this.responseSocket = responseSocket;
        this.request = (GetTorrentFileRequest) request;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response todo
            GetTorrentFileResponse response = TorrentFile.get(request.getFilename());

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
    }
}
