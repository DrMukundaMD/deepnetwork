package DeepNetwork;

import DeepThread.DeepLogger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DeepHash {

    public static String getHash(byte[] toHash) {

        byte[] hash_data;
        StringBuilder output = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(toHash);
            hash_data = digest.digest();
            //write number to buffer in hex
            for (byte b: hash_data) {
                output.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e){
            DeepLogger.log(e.getMessage());
        }

        return output.toString();
    }

    public static boolean compareHash(byte[] toHash, String hash){
        return hash.equals(getHash(toHash));
    }

}
