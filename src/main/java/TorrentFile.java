import DeepNetwork.GetTorrentFileResponse;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TorrentFile {

    public static GetTorrentFileResponse get(String filename){
        File file = new File(TorrentFolder.getTorrents(), filename);
        if(!file.exists())
            return new GetTorrentFileResponse(filename,null);
        return new GetTorrentFileResponse(filename, get(file));
    }

    private static ArrayList<String> get(File file){
        Gson gson = new Gson();
        ArrayList<String> response = null;

        try (FileReader reader = new FileReader(file)){
            response = gson.fromJson(reader, ArrayList.class);

        }catch (IOException e ){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        return response;
    }
}
