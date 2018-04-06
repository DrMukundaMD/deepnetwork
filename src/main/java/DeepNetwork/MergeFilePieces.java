package DeepNetwork;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MergeFilePieces {

    // Get file name from torrent server
    public static void merge(List<byte[]> files, File into) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(into);
             BufferedOutputStream merge = new BufferedOutputStream(fos)) {
            for (byte[] f : files) {
                //Files.copy(f.toPath(), merge);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
