package DeepThread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class MergeFilePieces {

    // Get file name from torrent server
    public static void merge(ArrayList<byte[]> files, String filename){

        File file = new File(TorrentFolder.getDone(), filename);

        try{
            FileChannel fileChannel = FileChannel.open(file.toPath(), StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE, StandardOpenOption.APPEND);

            for (byte[] f : files){
                ByteBuffer b = ByteBuffer.wrap(f);
                fileChannel.write(b);
            }

        } catch (IOException e){
            DeepLogger.log(e.getMessage());
        }
//        try (FileOutputStream fos = new FileOutputStream(file);
//             BufferedOutputStream merge = new BufferedOutputStream(fos)) {
//            for (byte[] f : files) {
//                merge.copy(f.toPath(), merge);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
