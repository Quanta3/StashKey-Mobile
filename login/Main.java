package StashKey.login;

import StashKey.Vault.Vault;
import StashKey.Vault.UserLogin;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import StashKey.Crypt.Crypt;


public class Main{
    public static void main(String[] args){
        UserLogin usr1 = new UserLogin("us1", "us@g.com", "us", "");
        UserLogin usr2 = new UserLogin("us2", "us2@g.com", "uspass", "");


        Vault vault = new Vault("Password");
        System.out.println("Here");

        vault.AddUserLogin(usr1);
        //vault.AddUserLogin(usr2);
        
        vault.showAllEntries();

        byte[] byteArray = {0,2,1};
        try{
            byteArray = vault.serialize();
            System.out.println(byteArray.length);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        try (FileOutputStream fos = new FileOutputStream("./file.txt")) {
            fos.write(byteArray);
            //fos.close(); There is no more need for this line since you had created the instance of "fos" inside the try. And this will automatically close the OutputStream
        }catch(Exception e){
            e.printStackTrace();
        }

        // File file;
        // byte[] fileContent;

        // try{
        //     fileContent = Files.readAllBytes(Paths.get("./file.txt"));
        //     System.out.println(fileContent.length);
        //     Vault.deserialize(fileContent);
        // }
        // catch(Exception e){
        //     e.printStackTrace();
        // }

        vault.showAllEntries();

        System.out.println("Hello");
    }
}