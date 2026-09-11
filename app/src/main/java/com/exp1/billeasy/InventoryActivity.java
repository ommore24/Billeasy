package com.exp1.billeasy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    FloatingActionButton fabAddProduct;

    DatabaseHelper databaseHelper;

    ProductAdapter productAdapter;

    ArrayList<Product> productList;

    EditText etSearch;
    com.google.android.material.card.MaterialCardView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        recyclerProducts = findViewById(R.id.recyclerProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        etSearch = findViewById(R.id.etSearch);

        btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);

        recyclerProducts.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadProducts();

        btnBack.setOnClickListener(v -> finish());

        fabAddProduct.setOnClickListener(v -> {

            Intent intent = new Intent(
                    InventoryActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);

        });
    }


    private void loadProducts() {

        productList = databaseHelper.getAllProducts();

        productAdapter = new ProductAdapter(this, productList);

        recyclerProducts.setAdapter(productAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (databaseHelper != null) {
            loadProducts();
        }
    }
}
