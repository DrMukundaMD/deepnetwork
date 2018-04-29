package DeepThread;

import DeepNetwork.*;
import DeepServer.ServerThreadStuff;
import com.google.gson.Gson;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GetFileInfoThread extends Thread{
    private GetFilePieceRequest request;
    private ServerThreadStuff callingThread;
    private ServerSocket responseSocket;

    public GetFileInfoThread(ServerThreadStuff callingThread, ServerSocket responseSocket, Request request){
        this.callingThread = callingThread;
        this.responseSocket = responseSocket;
        this.request = (GetFilePieceRequest) request;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response
            GetFileInfoResponse response = getFileInfo(request.getFile());

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

    private GetFileInfoResponse getFileInfo(String filename){
        boolean[] flags = null;
        Gson gson = new Gson();
        File torrentFolder = new File(TorrentFolder.getTorrents(), filename);
        File file = new File(torrentFolder, "flags");

        try (FileReader reader = new FileReader(file)) {
            flags = gson.fromJson(reader, boolean[].class);
        }
        catch (IOException e){
            DeepLogger.log(e.getMessage());
        }

        return new GetFileInfoResponse(filename, flags);
    }
}
