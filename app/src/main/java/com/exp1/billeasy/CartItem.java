package com.exp1.billeasy;

public class CartItem {

    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private int availableStock;

    public CartItem(
            int productId,
            String productName,
            double price,
            int quantity,
            int availableStock) {

        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.availableStock = availableStock;
    }

    public int getProductId() {
        return productId;
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

    public int getAvailableStock() {
        return availableStock;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return price * quantity;
    }
}