package DeepThread;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class DeepLogger {
    private static final Logger LOGGER = Logger.getLogger("DeepLogger");
    private static File logs;

    public DeepLogger(Long time){
        logs = new File(".logs");
        if(logs.exists())
            logs.mkdir();
        try{
            FileHandler fileHandler = new FileHandler(logs.toString() + time + "_log.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.FINE);
        } catch (IOException e){
            System.out.println("Unable to start DeepLogger");
            e.printStackTrace();
        }
    }

    public static void log(String message){
        LOGGER.log(Level.FINE, message + System.lineSeparator());
    }
}
