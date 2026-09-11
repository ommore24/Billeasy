package com.exp1.billeasy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewBillActivity extends AppCompatActivity {

    private TextInputEditText etCustomerName;
    private TextInputEditText etDiscount;
    private TextInputEditText etGst;

    private TextView tvSubtotal;
    private TextView tvDiscount;
    private TextView tvGst;
    private TextView tvGrandTotal;

    private RecyclerView recyclerProducts;
    private RecyclerView recyclerCart;

    private MaterialButton btnSaveBill;

    private DatabaseHelper databaseHelper;

    private ArrayList<Product> productList;
    private ArrayList<CartItem> cartList;

    private ProductBillingAdapter productAdapter;
    private CartAdapter cartAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_bill);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        setupBackButton();

        setupProducts();

        setupCart();

        setupInputListeners();

        calculateBill();
    }


    // -----------------------------------------
    // INITIALIZE VIEWS
    // -----------------------------------------

    private void initializeViews() {

        etCustomerName =
                findViewById(R.id.etCustomerName);

        etDiscount =
                findViewById(R.id.etDiscount);

        etGst =
                findViewById(R.id.etGst);

        tvSubtotal =
                findViewById(R.id.tvSubtotal);

        tvDiscount =
                findViewById(R.id.tvDiscount);

        tvGst =
                findViewById(R.id.tvGst);

        tvGrandTotal =
                findViewById(R.id.tvGrandTotal);

        recyclerProducts =
                findViewById(R.id.recyclerProducts);

        recyclerCart =
                findViewById(R.id.recyclerCart);

        btnSaveBill =
                findViewById(R.id.btnSaveBill);
    }


    // -----------------------------------------
    // BACK BUTTON
    // -----------------------------------------

    private void setupBackButton() {

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }


    // -----------------------------------------
    // PRODUCTS
    // -----------------------------------------

    private void setupProducts() {

        productList =
                databaseHelper.getAllProducts();

        recyclerProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        productAdapter =
                new ProductBillingAdapter(
                        this,
                        productList,
                        this::addProductToCart
                );

        recyclerProducts.setAdapter(productAdapter);
    }


    // -----------------------------------------
    // CART
    // -----------------------------------------

    private void setupCart() {

        cartList = new ArrayList<>();

        recyclerCart.setLayoutManager(
                new LinearLayoutManager(this)
        );

        cartAdapter =
                new CartAdapter(
                        cartList,
                        new CartAdapter.CartListener() {

                            @Override
                            public void onQuantityChanged() {
                                calculateBill();
                            }

                            @Override
                            public void onRemove(int position) {

                                if (position >= 0 &&
                                        position < cartList.size()) {

                                    cartList.remove(position);

                                    cartAdapter.notifyItemRemoved(
                                            position
                                    );

                                    calculateBill();
                                }
                            }
                        }
                );

        recyclerCart.setAdapter(cartAdapter);
    }


    // -----------------------------------------
    // ADD PRODUCT TO CART
    // -----------------------------------------

    private void addProductToCart(Product product) {

        if (product.getQuantity() <= 0) {

            Toast.makeText(
                    this,
                    "Product is out of stock",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Check if already in cart

        for (CartItem item : cartList) {

            if (item.getProductId() == product.getId()) {

                if (item.getQuantity() <
                        item.getAvailableStock()) {

                    item.setQuantity(
                            item.getQuantity() + 1
                    );

                    cartAdapter.notifyDataSetChanged();

                    calculateBill();

                } else {

                    Toast.makeText(
                            this,
                            "Maximum available stock reached",
                            Toast.LENGTH_SHORT
                    ).show();
                }

                return;
            }
        }


        // Add new item

        CartItem cartItem =
                new CartItem(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        1,
                        product.getQuantity()
                );

        cartList.add(cartItem);

        cartAdapter.notifyItemInserted(
                cartList.size() - 1
        );

        calculateBill();
    }


    // -----------------------------------------
    // INPUT LISTENERS
    // -----------------------------------------

    private void setupInputListeners() {

        etDiscount.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus) {
                        calculateBill();
                    }
                }
        );

        etGst.setOnFocusChangeListener(
                (v, hasFocus) -> {

                    if (!hasFocus) {
                        calculateBill();
                    }
                }
        );


        btnSaveBill.setOnClickListener(v -> {

            calculateBill();

            saveBill();
        });
    }


    // -----------------------------------------
    // CALCULATE BILL
    // -----------------------------------------

    private void calculateBill() {

        double subtotal = 0;

        for (CartItem item : cartList) {

            subtotal += item.getTotal();
        }


        double discount = getDiscount();

        double gstPercentage = getGst();


        // Prevent negative subtotal

        double afterDiscount =
                subtotal - discount;

        if (afterDiscount < 0) {
            afterDiscount = 0;
        }


        double gstAmount =
                afterDiscount *
                        gstPercentage / 100;


        double grandTotal =
                afterDiscount + gstAmount;


        tvSubtotal.setText(
                "Subtotal     ₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                subtotal
                        )
        );

        tvDiscount.setText(
                "Discount     ₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                discount
                        )
        );

        tvGst.setText(
                "GST     ₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                gstAmount
                        )
        );

        tvGrandTotal.setText(
                "₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                grandTotal
                        )
        );
    }


    // -----------------------------------------
    // GET DISCOUNT
    // -----------------------------------------

    private double getDiscount() {

        String value =
                etDiscount.getText()
                        .toString()
                        .trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {

            double discount =
                    Double.parseDouble(value);

            return Math.max(0, discount);

        } catch (Exception e) {

            return 0;
        }
    }


    // -----------------------------------------
    // GET GST
    // -----------------------------------------

    private double getGst() {

        String value =
                etGst.getText()
                        .toString()
                        .trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {

            double gst =
                    Double.parseDouble(value);

            return Math.max(0, gst);

        } catch (Exception e) {

            return 0;
        }
    }


    // -----------------------------------------
    // SAVE BILL
    // -----------------------------------------

    private void saveBill() {

        String customerName =
                etCustomerName.getText()
                        .toString()
                        .trim();


        if (customerName.isEmpty()) {

            etCustomerName.setError(
                    "Enter customer name"
            );

            etCustomerName.requestFocus();

            return;
        }


        if (cartList.isEmpty()) {

            Toast.makeText(
                    this,
                    "Add at least one product",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Calculate final values

        double subtotal = 0;

        for (CartItem item : cartList) {

            subtotal += item.getTotal();
        }


        double discount = getDiscount();

        double gstPercentage = getGst();


        double afterDiscount =
                subtotal - discount;

        if (afterDiscount < 0) {
            afterDiscount = 0;
        }


        double gstAmount =
                afterDiscount *
                        gstPercentage / 100;


        double grandTotal =
                afterDiscount + gstAmount;


        String date =
                new SimpleDateFormat(
                        "dd MMM yyyy, hh:mm a",
                        Locale.getDefault()
                ).format(new Date());


        // Create bill

        long billId =
                databaseHelper.createBill(
                        customerName,
                        subtotal,
                        discount,
                        gstAmount,
                        grandTotal,
                        date
                );


        if (billId == -1) {

            Toast.makeText(
                    this,
                    "Failed to save bill",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // Save every cart item

        boolean success = true;

        for (CartItem item : cartList) {

            boolean itemSaved =
                    databaseHelper.addBillItem(
                            billId,
                            item.getProductId(),
                            item.getProductName(),
                            item.getPrice(),
                            item.getQuantity(),
                            item.getTotal()
                    );


            if (!itemSaved) {
                success = false;
                break;
            }


            // Reduce inventory stock

            boolean stockUpdated =
                    databaseHelper.reduceStock(
                            item.getProductId(),
                            item.getQuantity()
                    );


            if (!stockUpdated) {
                success = false;
                break;
            }
        }


        if (!success) {

            Toast.makeText(
                    this,
                    "Bill saved but some items could not be processed",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        Toast.makeText(
                this,
                "Bill saved successfully",
                Toast.LENGTH_SHORT
        ).show();


        // Open bill details

        Intent intent =
                new Intent(
                        NewBillActivity.this,
                        BillDetailsActivity.class
                );

        intent.putExtra(
                "bill_id",
                (int) billId
        );

        startActivity(intent);

        finish();
    }
}