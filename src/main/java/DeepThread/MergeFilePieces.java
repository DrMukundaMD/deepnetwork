package DeepThread;

import com.google.gson.Gson;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MergeFilePieces {

    // Get file name from torrent server
    public static void merge(String filename){
        // Get segment folder
        File folder = new File(TorrentFolder.getSegments(), filename);

        // If it exists
        if(folder.exists()) {
            File file = new File(TorrentFolder.getDone(), filename);
            // Create file
            try{
                FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);

                int i = 0;
                byte[] test;
                Gson gson = new Gson();
                File segment = new File(folder, Integer.toString(i));

                // While segments exist
                while(segment.exists() && segment.isFile()) {
                    // Open segment and read to channel
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(segment));
                        test = gson.fromJson(br, byte[].class);
                        ByteBuffer b = ByteBuffer.wrap(test);
                        fileChannel.write(b);
                        br.close();
                    } catch (FileNotFoundException e) {
                        DeepLogger.log(e.getMessage());
                        break;
                    }
                    // Iterate file segment
                    segment = new File(folder, Integer.toString(++i));
                }

                fileChannel.close();
            } catch (IOException e){
                DeepLogger.log(e.getMessage());
            }
        } else {
            DeepLogger.log("Torrent not found: " + filename);
            //Server response
        }
    }
}
