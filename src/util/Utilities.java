package util;

import java.security.MessageDigest;

/**
 * @class Utilities
 * @desc this class provides static functions available to each class that needs to use them.
 */

public class Utilities {

    public static String sha256(char[] toEncode) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(new String(toEncode).getBytes());
            byte[] hash = md.digest();
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
