package com.exp1.billeasy;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddProductActivity extends AppCompatActivity {

    TextInputEditText etProductName;
    TextInputEditText etCategory;
    TextInputEditText etPrice;
    TextInputEditText etQuantity;
    TextInputEditText etSupplier;

    MaterialButton btnSaveProduct;
    TextView btnBack;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_product);

        etProductName = findViewById(R.id.etProductName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etSupplier = findViewById(R.id.etSupplier);

        btnSaveProduct = findViewById(R.id.btnSaveProduct);
        btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);


        // Back button

        btnBack.setOnClickListener(v -> finish());


        // Save product

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }


    private void saveProduct() {

        String name = etProductName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String supplier = etSupplier.getText().toString().trim();


        // Validation

        if (name.isEmpty()) {
            etProductName.setError("Enter product name");
            etProductName.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            etCategory.setError("Enter category");
            etCategory.requestFocus();
            return;
        }

        if (priceText.isEmpty()) {
            etPrice.setError("Enter price");
            etPrice.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            etQuantity.setError("Enter quantity");
            etQuantity.requestFocus();
            return;
        }


        double price;

        int quantity;

        try {

            price = Double.parseDouble(priceText);
            quantity = Integer.parseInt(quantityText);

        } catch (NumberFormatException e) {

            Toast.makeText(
                    this,
                    "Enter valid price and quantity",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (price < 0) {

            etPrice.setError("Price cannot be negative");
            return;
        }


        if (quantity < 0) {

            etQuantity.setError("Quantity cannot be negative");
            return;
        }


        // Insert into SQLite

        boolean inserted = databaseHelper.addProduct(
                name,
                category,
                price,
                quantity,
                supplier
        );


        if (inserted) {

            Toast.makeText(
                    this,
                    "Product added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add product",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}