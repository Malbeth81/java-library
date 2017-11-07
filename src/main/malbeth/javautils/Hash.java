package malbeth.javautils;

import java.security.MessageDigest;

public class Hash {

    public static final byte[] getHash(String algorithm, String value) {
        return getHash(algorithm, value, "US-ASCII");
    }

    public static final byte[] getHash(String algorithm, String value, String encoding) {
        if (algorithm != null && value != null && encoding != null) {
            try {
                return MessageDigest.getInstance(algorithm).digest(value.getBytes(encoding));
            } catch (Exception e) {
            }
        }
        return null;
    }
}
