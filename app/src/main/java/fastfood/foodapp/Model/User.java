package fastfood.foodapp.Model;

/**
 * Created by Sheraz on 12/29/2017.
 */

public class User {
    private  String Name;
    private  String Password,Phone,IsStaff,securecode;

    public User() {
    }



    public User(String name, String password,String securecode) {
        this.Name = name;
        this.Password = password;
        this.securecode=securecode;

        IsStaff="false";

    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getSecurecode() {
        return securecode;
    }

    public void setSecurecode(String securecode) {
        this.securecode = securecode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}

