package DeepManager;

import DeepNetwork.DeepHash;
import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CreateTorrentFile {

    public static void create(File file) {

        int i = 0;
        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];
        ArrayList<String> file_hashes = new ArrayList<>();

        //make segment directory and files
        File segment_folder = new File(TorrentFolder.getSegments(), file.getName());
        segment_folder.delete();
        segment_folder.mkdir();
        DeepLogger.log(file.toString());
        // Open file and read
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            // While file is not empty, write segment, save hash
            while ((bStream.read(buffer)) > 0) {
                write(new File(segment_folder, Integer.toString(i++)), buffer);
                file_hashes.add(DeepHash.getHash(buffer));
            }

            // Close file
            inputStream.close();
            bStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        // Create .torrent file
        File torrent_file = new File(TorrentFolder.getTorrents(), file.getName());
        write(torrent_file, file_hashes);

    }

    private static void write(File file, ArrayList<String> output){
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(output, writer);
        }
        catch (Exception e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }

    private static void write(File file, byte[] output){
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(output, writer);
        }
        catch (Exception e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }
}