/*-----------------------------*
	04/01/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/
import DeepNetwork.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;


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
            System.out.println("Server Started");

            while(true){
                socket = serverSocket.accept();
                newPort = reception(socket);
                DataOutputStream r = new DataOutputStream(socket.getOutputStream());
                r.writeInt(newPort);
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
            ServerPort s = GetPort.getPort();
            //System.out.println("got port # " + s.getPort());

            if (object instanceof Request) {
                Thread deepThread = getRequestThread((Request) object, s.getS());
                deepThread.start();
            }

            if (object instanceof Response) {
                //Thread deepThread = log((Response) object); // todo fix log
                //deepThread.start();
                while(true){break;}
            }

            return s.getPort();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //Logger.debug(e.getMessage());
        }
        return 6344; //todo delete
    }

    private static Thread getRequestThread(Request request, ServerSocket s){
        //Thread manager
        //static int?
        if(request instanceof GetTorrentListRequest){
            return new TorrentListThread(s);
        }

        if(request instanceof GetTorrentFileRequest){
            return new TorrentFileThread(s);
        }

        if(request instanceof GetPeersRequest){
            return new GetPeersThread(s, request);
        }

        return new UnknownRequestThread(s);
    }

}
