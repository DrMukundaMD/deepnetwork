package DeepNetwork;

import java.security.MessageDigest;

public class DeepHash {

    public static String getHash(byte[] toHash) throws Exception {

        StringBuffer output = new StringBuffer();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(toHash);

        byte[] hash_data = digest.digest();

        //write number to buffer in hex
        for (int i = 0; i < hash_data.length; i++) {
            output.append(Integer.toString((hash_data[i] & 0xff) + 0x100, 16).substring(1));
        }

        return output.toString();

    }

    public static boolean compareHash(byte[] to_hash, String hash) throws Exception {

        StringBuffer output = new StringBuffer();
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(to_hash);

        byte hash_data[] = digest.digest();

        //write number to buffer in hex
        for (int i = 0; i < hash_data.length; i++) {
            output.append(Integer.toString((hash_data[i] & 0xff) + 0x100, 16).substring(1));
        }

        return output.toString().equals(hash);
    }

}
