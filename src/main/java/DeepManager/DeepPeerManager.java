package DeepManager;

import DeepThread.DeepLogger;

import java.util.ArrayList;
import java.util.Random;

public class DeepPeerManager {
    private ArrayList<String> array;
    private int dead;
    private int used;

    DeepPeerManager(){
        array = new ArrayList<>();
    }

    public void setPeers(ArrayList<String> peers){
        array = peers;
        used = array.size();
        dead = array.size();
    }

    public String getPeer() {
        return get();
    }

    public boolean isEmpty(){
        return dead == 0;
    }

    private String swap(int x, int y){
        String temp = array.set(y, array.get(x));
        return array.set(x, temp);
    }

    private void dead(String d){
        if(dead == 0) {
            DeepLogger.log("Error in DeepPeerManager");
        }

        dead--;
        int index = array.indexOf(d);
        swap(index, dead);
        used(index);
    }

    private String used(int index){
        used--;
        return swap(index,used);
    }

    private String get(){
        if(used == 0)
            used = dead;
        Random rand = new Random();
        int pick = rand.nextInt(used);
        return used(pick);
    }
}
