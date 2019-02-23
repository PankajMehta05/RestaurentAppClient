package resaurentapp.pankaj.com.restaurentapp.Model;

//public class User {
//
//    private String Name;
//    private String Password;
//
//    public String getIsStaff() {
//        return isStaff;
//    }
//
//    public void setIsStaff(String isStaff) {
//        this.isStaff = isStaff;
//    }
//
//    private  String isStaff;
//
//    public String getPhone() {
//        return Phone;
//    }
//
//
//    private String Phone;
//
//public User()
//{
//
//}
//
//    public User(String name, String password) {
//        Name = name;
//        Password = password;
//        isStaff="false";
//
//    }
//    public void setPhone(String phone) {
//        Phone = phone;
//    }
//
//    public String getName() {
//        return Name;
//    }
//
//    public void setName(String name) {
//        Name = name;
//    }
//
//    public String getPassword() {
//        return Password;
//    }
//
//    public void setPassword(String password) {
//        Password = password;
//    }
//}
public class User {
    private String Name;
    private String Password;
    private  String Phone;
    private String IsStaff;
    private  String secureCode;
    private String homeAddress;
    private double balance;

    public User() {
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;

        IsStaff ="false";
        this.secureCode=secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

//    public void setPhone(String phone) {
//        Phone = phone;
//    }
//
//    public String getPhone() {
//        return Phone;
//    }

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

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }
}


