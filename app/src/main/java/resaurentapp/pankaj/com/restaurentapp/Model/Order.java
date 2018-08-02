package resaurentapp.pankaj.com.restaurentapp.Model;

public class Order
{
    private String ProductId;
    private String Discount;
    private String ProductName;
    private String Quantity ;
    private String  Price;

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
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

    public String getPrice()
    {
        return Price;
    }

    public void setPrice(String price)
    {
        Price = price;
    }

    public Order(String productId, String discount, String productName, String quantity, String price) {
        ProductId = productId;
        Discount = discount;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
    }

    public Order() {

    }
}
