package com.exp1.billeasy.inventory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.exp1.billeasy.R;
import com.exp1.billeasy.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;

public class EditProductActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextInputEditText etProductName;
    TextInputEditText etCategory;
    TextInputEditText etPrice;
    TextInputEditText etQuantity;
    TextInputEditText etSupplier;

    MaterialButton btnUpdateProduct;
    MaterialButton btnAddPhoto;
    ImageView ivProductImage;

    DatabaseHelper databaseHelper;

    int productId;
    String imagePath = "";
    Uri tempImageUri;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (tempImageUri != null) {
                        imagePath = tempImageUri.toString();
                        ivProductImage.setImageURI(tempImageUri);
                        ivProductImage.setColorFilter(null);
                        ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    openHardwareCamera();
                } else {
                    Toast.makeText(this, "Camera permission is required to take product photos", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_product);

        btnBack = findViewById(R.id.btnBack);

        etProductName = findViewById(R.id.etProductName);
        etCategory = findViewById(R.id.etCategory);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etSupplier = findViewById(R.id.etSupplier);

        ivProductImage = findViewById(R.id.ivProductImage);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        btnUpdateProduct = findViewById(R.id.btnUpdateProduct);

        databaseHelper = new DatabaseHelper(this);

        productId = getIntent().getIntExtra("product_id", -1);

        String name = getIntent().getStringExtra("product_name");
        String category = getIntent().getStringExtra("product_category");
        double price = getIntent().getDoubleExtra("product_price", 0);
        int quantity = getIntent().getIntExtra("product_quantity", 0);
        String supplier = getIntent().getStringExtra("product_supplier");
        imagePath = getIntent().getStringExtra("product_image");

        etProductName.setText(name);
        etCategory.setText(category);
        etPrice.setText(String.valueOf(price));
        etQuantity.setText(String.valueOf(quantity));

        if (supplier != null) {
            etSupplier.setText(supplier);
        }

        if (imagePath != null && !imagePath.isEmpty()) {
            Uri imageUri = Uri.parse(imagePath);
            ivProductImage.setImageURI(imageUri);
            ivProductImage.setColorFilter(null);
            ivProductImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        btnBack.setOnClickListener(v -> finish());

        // Check permission and launch hardware camera
        btnAddPhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openHardwareCamera();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        btnUpdateProduct.setOnClickListener(v -> updateProduct());
    }

    private void openHardwareCamera() {
        try {
            File tempFile = new File(getCacheDir(), "temp_product_" + System.currentTimeMillis() + ".jpg");
            tempImageUri = FileProvider.getUriForFile(
                    this,
                    "com.exp1.billeasy.fileprovider",
                    tempFile
            );
            
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Error starting camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProduct() {
        String name = etProductName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String quantityText = etQuantity.getText().toString().trim();
        String supplier = etSupplier.getText().toString().trim();

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
            Toast.makeText(this, "Enter valid price and quantity", Toast.LENGTH_SHORT).show();
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

        boolean updated = databaseHelper.updateProduct(
                productId,
                name,
                category,
                price,
                quantity,
                supplier,
                imagePath
        );

        if (updated) {
            Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to update product", Toast.LENGTH_SHORT).show();
        }
    }
}