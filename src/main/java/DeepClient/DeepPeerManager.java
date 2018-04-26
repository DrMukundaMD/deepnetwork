package DeepClient;

import DeepThread.DeepLogger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

public class DeepPeerManager {
    private ArrayList<String> array;
    private int dead;
    private int used;
    private int last;

    DeepPeerManager(){
        array = new ArrayList<>();
    }

    public void setPeers(ArrayList<String> peers){
        array = peers;
        used = array.size();
        dead = array.size();
    }

    public String getPeer(){
        if(used == 0)
            reset();

        Random rand = new Random();
        int pick = rand.nextInt(used);
        return used(pick);
    }

    public boolean isEmpty(){
        return dead == 0;
    }

    private String swap(int x, int y){
        String temp = array.set(y, array.get(x));
        return array.set(x, temp);
    }

    private void dead(){
        if(dead != 0) {
            dead--;
            swap(last, dead);
        } else
            DeepLogger.log("Error in DeepPeerManager");
    }

    private String used(int index){
        used--;
        last = used;
        return swap(index,used);
    }


    private void reset(){
//        Queue<String> queue = new ArrayDeque<>();
//        for(int i = used; i < dead; i++){
//            queue.add(array.get(i));
//        }
        used = dead;
    }
}
