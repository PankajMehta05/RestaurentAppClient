package resaurentapp.pankaj.com.restaurentapp.Model;

import java.util.List;


public class Request {


    private String phone;
    private String name;
    private String address;
    private String total;
    private  String status;
    private  String comment;
    private String paymentMetod;
    private String paymentState;
    private  String latlng;
    private List<Order> foods;



   // public Request(String phone, String name, String address, String s, String total, String comment, String string, String format, List<Order> cart) {
    //}


    public Request(String phone, String name, String address, String total, String status, String comment, String paymentMetod, String paymentState, String latlng, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.paymentMetod = paymentMetod;
        this.paymentState = paymentState;
        this.latlng = latlng;
        this.foods = foods;
    }

    public Request(String phone, String name, String address, String s, List<Order> carts, String notes, String format, String payPal, String unpaid) {
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPaymentMetod() {
        return paymentMetod;
    }

    public void setPaymentMetod(String paymentMetod) {
        this.paymentMetod = paymentMetod;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getLatlng() {
        return latlng;
    }

    public void setLatlng(String latlng) {
        this.latlng = latlng;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
