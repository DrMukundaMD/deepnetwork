package DeepThread;

import DeepNetwork.Log;
import DeepNetwork.Response;

public class LogThread extends Thread{
    private Log response;

    public LogThread( Response response){
        this.response = (Log) response;
    }

    @Override
    public void run(){
        DeepLogger.log(response.getLog());
    }
}