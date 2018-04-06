/*-----------------------------*
	04/01/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/
import DeepNetwork.*;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;


class DeepServer {
    private static final int PORT = 6345;

    public static void main(String [] args) {
        //Start up stuff
        ServerStartup.main(null);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            serverSocket.setSoTimeout(10000);
            int newPort;
            Socket socket;
            System.out.println("~DeepServer Started~");

            while(true){
                socket = serverSocket.accept();
                newPort = reception(socket);
                if(newPort != -1) {
                    DataOutputStream r = new DataOutputStream(socket.getOutputStream());
                    r.writeInt(newPort);
                }
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Log
        }
    }

    private static int reception(Socket socket) {
        try {
            ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
            Object object = stream.readObject();
            //System.out.println("got port # " + s.getPort());

            if (object instanceof Request) {
                ServerPort s = GetPort.getPort();
                Thread deepThread = getRequestThread((Request) object, s.getS());
                deepThread.start();
                return s.getPort();
            }

            if (object instanceof Response) {
                Thread deepThread = getResponseThread((Response) object);
                deepThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static Thread getRequestThread(Request request, ServerSocket s){
        //Thread manager
        //static int?
        if(request instanceof GetTorrentListRequest){
            return new TorrentListThread(s);
        }

        if(request instanceof GetTorrentFileRequest){
            return new TorrentFileThread(s, request);
        }

        if(request instanceof GetPeersRequest){
            return new GetPeersThread(s, request);
        }

        return new UnknownRequestThread(s);
    }

    private static Thread getResponseThread(Response r){
        if(r instanceof LogPeer){
            //return new LogThread(s);
        }
        return null;
    }

}
