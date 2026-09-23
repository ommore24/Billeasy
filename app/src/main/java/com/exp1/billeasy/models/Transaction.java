package com.exp1.billeasy.models;

public class Transaction {

    private int id;
    private String customerName;
    private double total;
    private String date;


    public Transaction(
            int id,
            String customerName,
            double total,
            String date) {

        this.id = id;
        this.customerName = customerName;
        this.total = total;
        this.date = date;
    }


    public int getId() {
        return id;
    }


    public String getCustomerName() {
        return customerName;
    }


    public double getTotal() {
        return total;
    }


    public String getDate() {
        return date;
    }
}