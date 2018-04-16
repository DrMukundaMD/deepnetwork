package DeepThread;

import DeepManager.ThreadStuff;
import DeepNetwork.GetFilePieceRequest;
import DeepNetwork.GetFilePieceResponse;
import DeepNetwork.Request;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class GetFilePieceThread extends Thread{
    private GetFilePieceRequest request;
    private ThreadStuff callingThread;
    private ServerSocket responseSocket;

    public GetFilePieceThread(ThreadStuff callingThread, ServerSocket responseSocket, Request request){
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

        callingThread.closeThread(true, "");
    }

    private GetFilePieceResponse getFilePiece(String filename, int num){

        int buffer_size = 256 * 1024;
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));

        //todo hash check for fault tolerance

        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            if((bStream.read(buffer)) > 0) {
                inputStream.close();
                bStream.close();
            }
            DeepLogger.log("Segment not read, still sending over network.");
        } catch (IOException e){
            DeepLogger.log(e.getMessage());
        }

        return new GetFilePieceResponse(filename, num, buffer);
    }
}
