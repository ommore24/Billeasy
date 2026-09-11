package com.exp1.billeasy;

public class Bill {

    private int id;
    private String customerName;
    private double subtotal;
    private double discount;
    private double gst;
    private double total;
    private String date;

    public Bill(
            int id,
            String customerName,
            double subtotal,
            double discount,
            double gst,
            double total,
            String date) {

        this.id = id;
        this.customerName = customerName;
        this.subtotal = subtotal;
        this.discount = discount;
        this.gst = gst;
        this.total = total;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getGst() {
        return gst;
    }

    public double getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }
}