package DeepManager;

import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

public class DeepTorrentManager {
    private boolean[] segmentFlags;
    private boolean isDone;
    private String filename;
    private int numOfSegments;
    private ArrayList peers;
    private boolean needPeers;

    public DeepTorrentManager(String filename, ArrayList<String> hashes){
        this.numOfSegments = hashes.size();
        this.filename = filename;
        isDone = false;
        needPeers = true;
        segmentFlags = new boolean[numOfSegments];

        for(int i = 0; i < numOfSegments; ++i)
            segmentFlags[i] = false;

        writeT(hashes);
    }

    // -- Segments --

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

    public synchronized int getSegments(){
        for(int i = 0; i < numOfSegments; ++i)
            if(!segmentFlags[i])
                return i;
        return -1;
    }

    // -- Peers --

    public void addPeers(ArrayList<String> peers){
        this.peers = peers;
        needPeers = false;
    }

    public boolean needsPeers(){
        return needPeers;
    }

    // -- Control --

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

    private void writeT(ArrayList<String> torrent){

        Gson gson = new Gson();
        File file = new File(TorrentFolder.getTorrents(), filename);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(torrent, writer);
        }
        catch (Exception e){
            DeepLogger.log(e.getMessage());
        }
    }

}
