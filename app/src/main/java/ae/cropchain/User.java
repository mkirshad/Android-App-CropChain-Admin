package ae.cropchain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by biome on 3/17/2018.
 */

public class User {

    long Id;
    String Name;
    String Email;
    String Password;

    public User(){
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) { this.Email = email; }

    public String getPassword(){ return Password;}
    public void setPassword(String password){ Password = password;}
}