package com.exp1.billeasy;

public class BillItem {

    private int id;
    private String name;
    private double price;
    private int quantity;
    private double total;


    public BillItem(
            int id,
            String name,
            double price,
            int quantity,
            double total) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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