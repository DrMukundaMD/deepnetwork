package DeepThread;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerPeers {
    private HashMap<String,BlockingQueue<ArrayList<String>>> map;

    public ServerPeers() {
        map = new HashMap<>();
    }

    public ArrayList<String> get(String filename){

        ArrayList<String> retVal = null;

        if(map.containsKey(filename)) {
            BlockingQueue<ArrayList<String>> queue = map.get(filename);

            try {
                ArrayList<String> list = queue.take();
                retVal = new ArrayList<>(list);
                queue.offer(list);
            } catch (InterruptedException e){
                DeepLogger.log(e.getMessage());
            }

        } else {
            // create a new one
            BlockingQueue<ArrayList<String>> queue = new LinkedBlockingQueue<>();

            // create an array list for it
            ArrayList<String> list = new ArrayList<>();

            try {
                String host = InetAddress.getLocalHost().getHostName();
                list.add(host);
            } catch (UnknownHostException e) {
                DeepLogger.log(e.getMessage());
            }

            retVal = new ArrayList<>(list);
            queue.offer(list);
            map.put(filename, queue);
        }
            return retVal;
    }

    public void add(String filename, String hostname){
        BlockingQueue<ArrayList<String>> queue = map.get(filename);

        if (queue == null){
            queue = new LinkedBlockingQueue<>();
            ArrayList<String> list = new ArrayList<>();

            list.add(hostname);
            queue.offer(list);
            map.put(filename, queue);

        } else {
            try {
                ArrayList<String> peers = queue.take();

                try {
                    String host = InetAddress.getLocalHost().getHostName();
                    if (peers.contains(host))
                        peers.remove(host);
                } catch (UnknownHostException e) {
                    DeepLogger.log(e.getMessage());
                }

                peers.add(hostname);
                queue.offer(peers);

            } catch (InterruptedException e) {
                DeepLogger.log(e.getMessage());
            }
        }
    }
}
