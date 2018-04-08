package DeepThread;

import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class GetSegment {
    public static byte[] getSegment(String filename, int num){
        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));

        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            if((bStream.read(buffer)) > 0) {
                inputStream.close();
                bStream.close();
                return buffer;
            }

        } catch (Exception e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        return null;
    }
}
