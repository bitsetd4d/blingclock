package d3bug.licensing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtil {
	
	private static final char[] hexChars = {
        '0','1','2','3','4','5','6','7',
        '8','9','a','b','c','d','e','f'};

    public static String SHA(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(s.getBytes(), 0, s.getBytes().length);
            byte[] hash = md.digest();
            StringBuilder sb = new StringBuilder();
            int msb;
            int lsb = 0;
            int i;
            for (i = 0; i < hash.length; i++) {
                msb = ((int)hash[i] & 0x000000FF) / 16;
                lsb = ((int)hash[i] & 0x000000FF) % 16;
                sb.append(hexChars[msb]);
                sb.append(hexChars[lsb]);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}
