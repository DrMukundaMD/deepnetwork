package DeepClient;

import DeepNetwork.*;
import DeepThread.DeepLogger;
import DeepThread.TorrentFolder;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DeepClientManager extends Thread implements ClientThreadStuff {
    private HashMap<String, DeepTorrentManager> torrents;
    private BlockingQueue<String> doneQueue;
    private BlockingQueue<Request> fromUI;
    private BlockingQueue<Response> toUI;
    private BlockingQueue<Response> toDM;

    private static DeepClientManager DM;
    private String server;
    private int port;

    private DeepClientManager(BlockingQueue<Request> fromUI, BlockingQueue<Response> toUI) {
        torrents = new HashMap<>();
        doneQueue = new LinkedBlockingQueue<>();
        toDM = new LinkedBlockingQueue<>();
        this.fromUI = fromUI;
        this.toUI = toUI;
        server = "ada";
        port = 6345;
    }

    public static synchronized DeepClientManager getInstance(BlockingQueue<Request> fromUI,
                                                             BlockingQueue<Response> toUI) {
        File file = new File(TorrentFolder.getSegments(), ".dm");

        if (DM == null)
            if(file.exists()){
                Gson gson = new Gson();
                try (FileReader reader = new FileReader(file)) {
                    DM = gson.fromJson(reader, DeepClientManager.class);
                }
                catch (Exception e){
                    e.printStackTrace();
                    DeepLogger.log(e.getMessage());
                }
            } else
                DM = new DeepClientManager(fromUI, toUI);

        return DM;
    }

    @Override
    public void run(){
        System.out.println("~DeepManager Started~");
        boolean on = true;
        while(on){ //user is not ended

                if(fromUI.size() > 0) {
                    Request r = fromUI.poll();

                    // user.request1 (get new torrent list)
                    if (r instanceof GetTorrentListRequest) {
                        DeepLogger.log("Get Torrent List User Request");
                        Thread t = new GetTorrentListThread(toDM, server, port);
                        t.start();
                    }

                    // user.request2 (get torrent)

                    if(r instanceof GetTorrentFileRequest){
                        DeepLogger.log("Get Torrent File User Request");
                        startTorrent(((GetTorrentFileRequest) r).getFilename());
                    }

                    // user shutdown
                    if (r instanceof ShutDownRequest) {
                        for(DeepTorrentManager dtm : torrents.values()){
                            BlockingQueue<Request> q = dtm.getFromDM();
                            q.add(new ShutDownRequest());
                        }
                        on = false;
                        System.out.println("~DeepManager Closed~");
                    }
                }

                if(toDM.size() > 0) {
                    Response r = toDM.poll();

                    if(r instanceof GetTorrentListResponse){
                        toUI.add(r);
                    }else {
                        ArrayList<String> test = new ArrayList<>();
                        test.add("test1.file");
                        test.add("test2.file");
                        test.add("test3.file");
                        toUI.add(new GetTorrentListResponse(test));
                    }
                }

                // check done queue
                while (!doneQueue.isEmpty()) {
                    String s = doneQueue.poll();
                    toUI.offer(new GetFilePieceResponse(s, 0, null));
                }


                try {
                    sleep(5);
                } catch (InterruptedException e){
                    DeepLogger.log(e.getMessage());
                }
            //call back for UI
        }
    }

    @Override
    public synchronized void closeThread(boolean flag, String file){
        //todo
        //if file still needs requesting
        if(flag){
            doneQueue.add(file);
        }

    }
    @Override
    public void contact(String filename){

    }

    private void startTorrent(String filename) {
        if (!torrents.containsKey(filename)) {
            DeepTorrentManager dtm = new DeepTorrentManager(filename, server, port, new LinkedBlockingQueue<>(), this);
            torrents.put(filename, dtm);
            dtm.start();
        }
        else
            DeepLogger.log("DeepManager: startTorrent: Torrent Manager " + filename + " already created.");
    }

}