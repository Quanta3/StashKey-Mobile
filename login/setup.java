package StashKey.login;

import StashKey.Crypt.Hash;

/* Setup Class will be called to setup the:
 * Master Password
 * Connect Google Account
 * Set Up Biometric
 */
 
 /* Only One Instance of This Class Should be Created - Single User App */
 
 /* Internal Method Setting Up Persistent Master Password in the device storage in encrypted format
 *  Encryptor 
 */

//  Instace To Be Created Inside Try-Catch Block

public class setup{

    public setup(String MasterPassword){
        
        if(count >= 1){
            throw new Error("USER ALREADY EXIST");
        }
        //Encryptor To Be Implemented
        EncryptedMasterPassword = encrypt(MasterPassword);
        
        //To Be implemented in Android Studio
        savePassword(EncryptedMasterPassword);
        
    }
    
    
    public changeMasterPassword(String currentPassword, String NewPassword){

        //GetTheList
        //Decrypt It Using Current Password
        //Encrypt It with New Password
        //Save The List

        EncryptedMasterPassword = encrypt(Password);
        savePassword(EncryptedMasterPassword);
    }



}