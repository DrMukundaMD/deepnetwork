package DeepManager;

import DeepThread.DeepLogger;

import java.util.ArrayList;
import java.util.Random;

public class DeepPeerManager {
    private static ArrayList<String> array;
    private static int dead;
    private static int used;

    DeepPeerManager(){
        array = new ArrayList<>();
    }

    public void setPeers(ArrayList<String> peers){
        array = peers;
        used = array.size();
        dead = array.size();
    }

    public String getPeer() {
        if(used == 0)
            return null;
        return get();
    }

    public boolean isEmpty(){
        return dead == 0;
    }

    private static String swap(int x, int y){
        String temp = array.set(y, array.get(x));
        return array.set(x, temp);
    }

    private static void dead(String d){
        if(dead == 0) {
            DeepLogger.log("Error in DeepPeerManager");
        }

        dead--;
        int index = array.indexOf(d);
        swap(index, dead);
        used(index);
    }

    private static String used(int index){
        if(used == 0)
            used = dead;
        used--;
        return swap(index,used);
    }

    private static String get(){
        Random rand = new Random();
        int pick = rand.nextInt(used);
        return used(pick);
    }
}
