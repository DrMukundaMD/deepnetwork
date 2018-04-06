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

        ServerStartup.main(null);
        int x;
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            //serverSocket.setSoTimeout(10000);
            Socket socket;
            System.out.println("Server Started");
            while(true){
                socket = serverSocket.accept();
                x = reception(socket);
                DataOutputStream r = new DataOutputStream(socket.getOutputStream());
                r.writeInt(x);
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            //Log
        }
    }

    private static int reception(Socket socket) throws IOException {
        try {
            ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
            Object object = stream.readObject();
            ServerPort s = GetPort.getPort();
            System.out.println("got port # " + s.getPort());

            if (object instanceof Request) {
                Thread deepThread = getDeepThread((Request) object, s.getS()); // todo fix select port
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

    private static Thread getDeepThread(Request request, ServerSocket s){
        //Thread manager
        //static int?
        if(request instanceof GetTorrentListRequest){
            return new TorrentListThread(s);
        }

        if(request instanceof GetTorrentFileRequest){
            //return new TorrentFileThread(request, port);
        }

        return null;
    }

//    public void run() {
//        while(true) {
//            try {
//                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
//                Socket server = serverSocket.accept();
//
//                //System.out.println("Just connected to " + server.getRemoteSocketAddress());
//                DataInputStream in = new DataInputStream(server.getInputStream());
//                String user_string = in.readUTF();
//                System.out.println("Message received: " + user_string);
//                String hash = DeepHash.getHash(user_string);
//                System.out.println("Message hashed: " + hash);
//
//                DataOutputStream out = new DataOutputStream(server.getOutputStream());
//                out.writeUTF(hash);
//                server.close();
//
//            } catch (SocketTimeoutException s) {
//                System.out.println("Socket timed out!");
//                break;
//            } catch (IOException e) {
//                e.printStackTrace();
//                break;
//            } catch (Exception e){
//                System.out.println("Hash error?");
//                e.printStackTrace();
//                break;
//            }
//        }
//    }

}
