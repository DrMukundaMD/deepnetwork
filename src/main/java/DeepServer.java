/*-----------------------------*
	04/10/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/

import DeepThread.*;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

class DeepServer {
    private static final int PORT = 6345;

    public static void main(String [] args) {

        ServerStartup.main(null);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            //serverSocket.setSoTimeout(10000); //this is 10 seconds
            int newPort;
            Socket socket;
            DeepThreadManager manager = new DeepThreadManager(100);
            System.out.println("~DeepServer Started~");

            while(true){
                socket = serverSocket.accept();

                try {
                    ObjectInputStream stream = new ObjectInputStream(socket.getInputStream());
                    Object object = stream.readObject();
                    newPort = manager.reception(object);

                    if(newPort != -1) {
                        DataOutputStream r = new DataOutputStream(socket.getOutputStream());
                        r.writeInt(newPort);
                    }

                } catch (ClassNotFoundException e){
                    DeepLogger.log(e.getMessage());
                }

                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }
}
