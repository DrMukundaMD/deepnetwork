package DeepNetwork;

import java.net.ServerSocket;
import java.util.Random;

public class GetPort {
    private static int[] array;
    private static int dead;
    private static int used;
    private static int min = 6000;
    //private static int max = 7000;

    public GetPort(){
        array = new int[1000];

        for(int i = 0; i < array.length; i++)
            array[i] = i + min;
        //todo make this randomized based on start time

        dead = array.length;
        used = array.length;
    }

    public static ServerPort getPort() {
        int index, port;
        ServerSocket serverSocket;
        ServerPort ret;
        while (true) {
                index = get();
                port = array[index];
            try {
                serverSocket = new ServerSocket(port);
                used(index);
                ret = new ServerPort(port, serverSocket);
                return ret;
            } catch (Exception e) {
                dead(index);
            }
        }
    }

    private static void swap(int x, int y){
        int temp = array[x];
        array[x] = array[y];
        array[y] = temp;
    }

    private static void dead(int index){
        if(dead == 0) {
            dead = array.length;
            System.out.println("Out of available ports");
        }
        dead--;
        swap(index, dead);
        used(index);
    }

    private static void used(int index){
        if(used == 0)
            used = dead - 1;
        used--;
        swap(index,used);
    }

    private static int get(){
        Random rand = new Random();
        return rand.nextInt(used);
    }
}