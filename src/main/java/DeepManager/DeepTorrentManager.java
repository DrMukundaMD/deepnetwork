package DeepManager;

import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class DeepTorrentManager {
    private boolean[] segmentFlags;
    private boolean isDone;
    private String filename;
    private int numOfSegments;

    public DeepTorrentManager(int size, String filename){
        this.numOfSegments = size;
        this.filename = filename;
        isDone = false;

        segmentFlags = new boolean[size];

        for(int i = 0; i < size; ++i)
            segmentFlags[i] = false;
    }

    public void addSegment(int num, byte[] segment){
        Gson gson = new Gson();
        File segmentFile = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segmentFile, Integer.toString(num));
        //todo add hash check here
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(segment, writer);
        }
        catch (Exception e){
            e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }
    }

    public byte[] getSegment(int num){

        int buffer_size = 256 * 1024;  // 256KB standardized pieces
        byte[] buffer = new byte[buffer_size];

        File segment = new File(TorrentFolder.getSegments(), filename);
        File file = new File(segment, Integer.toString(num));
        //todo maybe add hash check here for fault tolerance?
        try (FileInputStream inputStream = new FileInputStream(file);
             BufferedInputStream bStream = new BufferedInputStream(inputStream)) {

            if((bStream.read(buffer)) > 0) {
                inputStream.close();
                bStream.close();
                return buffer;
            }

        } catch (Exception e){
            //e.printStackTrace();
            DeepLogger.log(e.getMessage());
        }

        return null;
    }

    public boolean isDone(){return isDone;}

    public boolean check(){
        boolean done = true;

        for(int i = 0; i < numOfSegments; ++i){
            if(!segmentFlags[i])
                done = false;
        }

        if(done){ isDone = true; }

        return isDone;
    }

    public String getFilename() {
        return filename;
    }

    public int getSize() {
        return numOfSegments;
    }
}
