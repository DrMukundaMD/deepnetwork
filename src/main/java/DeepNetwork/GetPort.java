package DeepNetwork;

import java.net.ServerSocket;
import java.util.Random;

public class GetPort {
    private static int[] array;
    private static int dead;
    private static int used;
    private static int last;
    private static int min = 6000;
    private static int max = 7000;

    public GetPort(){
        array = new int[1000];

        for(int i = 0; i < array.length; i++)
            array[i] = i + min;
        dead = array.length;
        used = array.length;
    }
    public static ServerPort getPort() {
        int index, port;
        ServerSocket serverSocket;
        ServerPort ret;
        while (true) {
            try {
                index = get();
                port = array[index];
                used(index);
                serverSocket = new ServerSocket(port);
                ret = new ServerPort(port, serverSocket);
                return ret;
            } catch (Exception e) {
                dead();
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
        dead--; //todo maybe make this decrement after so that we're always pointing to a valid element?
        swap(index, dead);
    }

    private static void dead(){
        if(dead == 0) {
            dead = array.length;
            System.out.println("Out of available ports");
        }
        dead--;
        swap(last,dead);
    }

    private static void used(int index){

        used--;
        swap(index,used);
    }

    private static int get(){
        if(used == 0)
            used = dead - 1;

        Random rand = new Random();
        int index = rand.nextInt(used);
        last = index;
        return index;
    }
}