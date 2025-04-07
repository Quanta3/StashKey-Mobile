package StashKey.Crypt;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

public class Crypt {
    private SecretKey key;
    private final byte[] FIXED_IV = {1, 4, 82, 18, 29, 91, 34, -32, 103, -13, 120, -63, 12, 53, 24, 27};

    public Crypt(String password) {
        try {
            key = getKeyFromPassword(password, "3#4k2");  // Use fixed salt
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IvParameterSpec getFixedIv() {
        return new IvParameterSpec(FIXED_IV);
    }

    public  SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public  byte[] encrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, getFixedIv());  // Use fixed IV
        byte[] cipherText = cipher.doFinal(input);
        return cipherText;
    }

    public  byte[] decrypt(byte[] input) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, getFixedIv());  // Use fixed IV
        byte[] plainText = cipher.doFinal(input);
        return plainText;
    }

}
