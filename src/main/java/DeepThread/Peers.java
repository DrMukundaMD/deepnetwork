package DeepThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Peers {
    private HashMap<String,BlockingQueue<ArrayList<String>>> map;

    public Peers() {
        map = new HashMap<>();
    }

    public BlockingQueue<ArrayList<String>> get(String filename){
        if(map.containsKey(filename)) {
            return map.get(filename);
        }

        BlockingQueue<ArrayList<String>> retVal = new LinkedBlockingQueue<>();

        ArrayList<String> peers = new ArrayList<>();

        try {
            String host = InetAddress.getLocalHost().getHostName();
            peers.add(host);
        }catch (UnknownHostException e){
            DeepLogger.log(e.getMessage());
        }
        retVal.add(peers);



        return retVal;
    }

    public void add(String filename, String hostname){
        BlockingQueue<ArrayList<String>> queue = map.get(filename);

        try {
            ArrayList<String> peers = queue.take();
            try {
                String host = InetAddress.getLocalHost().getHostName();
                if(peers.contains(host))
                    peers.remove(host);
            }catch (UnknownHostException e){
                DeepLogger.log(e.getMessage());
            }

            peers.add(hostname);
            queue.put(peers);

        } catch (InterruptedException e){
            DeepLogger.log(e.getMessage());
        }
    }
}
