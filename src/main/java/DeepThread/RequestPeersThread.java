package DeepThread;

import DeepManager.DeepTorrentManager;
import DeepManager.ThreadStuff;
import DeepNetwork.GetPeersRequest;
import DeepNetwork.GetPeersResponse;
import DeepNetwork.UnknownRequestResponse;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestPeersThread extends Thread {
    private ThreadStuff DM;
    private DeepTorrentManager dtm;
    private String server;
    private int port;

    public RequestPeersThread(ThreadStuff DM, DeepTorrentManager dtm, String server, int port){
        this.DM = DM;
        this.dtm = dtm;
        this.server = server;
        this.port = port;
    }


    @Override
    public void run(){
        try{
            // create stuff
            Socket serverMain = new Socket(server, port);
            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());
            GetPeersRequest request = new GetPeersRequest(dtm.getFilename());

            // write request
            output.writeObject(request);

            // get new port
            DataInputStream input = new DataInputStream(serverMain.getInputStream());
            int newPort = input.readInt();

            // close socket
            input.close();
            output.close();
            serverMain.close();

            // open connection on new port
            serverMain = new Socket(server, newPort);

            // get response
            ObjectInputStream stream = new ObjectInputStream(serverMain.getInputStream());

            try {
                Object response = stream.readObject();

                if(response instanceof GetPeersResponse){
                    dtm.addPeers(((GetPeersResponse) response).getPeers());
                }

                if(response instanceof UnknownRequestResponse){

                }
            }catch (ClassNotFoundException e){
                DeepLogger.log(e.getMessage());
            }

            //Close
            stream.close();
            serverMain.close();

        } catch (IOException e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        DM.closeThread();
    }
}
