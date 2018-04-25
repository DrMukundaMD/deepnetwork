package DeepClient;

import DeepNetwork.PortResponse;
import DeepServer.DeepServerManager;
import DeepServer.ServerStartup;
import DeepThread.DeepLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DeepClientServer {
    private static final int PORT = 6752;

    public static void main(String [] args) {

        ServerStartup.main(null);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            //serverSocket.setSoTimeout(10000); //this is 10 seconds
            PortResponse newPort;
            Socket socket;
            DeepClientServerManager manager = new DeepClientServerManager(20);
            DeepLogger.log("~DeepClientServer Started~");

            while(true){
                // accept
                socket = serverSocket.accept();

                try {
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

                    Object object = input.readObject();

                    newPort = manager.reception(object);

                    if(newPort.getPort() != -1) {
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(newPort);
                        output.close();
                        DeepLogger.log("Sending new port # " + newPort.getPort());
                    }

                    input.close();

                } catch (ClassNotFoundException e){
                    DeepLogger.log(e.getMessage());
                }

                // reset
                socket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }
}
