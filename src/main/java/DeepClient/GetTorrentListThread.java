package DeepClient;

import DeepNetwork.GetTorrentListRequest;
import DeepNetwork.GetTorrentListResponse;
import DeepNetwork.PortResponse;
import DeepNetwork.Response;
import DeepThread.DeepLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class GetTorrentListThread extends Thread{
    private String host;
    private int port;
    private BlockingQueue<Response> toDM;

    GetTorrentListThread(BlockingQueue<Response> toDM, String host, int port){
        this.host = host;
        this.port = port;
        this.toDM = toDM;
    }

    @Override
    public void run() {
        System.out.println("~GetTorrentListTread started~");
        // create request
        GetTorrentListRequest request = new GetTorrentListRequest();
        try {
            // create stuff
            Socket serverMain = new Socket(host, port);
            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());
            DeepLogger.log("Writing request");
            output.writeObject(request);

            // write request & get response
            ObjectInputStream input = new ObjectInputStream(serverMain.getInputStream());
            DeepLogger.log("Getting port object");
            Object newPort = input.readObject();

            // close socket
            input.close();
            output.close();
            serverMain.close();

            // get new port
            PortResponse pr;

            if (newPort instanceof PortResponse) {
                pr = (PortResponse) newPort;
                if (pr.getPort() != 0) {
                    DeepLogger.log("Getting new socket");
                    serverMain = new Socket(host, pr.getPort());
                }
            }
            input = new ObjectInputStream(serverMain.getInputStream());
            Object obj = input.readObject();

            if(obj instanceof GetTorrentListResponse){
                Response r = (Response) obj;
                toDM.add(r);
            }

        } catch (IOException | ClassNotFoundException e){
            DeepLogger.log(e.getMessage());
        }
        System.out.println("~GetTorrentListTread closed~");
    }
}
