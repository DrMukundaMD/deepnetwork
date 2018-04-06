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

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class DeepClient {

    private static final int port = 6345;

    public static void main(String[] args) {
        String serverName = args[0];

        try {
            Scanner reader = new Scanner(System.in);

            System.out.println("Client started");
            Socket client = new Socket(serverName, port);
            System.out.println("Client connected");
            //OutputStream outToServer = client.getOutputStream();
            //DataOutputStream out = new DataOutputStream(outToServer);
            ObjectOutputStream output = new ObjectOutputStream(client.getOutputStream());
            //System.out.println("Enter a message: ");
            //String input = reader.next();
            //System.out.println("Message to be sent to server: " + input);
            //out.writeUTF(input);
            GetTorrentListRequest request = new GetTorrentListRequest();
            output.writeObject(request);
            DataInputStream i = new DataInputStream(client.getInputStream());
            int port1 = i.readInt();
            client.close();
            i.close();
            System.out.println("Received port # "+ port1);
            client = new Socket(serverName, port1);

            ObjectInputStream input = new ObjectInputStream(client.getInputStream());
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
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}