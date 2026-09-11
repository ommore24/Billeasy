package com.exp1.billeasy;

public class BillItem {

    private int id;
    private String productName;
    private double price;
    private int quantity;
    private double total;

    public BillItem(
            int id,
            String productName,
            double price,
            int quantity,
            double total) {

        this.id = id;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
}