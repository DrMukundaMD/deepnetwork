package DeepThread;

import DeepClient.ClientThreadStuff;
import DeepServer.ServerThreadStuff;
import DeepNetwork.GetFilePieceRequest;
import DeepNetwork.GetFilePieceResponse;
import DeepNetwork.Request;
import com.google.gson.Gson;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GetFilePieceThread extends Thread{
    private GetFilePieceRequest request;
    private ServerThreadStuff callingThread;
    private ClientThreadStuff callingThread_;
    private ServerSocket responseSocket;

    public GetFilePieceThread(ServerThreadStuff callingThread, ServerSocket responseSocket, Request request){
        this.callingThread = callingThread;
        this.responseSocket = responseSocket;
        this.request = (GetFilePieceRequest) request;
    }

    public GetFilePieceThread(ClientThreadStuff callingThread, ServerSocket responseSocket, Request request){
        this.callingThread_ = callingThread;
        this.responseSocket = responseSocket;
        this.request = (GetFilePieceRequest) request;
    }

    @Override
    public void run(){
        try{
            //Accept connections
            Socket socket = responseSocket.accept();

            //Set Response
            GetFilePieceResponse response = getFilePiece(request.getFile(), request.getPiece());

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

    private GetFilePieceResponse getFilePiece(String filename, int num){

        int buffer_size = 256 * 1024;
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));

        //todo hash check for fault tolerance

        Gson gson = new Gson();
        try (FileReader reader = new FileReader(file)) {
            buffer = gson.fromJson(reader, byte[].class);
            DeepLogger.log("No shit");
        }
        catch (IOException e){
            DeepLogger.log(e.getMessage());
        }

        return new GetFilePieceResponse(filename, num, buffer);
    }
}
