package com.exp1.billeasy;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class EditProductActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextInputEditText etProductName;
    TextInputEditText etCategory;
    TextInputEditText etPrice;
    TextInputEditText etQuantity;
    TextInputEditText etSupplier;

    MaterialButton btnUpdateProduct;

    DatabaseHelper databaseHelper;

    int productId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_product);


        // Find views

        btnBack = findViewById(R.id.btnBack);

        etProductName = findViewById(R.id.etProductName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etSupplier = findViewById(R.id.etSupplier);

        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);


        databaseHelper = new DatabaseHelper(this);


        // Get product ID

        productId = getIntent().getIntExtra("product_id", -1);


        // Get existing data

        String name = getIntent().getStringExtra("product_name");
        String category = getIntent().getStringExtra("product_category");
        double price = getIntent().getDoubleExtra("product_price", 0);
        int quantity = getIntent().getIntExtra("product_quantity", 0);
        String supplier = getIntent().getStringExtra("product_supplier");


        // Put existing values into fields

        etProductName.setText(name);
        etCategory.setText(category);
        etPrice.setText(String.valueOf(price));
        etQuantity.setText(String.valueOf(quantity));

        if (supplier != null) {
            etSupplier.setText(supplier);
        }


        // Back

        btnBack.setOnClickListener(v -> finish());


        // Update

        btnUpdateProduct.setOnClickListener(v -> updateProduct());

    }


    private void updateProduct() {

        String name =
                etProductName.getText().toString().trim();

        String category =
                etCategory.getText().toString().trim();

        String priceText =
                etPrice.getText().toString().trim();

        String quantityText =
                etQuantity.getText().toString().trim();

        String supplier =
                etSupplier.getText().toString().trim();


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


        // Update database

        boolean updated = databaseHelper.updateProduct(
                productId,
                name,
                category,
                price,
                quantity,
                supplier
        );


        if (updated) {

            Toast.makeText(
                    this,
                    "Product updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update product",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}