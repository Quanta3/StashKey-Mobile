package com.example.stashkey.vault;

import java.util.ArrayList;

import com.example.stashkey.crypt.Crypt;
import com.example.stashkey.vault.UserLogin;
import java.io.*; 
/*Methods:
 * Convert ArrayList to Bytes
 * Converting Bytes to ArrayList
 * 
 * 
 */

public class Vault{

    private UserLogins ul = new UserLogins();
    Crypt crypt;

    public Vault(String MasterPassword){
        crypt = new Crypt(MasterPassword);
        // try{
        //     deserialize(data);
        // }
        // catch(Exception e){
        //     e.printStackTrace();
        // }
    }
    //Get ByteStream From File and save in vault
    public void deserialize(byte[] data) throws Exception{
        data = crypt.decrypt(data);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        ul = (UserLogins)is.readObject();
        System.out.println(ul.logins.size());
    }


    //Convert vault to byteStream;
   public  byte[] serialize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(ul);
        return crypt.encrypt(out.toByteArray());
    }

    //Get Element using ID
    public UserLogin getLoginFromId(int id){
        return ul.logins.get(id);
    }

    //Add Element
    public void AddUserLogin(UserLogin login){
        ul.logins.add(login);
    }


    //Delete Element
    public void DeleteUserLogin(int id){
        ul.logins.remove(id);
    }


    //Update Element
    public void UpdateUserLogin(int id, UserLogin login){
        ul.logins.set(id, login);
    }


    //Test Purposes
    public void showAllEntries(){
        for(UserLogin login : ul.logins){
            System.out.println(login.getUsername());
        }
    }

    public int getSize(){
        return ul.logins.size();
    }
    
}

class UserLogins implements Serializable{
    public ArrayList<UserLogin> logins  = new ArrayList<>();
}