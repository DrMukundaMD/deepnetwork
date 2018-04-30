package DeepClient;

import DeepThread.DeepLogger;

import java.util.*;

public class DeepPeerManager {
    private ArrayList<String> array;
    private ArrayList<String> priority;
    private int p;
    private int dead;
    private int used;
    private int last;

    DeepPeerManager(){
        array = new ArrayList<>();
        priority = new ArrayList<>();
    }

    public void setPeers(ArrayList<String> peers){
        array = peers;
        used = array.size();
        dead = array.size();
    }

    public String getPeer(){
        if(priority != null){
            if(p == 0)
                p = priority.size();
            p--;
            return priority.get(p);
        }

        if(used == 0)
            reset();

        Random rand = new Random();
        int pick = rand.nextInt(used);
        return used(pick);
    }

    public List<String> getArray() {
        return array.subList(0,dead);
    }

    public void setPriority(ArrayList<String> priority){
        this.priority = priority;
        p = priority.size();
    }

    public boolean isEmpty(){
        return dead == 0;
    }

    public boolean checkHost(String webserver){
        return array.size() == 1 && array.get(0).equals(webserver);
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
