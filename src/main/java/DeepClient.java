/*-----------------------------*
	04/01/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/

import DeepNetwork.GetTorrentFileRequest;
import DeepNetwork.GetTorrentListRequest;
import DeepNetwork.GetTorrentListResponse;
import DeepNetwork.Request;
import DeepThread.DeepLogger;
import DeepThread.TorrentListThread;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class DeepClient {

    private static final int port = 6345;

    public static void main(String[] args) {
        //String serverName = args[0];
        String serverName = "ada";

        ClientStartup.main(null);

        try {
            System.out.println("~DeepClient started~");

            Socket serverMain = new Socket(serverName, port);

            ObjectOutputStream output = new ObjectOutputStream(serverMain.getOutputStream());

            GetTorrentListRequest request = new GetTorrentListRequest();

            output.writeObject(request);

            DataInputStream i = new DataInputStream(serverMain.getInputStream());

            int port1 = i.readInt();

            serverMain.close();

            i.close();

            System.out.println("Received port # "+ port1);

            serverMain = new Socket(serverName, port1);



            ObjectInputStream input = new ObjectInputStream(serverMain.getInputStream());

            Object object = input.readObject();



            if(object instanceof GetTorrentListResponse){

                ArrayList<String> r = ((GetTorrentListResponse) object).getFiles();

                System.out.println("Response:");

                for(String s: r){

                    System.out.println(s);

                }

            }

            else{ System.out.println("Object error"); }



            //InputStream inFromServer = client.getInputStream();

            //DataInputStream in = new DataInputStream(inFromServer);

            //String hash = DeepHash.getHash(input.getBytes());

            //System.out.println("Hash from input: " + hash);

            //String server_hash = in.readUTF();

            //System.out.println("Hash from server " + server_hash);

            //if (hash.equals(server_hash))

               // System.out.println("Hashs are equal.");

            input.close();
            output.close();
            serverMain.close();
        } catch (Exception e) {
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }

    private static Thread getDeepThread(Request request, ServerSocket s){
        //Thread manager
        //static int?
        if(request instanceof GetTorrentListRequest){
            //return new TorrentListThread(s);
        }

        if(request instanceof GetTorrentFileRequest){
            //return new TorrentFileThread(request, port);
        }

        return null;
    }
}