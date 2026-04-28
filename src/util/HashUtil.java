package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * HashUtil — converts plain-text passwords into SHA-256 hashes.
 * We NEVER store raw passwords in the database. Always store the hash.
 */
public class HashUtil {

    /**
     * Takes a plain password string and returns its SHA-256 hash as hex.
     * Example: "admin123" → "240be518..." (64-char hex string)
     */
    public static String sha256(String password) {
        try {
            // Get SHA-256 digest algorithm
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Compute the hash — returns byte array
            byte[] hashBytes = md.digest(password.getBytes());

            // Convert bytes to hex string (readable format)
            StringBuilder hex = new StringBuilder();
            for (byte b : hashBytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();

        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in Java — this should never happen
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
