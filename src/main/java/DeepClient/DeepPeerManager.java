package DeepClient;

import DeepThread.DeepLogger;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class DeepPeerManager {
    private ArrayList<String> array;
    private ArrayList<String> priority;
    private int p;
    private int dead;
    private int used;

    DeepPeerManager(){
        array = new ArrayList<>();
        priority = null;
    }

    public void setPeers(ArrayList<String> peers){
        array = peers;
        used = array.size();
        dead = array.size();
    }

    public String getPeer(){
        String peer = getPriorityPeer();

        if(peer != null)
            return peer;

        return getRandomPeer();
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

    public boolean checkPeers(String webserver){

        try {
            String host = InetAddress.getLocalHost().getHostName();
            if(array.remove(host))
                DeepLogger.log("Cannot download something from yourself");

        } catch (IOException e){
            DeepLogger.log(e.getMessage());
        }

        return array.size() == 1 && array.contains(webserver);
    }

    public void dead(String host){
        if(dead != 0) {
            int index = array.indexOf(host);
            if(index < dead) {
                dead--;
                swap(index, dead);
            } else
                DeepLogger.log("Host already dead");
        } else
            DeepLogger.log("Out of peers");
    }

    private String swap(int x, int y){
        String temp = array.set(y, array.get(x));
        return array.set(x, temp);
    }

    private String getRandomPeer(){
        if(used == 0)
            used = dead;

        Random rand = new Random();
        int index = rand.nextInt(used);
        used--;
        return swap(index,used);
    }

    private String getPriorityPeer(){
        if(priority.size() == 0)
            priority = null;

        if(priority == null)
            return null;

        if(p <= 0)
            p = priority.size();

        p--;
        return priority.get(p);
    }

}
