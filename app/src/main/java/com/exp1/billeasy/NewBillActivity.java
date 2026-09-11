package com.exp1.billeasy;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class NewBillActivity extends AppCompatActivity {

    private MaterialCardView btnBack;

    private TextInputEditText etCustomerName;
    private TextInputEditText etDiscount;
    private TextInputEditText etGst;

    private RecyclerView recyclerProducts;
    private RecyclerView recyclerCart;

    private android.widget.TextView tvSubtotal;
    private android.widget.TextView tvDiscount;
    private android.widget.TextView tvGst;
    private android.widget.TextView tvGrandTotal;

    private MaterialButton btnSaveBill;

    private DatabaseHelper databaseHelper;

    private ArrayList<Product> productList;
    private ArrayList<CartItem> cartList;

    private ProductAdapter productAdapter;
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_new_bill);

        databaseHelper = new DatabaseHelper(this);

        initializeViews();

        setupBackButton();

        setupProductRecyclerView();

        setupCartRecyclerView();

        setupCalculationListeners();

        loadProducts();

        calculateBill();
    }

    // ----------------------------------------------------
    // INITIALIZE VIEWS
    // ----------------------------------------------------

    private void initializeViews() {

        btnBack = findViewById(R.id.btnBack);

        etCustomerName =
                findViewById(R.id.etCustomerName);

        etDiscount =
                findViewById(R.id.etDiscount);

        etGst =
                findViewById(R.id.etGst);

        recyclerProducts =
                findViewById(R.id.recyclerProducts);

        recyclerCart =
                findViewById(R.id.recyclerCart);

        tvSubtotal =
                findViewById(R.id.tvSubtotal);

        tvDiscount =
                findViewById(R.id.tvDiscount);

        tvGst =
                findViewById(R.id.tvGst);

        tvGrandTotal =
                findViewById(R.id.tvGrandTotal);

        btnSaveBill =
                findViewById(R.id.btnSaveBill);

        cartList = new ArrayList<>();

        btnSaveBill.setOnClickListener(
                v -> saveBill()
        );
    }

    // ----------------------------------------------------
    // BACK BUTTON
    // ----------------------------------------------------

    private void setupBackButton() {

        btnBack.setOnClickListener(v -> finish());
    }

    // ----------------------------------------------------
    // PRODUCT LIST
    // ----------------------------------------------------

    private void setupProductRecyclerView() {

        productList = new ArrayList<>();

        recyclerProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        productAdapter = new ProductAdapter(
                this,
                productList,
                product -> addProductToCart(product)
        );

        recyclerProducts.setAdapter(productAdapter);
    }

    private void loadProducts() {

        productList.clear();

        productList.addAll(
                databaseHelper.getAllProducts()
        );

        productAdapter.notifyDataSetChanged();
    }

    // ----------------------------------------------------
    // CART
    // ----------------------------------------------------

    private void setupCartRecyclerView() {

        recyclerCart.setLayoutManager(
                new LinearLayoutManager(this)
        );

        cartAdapter = new CartAdapter(

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

    // ----------------------------------------------------
    // ADD PRODUCT TO CART
    // ----------------------------------------------------

    private void addProductToCart(Product product) {

        // Do not allow products with zero stock
        if (product.getQuantity() <= 0) {

            Toast.makeText(
                    this,
                    "This product is out of stock",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Check whether product is already in cart
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

        // Product is not already in cart
        CartItem cartItem = new CartItem(
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

        Toast.makeText(
                this,
                product.getName() + " added to bill",
                Toast.LENGTH_SHORT
        ).show();
    }

    // ----------------------------------------------------
    // DISCOUNT / GST LISTENERS
    // ----------------------------------------------------

    private void setupCalculationListeners() {

        etDiscount.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        calculateBill();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );

        etGst.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        calculateBill();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    // ----------------------------------------------------
    // CALCULATE BILL
    // ----------------------------------------------------

    private void calculateBill() {

        double subtotal = 0;

        for (CartItem item : cartList) {

            subtotal += item.getTotal();
        }

        double discount =
                getNumberFromField(etDiscount);

        double gstPercentage =
                getNumberFromField(etGst);

        // Prevent discount greater than subtotal
        if (discount > subtotal) {
            discount = subtotal;
        }

        double amountAfterDiscount =
                subtotal - discount;

        double gstAmount =
                amountAfterDiscount *
                        gstPercentage / 100.0;

        double grandTotal =
                amountAfterDiscount + gstAmount;

        tvSubtotal.setText(
                "Subtotal     ₹" +
                        String.format("%.2f", subtotal)
        );

        tvDiscount.setText(
                "Discount     ₹" +
                        String.format("%.2f", discount)
        );

        tvGst.setText(
                "GST     ₹" +
                        String.format("%.2f", gstAmount)
        );

        tvGrandTotal.setText(
                "₹" +
                        String.format("%.2f", grandTotal)
        );
    }

    private double getNumberFromField(
            TextInputEditText field) {

        String value =
                field.getText() == null
                        ? ""
                        : field.getText()
                          .toString()
                          .trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {

            double number =
                    Double.parseDouble(value);

            if (number < 0) {
                return 0;
            }

            return number;

        } catch (NumberFormatException e) {

            return 0;
        }
    }

    // ----------------------------------------------------
    // SAVE BILL
    // ----------------------------------------------------

    private void saveBill() {

        String customerName =
                etCustomerName.getText() == null
                        ? ""
                        : etCustomerName.getText()
                          .toString()
                          .trim();

        // Validate customer
        if (customerName.isEmpty()) {

            etCustomerName.setError(
                    "Enter customer name"
            );

            etCustomerName.requestFocus();

            return;
        }

        // Validate cart
        if (cartList.isEmpty()) {

            Toast.makeText(
                    this,
                    "Add at least one product",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        double subtotal = 0;

        for (CartItem item : cartList) {
            subtotal += item.getTotal();
        }

        double discount =
                getNumberFromField(etDiscount);

        double gstPercentage =
                getNumberFromField(etGst);

        if (discount > subtotal) {
            discount = subtotal;
        }

        double amountAfterDiscount =
                subtotal - discount;

        double gstAmount =
                amountAfterDiscount *
                        gstPercentage / 100.0;

        double grandTotal =
                amountAfterDiscount + gstAmount;

        String date =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                ).format(new Date());

        SQLiteDatabase db =
                databaseHelper.getWritableDatabase();

        db.beginTransaction();

        long billId = -1;

        try {

            // ------------------------------------------------
            // CREATE BILL
            // ------------------------------------------------

            billId = databaseHelper.createBill(
                    customerName,
                    subtotal,
                    discount,
                    gstAmount,
                    grandTotal,
                    date
            );

            if (billId == -1) {

                throw new Exception(
                        "Unable to create bill"
                );
            }

            // ------------------------------------------------
            // SAVE BILL ITEMS + REDUCE STOCK
            // ------------------------------------------------

            for (CartItem item : cartList) {

                // Verify stock one more time before saving
                Product currentProduct =
                        findProductById(
                                item.getProductId()
                        );

                if (currentProduct == null) {

                    throw new Exception(
                            "Product no longer exists"
                    );
                }

                if (item.getQuantity() >
                        currentProduct.getQuantity()) {

                    throw new Exception(
                            "Insufficient stock for "
                                    + item.getProductName()
                    );
                }

                boolean itemAdded =
                        databaseHelper.addBillItem(
                                billId,
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity(),
                                item.getTotal()
                        );

                if (!itemAdded) {

                    throw new Exception(
                            "Unable to save bill item"
                    );
                }

                boolean stockReduced =
                        databaseHelper.reduceStock(
                                item.getProductId(),
                                item.getQuantity()
                        );

                if (!stockReduced) {

                    throw new Exception(
                            "Unable to update stock"
                    );
                }
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            return;

        } finally {

            db.endTransaction();
        }

        // ------------------------------------------------
        // SUCCESS
        // ------------------------------------------------

        Toast.makeText(
                this,
                "Bill saved successfully",
                Toast.LENGTH_SHORT
        ).show();

        Intent intent = new Intent(
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

    // ----------------------------------------------------
    // FIND PRODUCT
    // ----------------------------------------------------

    private Product findProductById(int productId) {

        for (Product product : productList) {

            if (product.getId() == productId) {
                return product;
            }
        }

        // Reload database in case inventory changed
        ArrayList<Product> latestProducts =
                databaseHelper.getAllProducts();

        for (Product product : latestProducts) {

            if (product.getId() == productId) {
                return product;
            }
        }

        return null;
    }
}