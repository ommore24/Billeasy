package com.exp1.billeasy.inventory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exp1.billeasy.R;
import com.exp1.billeasy.adapters.ProductAdapter;
import com.exp1.billeasy.database.DatabaseHelper;
import com.exp1.billeasy.models.Product;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {

    RecyclerView recyclerProducts;
    FloatingActionButton fabAddProduct;

    DatabaseHelper databaseHelper;

    ProductAdapter productAdapter;
    ArrayList<Product> productList;

    EditText etSearch;
    TextView tvTotalProducts, tvLowStock;
    ChipGroup chipGroupFilters;
    com.google.android.material.card.MaterialCardView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_inventory);

        // Initialize views
        recyclerProducts = findViewById(R.id.recyclerProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        etSearch = findViewById(R.id.etSearch);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvLowStock = findViewById(R.id.tvLowStock);
        chipGroupFilters = findViewById(R.id.chipGroupFilters);
        btnBack = findViewById(R.id.btnBack);

        databaseHelper = new DatabaseHelper(this);

        recyclerProducts.setLayoutManager(new LinearLayoutManager(this));

        // Load initial data
        loadProducts();
        updateSummary();

        // Listeners
        btnBack.setOnClickListener(v -> finish());

        fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, AddProductActivity.class);
            startActivity(intent);
        });

        // Search logic
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Filter chips logic using the modern listener
        chipGroupFilters.setOnCheckedStateChangeListener((group, checkedIds) -> applyFilters());
    }

    private void loadProducts() {
        productList = databaseHelper.getAllProducts();
        // Initialize adapter with full list first
        productAdapter = new ProductAdapter(this, new ArrayList<>(productList));
        recyclerProducts.setAdapter(productAdapter);
    }

    private void updateSummary() {
        int totalProducts = databaseHelper.getProductCount();
        int lowStockCount = databaseHelper.getLowStockCount();

        tvTotalProducts.setText(String.valueOf(totalProducts));
        tvLowStock.setText(String.valueOf(lowStockCount));
    }

    private void applyFilters() {
        if (productList == null) return;

        String searchText = etSearch.getText().toString().toLowerCase().trim();
        int checkedChipId = chipGroupFilters.getCheckedChipId();

        ArrayList<Product> filteredList = new ArrayList<>();

        for (Product item : productList) {
            boolean matchesSearch = item.getName().toLowerCase().contains(searchText);
            boolean matchesFilter = true;

            if (checkedChipId == R.id.chipLowStock) {
                // Low stock = 1 to 5
                matchesFilter = (item.getQuantity() > 0 && item.getQuantity() <= 5);
            } else if (checkedChipId == R.id.chipOutOfStock) {
                // Out of stock = 0 or less
                matchesFilter = (item.getQuantity() <= 0);
            }

            if (matchesSearch && matchesFilter) {
                filteredList.add(item);
            }
        }

        if (productAdapter != null) {
            productAdapter.updateList(filteredList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            // Re-load the data from source
            productList = databaseHelper.getAllProducts();
            updateSummary();
            // Apply current filters/search to the fresh list
            applyFilters();
        }
    }
}
