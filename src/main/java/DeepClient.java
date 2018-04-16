/*-----------------------------*
	04/01/2018
	Mukunda Mensah
	Dr. Linda Null
	COMP 512
-------------------------------*/

import DeepManager.ClientStartup;
import DeepManager.DeepManager;
import DeepNetwork.*;
import DeepThread.DeepLogger;

import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

class DeepClient {

    private static final int port = 6345;
    private static BlockingQueue<Request> fromUI;
    private static BlockingQueue<Response> toUI;
    private static ArrayList<String> torrents;
    private static ArrayList<String> active;
    private static ArrayList<String> done;

    public static void main(String[] args) {
        //String serverName = args[0];
        //String serverName = "ada";

        //~ Client StartUp ~
        ClientStartup.main(null);
        torrents = new ArrayList<>();
        done = new ArrayList<>();
        //CLS.main();

        fromUI = new LinkedBlockingDeque<>();
        toUI = new LinkedBlockingDeque<>();
        DeepManager DM = DeepManager.getInstance(fromUI,toUI);

        DM.start();

        System.out.println("~DeepClient started~");

        menu();

    }

    private static void menu(){
        int user = 1;
        String display = "\t\t~Main Menu~\n" +
                "\t\t~~~~~~~~~~~\n" +
                "\t\t1 - Show Torrent List\n" +
                "\t\t2 - Request new Torrent List\n" +
                "\t\t3 - Done Torrents\n" +
                "\t\t0 - Exit\n";
        //todo

        while(user !=0){
            clear();
            System.out.println(display);
            Scanner reader = new Scanner(System.in);
            user = reader.nextInt();
            switch (user){
                case 1:
                    torrentList();
                    break;
                case 2:
                    getList();
                    break;
                case 3:
                    doneTorrents();
                    break;
                case 0:
                    exit();
                    break;
                default:
                    unknownInput();
            }
        }
    }

    private static void torrentList(){
        update();

        StringBuilder display = new StringBuilder("\t\t~Torrent List~\n" +
                "\t\t~~~~~~~~~~~\n");
        for(int i = 0; i < torrents.size(); ++i)
            display.append("\t\t").append(i+1).append(" - ").append(torrents.get(i)).append("\n");
        display.append("\t\t0 - Exit\n");

        int user = 1;
        while(user != 0) {//todo clean this up
            clear();
            System.out.println(display);
            Scanner reader = new Scanner(System.in);
            user = reader.nextInt();
            if(user > 0 && user <= torrents.size()){
                String file = torrents.get(user-1);
                System.out.println("\t\t~ " + user + " - " + file+ " requested ~");
                fromUI.add(new GetTorrentFileRequest(file));

            }else if (user != 0){
                System.out.println("Invalid input");
            }
        }
    }

    private static void doneTorrents(){
        update();

        StringBuilder display = new StringBuilder("\t\t~Completed Torrent List~\n" +
                "\t\t~~~~~~~~~~~\n");
        for(int i = 0; i < done.size(); ++i)
            display.append("\t\t").append(i+1).append(" - ").append(done.get(i)).append("\n");
        display.append("\t\t0 - Exit\n");

        System.out.println(display);
        int user;
        Scanner reader = new Scanner(System.in);
        user = reader.nextInt();
    }

    private static void getList(){
        fromUI.add(new GetTorrentListRequest());
    }

    private static void update(){
        Response r = toUI.poll();

        if(r != null){
            if(r instanceof GetTorrentListResponse){
                torrents = ((GetTorrentListResponse) r).getFiles();
            }

            if(r instanceof GetFilePieceResponse){
                done.add(((GetFilePieceResponse) r).getFile());
            }
        }
    }

    private static void exit(){
        fromUI.add(new ShutDownRequest());
        String display = "\t\t~Thank you for using DeepTorrent~\n";
        clear();
        System.out.println(display);
    }

    private static void unknownInput(){
        String display = "\t\t~!Unknown input!~\n";
        clear();
        System.out.println(display);
    }

    private static void clear(){
        try {
            Runtime.getRuntime().exec("clear");
            //Runtime.getRuntime().exec("cls");
        } catch (IOException e){
            DeepLogger.log(e.getMessage());
        }
    }
}