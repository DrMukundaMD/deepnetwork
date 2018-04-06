import DeepNetwork.GetPeersResponse;

import java.util.ArrayList;
import java.util.HashMap;

public class Peers {
    private static HashMap<String,ArrayList<String>> map;

    Peers() {
        map = new HashMap<>();
    }

    public static ArrayList<String> getPeers(String file){
        return map.get(file);
    }

    public static void add(String filename, String hostname){
        ArrayList<String> list = map.get(filename);
        if(list == null)
            list = new ArrayList<>();
        list.add(hostname);
    }

    public static GetPeersResponse get(String filename){
        return new GetPeersResponse(filename, map.get(filename));
    }
}
