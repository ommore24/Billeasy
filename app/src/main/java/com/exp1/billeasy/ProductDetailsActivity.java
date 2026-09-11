package com.exp1.billeasy;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProductDetailsActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextView tvProductName;
    TextView tvCategory;
    TextView tvPrice;
    TextView tvQuantity;
    TextView tvSupplier;

    MaterialButton btnEdit;
    MaterialButton btnDelete;

    DatabaseHelper databaseHelper;

    int productId;

    String name;
    String category;
    double price;
    int quantity;
    String supplier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product_details);


        // Views

        btnBack = findViewById(R.id.btnBack);

        tvProductName = findViewById(R.id.tvProductName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvSupplier = findViewById(R.id.tvSupplier);

        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);

        databaseHelper = new DatabaseHelper(this);


        // Receive product information

        productId =
                getIntent().getIntExtra("product_id", -1);

        name =
                getIntent().getStringExtra("product_name");

        category =
                getIntent().getStringExtra("product_category");

        price =
                getIntent().getDoubleExtra("product_price", 0);

        quantity =
                getIntent().getIntExtra("product_quantity", 0);

        supplier =
                getIntent().getStringExtra("product_supplier");


        // Display

        tvProductName.setText(name);
        tvCategory.setText(category);
        tvPrice.setText("₹" + price);
        tvQuantity.setText(String.valueOf(quantity));


        if (supplier == null || supplier.isEmpty()) {

            tvSupplier.setText("Not provided");

        } else {

            tvSupplier.setText(supplier);
        }


        // Back

        btnBack.setOnClickListener(v -> finish());


        // Edit

        btnEdit.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            ProductDetailsActivity.this,
                            EditProductActivity.class
                    );

            intent.putExtra("product_id", productId);
            intent.putExtra("product_name", name);
            intent.putExtra("product_category", category);
            intent.putExtra("product_price", price);
            intent.putExtra("product_quantity", quantity);
            intent.putExtra(
                    "product_supplier",
                    supplier == null ? "" : supplier
            );

            startActivity(intent);

        });


        // Delete

        btnDelete.setOnClickListener(v -> showDeleteDialog());

    }


    private void showDeleteDialog() {

        new AlertDialog.Builder(this)

                .setTitle("Delete product?")

                .setMessage(
                        "This product will be permanently removed from your inventory."
                )

                .setNegativeButton(
                        "CANCEL",
                        null
                )

                .setPositiveButton(
                        "DELETE",
                        (dialog, which) -> deleteProduct()
                )

                .show();
    }


    private void deleteProduct() {

        boolean deleted =
                databaseHelper.deleteProduct(productId);


        if (deleted) {

            Toast.makeText(
                    this,
                    "Product deleted",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Unable to delete product",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}