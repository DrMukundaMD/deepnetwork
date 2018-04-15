import DeepThread.DeepLogger;

import java.io.IOException;

public class CLS {
    public static void main(){
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException e){
            DeepLogger.log(e.getMessage());
        }
    }
}