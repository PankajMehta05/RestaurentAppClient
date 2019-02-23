package resaurentapp.pankaj.com.restaurentapp.Model;

public class Order
{

    private int ID ;
    private String ProductID;
    private String ProductName;
    private String Quantity ;
    private String  Price;
    private  String Discount;
    private String Image;

    public Order(String image, String productID, String productName, String string, String quantity, String price, String discount, int id) {
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public Order() {
    }

    public Order(String discount, String productName, String productID, String image, String quantity, String price, int ID) {
        this.ID = ID;
        Image = image;
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }

    public Order(String image, String productID, String productName, String quantity, String price, String discount) {
        Image = image;
        ProductID = productID;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
    }


    public void setID(int ID) {
        this.ID = ID;
    }

    public  int getID()
{
    return ID;

}

    public Order(String productID, String productName, String quantity, String price, String discount, int id, String image) {
    }


}
