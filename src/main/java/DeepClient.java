/*-----------------------------*
	04/01/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/

import DeepManager.ClientStartup;
import DeepManager.DeepManager;
import DeepNetwork.GetTorrentFileRequest;
import DeepNetwork.GetTorrentListRequest;
import DeepNetwork.GetTorrentListResponse;
import DeepNetwork.Request;
import DeepThread.DeepLogger;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

class DeepClient {

    private static final int port = 6345;
    public static BlockingQueue<String> UIQueue;

    public static void main(String[] args) {
        //String serverName = args[0];
        String serverName = "ada";

        UIQueue = new LinkedBlockingDeque<>();

        DeepManager DM = new DeepManager(true,UIQueue);
        ClientStartup.main(null);
        System.out.println("~DeepClient started~");
        int user = 1;
        while(user !=0){
            user = menu();

            if(user == 1)
                user = torrentList();

        }

    }

    private static int menu(){
        StringBuilder display = new StringBuilder();

        display.append("\t\t~Main Menu~\n");
        display.append("\t\t~~~~~~~~~~~\n");
        display.append("\t\t1 - Torrent List\n");
        display.append("\t\t0 - Exit\n");

        Scanner reader = new Scanner(System.in);
        System.out.println(display.toString());

        return reader.nextInt(); // Scans the next token of the input
    }

    private static int torrentList(){
        System.out.println("Tested\n");
        return 0;
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