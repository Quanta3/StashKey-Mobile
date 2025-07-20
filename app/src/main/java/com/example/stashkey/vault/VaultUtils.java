package com.example.stashkey.vault;

import android.content.Context;

import java.io.*;
public class VaultUtils {
    public static String hashUserKey(String username, String password) {
        String combined = username + ":" + password;
        return String.valueOf(combined.hashCode()); // Optional: use SHA-256 for better uniqueness
    }

    public static File getVaultFile(Context context, String username, String password) {
        String hashedName = hashUserKey(username, password);
        return new File(context.getFilesDir(), hashedName + ".vault");
    }

    public static void saveVaultToFile(Context context, File file, byte[] data) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] readVaultFromFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}
