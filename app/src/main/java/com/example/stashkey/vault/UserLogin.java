    package com.example.stashkey.vault;

    import java.io.Serializable;

    public class UserLogin implements Serializable{
        private String username;
        private String email;
        private String password;
        private String note;


        public UserLogin(String username, String email, String password, String note){
            this.username = username;
            this.email = email;
            this.password = password;
            this.note = note;
        }

        public String getUsername(){return username;}
        public String getEmail(){return email;}
        public String getPassword(){return password;}
        public String getNote(){return note;}

        public void setUsername(String username){this.username = username;}
        public void setEmail(String email){this.email = email;}
        public void setPassword(String password){this.password = password;}
        public void setNote(String note){this.note = note;}
    }
