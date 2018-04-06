import DeepNetwork.DeepHash;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateTorrentFile {

    public static void create(File file) {

        int count = 0;
        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];
        List<byte[]> file_segments = new ArrayList<>();
        List<String> file_hashes = new ArrayList<>();
        String file_name = file.getName();


        // Open file and read
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            // While file is not empty, save segment and segment hash
            while ((bStream.read(buffer)) > 0) {
                file_segments.add(buffer);
                file_hashes.add(DeepHash.getHash(buffer));
                ++count;
            }

            // Close file
            inputStream.close();
            bStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Create output strings
        StringBuilder sb = new StringBuilder();
        int i = 0;

        sb.append(count).append(" ").append(file.getName()).append("\n");
        for (String s: file_hashes){
            sb.append(i++).append(" ").append(s).append("\n");
        }
        String tOutput = sb.toString();

        // Create .torrent file
        File torrent_file;
        //todo may have to fix this for files with dots without extentions
//        int index = file_name.lastIndexOf(".");
//        if (index != -1) {
//            file_name = file_name.substring(0, index);
//        }

        torrent_file = new File(TorrentFolder.getTorrents(), file_name + ".txt");
        write(torrent_file, tOutput);

        //make segment directory and files
        File segment_folder = new File(TorrentFolder.getSegments(), file_name);
        segment_folder.delete();
        segment_folder.mkdir();

        i = 0;
        for(byte[] segment: file_segments){
            file = new File(segment_folder, Integer.toString(i++));
            write(file, segment);
        }
    }

    private static void write(File file, String output){
        try (FileOutputStream output_stream = new FileOutputStream(file);
             BufferedOutputStream bOutStream = new BufferedOutputStream(output_stream)) {

            bOutStream.write(output.getBytes());

            bOutStream.close();
            output_stream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void write(File file, byte[] output){
        try (FileOutputStream output_stream = new FileOutputStream(file);
             BufferedOutputStream bOutStream = new BufferedOutputStream(output_stream)) {

            bOutStream.write(output);

            bOutStream.close();
            output_stream.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}