package DeepClient;

import DeepNetwork.PortResponse;
import DeepNetwork.Request;
import DeepServer.ServerStartup;
import DeepThread.DeepLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class DeepClientServer extends Thread{
//    private transient BlockingQueue<Request> fromDM;
//    private static final int PORT = 6752;
    private ServerSocket serverSocket;

    DeepClientServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void run() {

        ServerStartup.main(null);

        try {
//            serverSocket = new ServerSocket(PORT);
            //serverSocket.setSoTimeout(10000); //this is 10 seconds
            PortResponse newPort;
            Socket socket;
            DeepClientServerManager manager = new DeepClientServerManager(40);
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
//            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
        DeepLogger.log("~DeepClientServer Closed~");
    }
}
