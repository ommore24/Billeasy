package com.exp1.billeasy.inventory;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exp1.billeasy.R;
import com.exp1.billeasy.database.DatabaseHelper;
import com.exp1.billeasy.models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ProductDetailsActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextView tvProductName;
    TextView tvCategory;
    TextView tvPrice;
    TextView tvQuantity;
    TextView tvSupplier;
    ImageView ivProductImage;
    MaterialCardView cardProductImage;

    MaterialButton btnEdit;
    MaterialButton btnAddStock;
    MaterialButton btnDelete;

    DatabaseHelper databaseHelper;

    int productId;

    String name;
    String category;
    double price;
    int quantity;
    String supplier;
    String imagePath;


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
        ivProductImage = findViewById(R.id.ivProductImage);
        cardProductImage = findViewById(R.id.cardProductImage);

        btnEdit = findViewById(R.id.btnEdit);
        btnAddStock = findViewById(R.id.btnAddStock);
        btnDelete = findViewById(R.id.btnDelete);

        databaseHelper = new DatabaseHelper(this);


        // Receive product ID

        productId = getIntent().getIntExtra("product_id", -1);


        // Back

        btnBack.setOnClickListener(v -> finish());


        // Edit

        btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(ProductDetailsActivity.this, EditProductActivity.class);

            intent.putExtra("product_id", productId);
            intent.putExtra("product_name", name);
            intent.putExtra("product_category", category);
            intent.putExtra("product_price", price);
            intent.putExtra("product_quantity", quantity);
            intent.putExtra("product_supplier", supplier == null ? "" : supplier);
            intent.putExtra("product_image", imagePath);

            startActivity(intent);

        });


        // Add Stock

        btnAddStock.setOnClickListener(v -> showAddStockDialog());


        // Delete

        btnDelete.setOnClickListener(v -> showDeleteDialog());

    }


    @Override
    protected void onResume() {
        super.onResume();
        loadProductData();
    }


    private void loadProductData() {

        Product product = databaseHelper.getProduct(productId);

        if (product != null) {

            name = product.getName();
            category = product.getCategory();
            price = product.getPrice();
            quantity = product.getQuantity();
            supplier = product.getSupplier();
            imagePath = product.getImagePath();

            tvProductName.setText(name);
            tvCategory.setText(category);
            tvPrice.setText("₹" + price);
            tvQuantity.setText(String.valueOf(quantity));

            if (supplier == null || supplier.isEmpty()) {
                tvSupplier.setText("Not provided");
            } else {
                tvSupplier.setText(supplier);
            }

            if (imagePath != null && !imagePath.isEmpty()) {
                cardProductImage.setVisibility(View.VISIBLE);
                ivProductImage.setImageURI(Uri.parse(imagePath));
            } else {
                cardProductImage.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void showAddStockDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Stock Batch");

        final EditText input = new EditText(this);
        input.setHint("Enter quantity to add");
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 10);
        layout.addView(input);
        builder.setView(layout);

        builder.setPositiveButton("ADD STOCK", (dialog, which) -> {

            String quantityStr = input.getText().toString().trim();

            if (!quantityStr.isEmpty()) {

                try {

                    int addedQty = Integer.parseInt(quantityStr);

                    if (addedQty > 0) {
                        updateStock(addedQty);
                    } else {
                        Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.cancel());

        builder.show();
    }


    private void updateStock(int addedQty) {

        boolean success = databaseHelper.addStock(productId, addedQty);

        if (success) {
            Toast.makeText(this, "Stock updated successfully", Toast.LENGTH_SHORT).show();
            loadProductData();
        } else {
            Toast.makeText(this, "Failed to update stock", Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Delete product?")
                .setMessage("This product will be permanently removed from your inventory.")
                .setNegativeButton("CANCEL", null)
                .setPositiveButton("DELETE", (dialog, which) -> deleteProduct())
                .show();
    }


    private void deleteProduct() {

        boolean deleted = databaseHelper.deleteProduct(productId);

        if (deleted) {
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Unable to delete product", Toast.LENGTH_SHORT).show();
        }
    }
}