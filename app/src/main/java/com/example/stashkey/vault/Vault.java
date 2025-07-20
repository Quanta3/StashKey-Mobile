package com.example.stashkey.vault;

import java.util.ArrayList;

import com.example.stashkey.crypt.Crypt;
import com.example.stashkey.vault.Item;
import java.io.*; 
/*Methods:
 * Convert ArrayList to Bytes
 * Converting Bytes to ArrayList
 * 
 * 
 */

public class Vault {
    private ItemList ul = new ItemList();
    private Crypt crypt;

    public Vault(String password) {
        crypt = new Crypt(password);
    }

    public void deserialize(byte[] data) throws Exception {
        data = crypt.decrypt(data);
        try (ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(data))) {
            ul = (ItemList) is.readObject();
        }
    }

    public byte[] serialize() throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream os = new ObjectOutputStream(out)) {
            os.writeObject(ul);
        }
        return crypt.encrypt(out.toByteArray());
    }

    public Item getLoginFromId(int id) {
        return ul.logins.get(id);
    }

    public void AddUserLogin(Item login) {
        ul.logins.add(login);
    }

    public void DeleteUserLogin(int id) {
        ul.logins.remove(id);
    }

    public void UpdateUserLogin(int id, Item login) {
        ul.logins.set(id, login);
    }

    public int getSize() {
        return ul.logins.size();
    }
}

class ItemList implements Serializable {
    public ArrayList<Item> logins = new ArrayList<>();
}
