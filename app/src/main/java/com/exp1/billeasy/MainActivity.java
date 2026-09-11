package com.exp1.billeasy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    MaterialCardView cardInventory;
    MaterialCardView cardBilling;
    BottomNavigationView bottomNavigation;

    TextView tvRevenue;
    TextView tvProducts;
    TextView tvLowStock;

    DatabaseHelper databaseHelper;
    MaterialCardView cardRecentTransaction;

    TextView tvRecentBill;
    TextView tvRecentBillDate;
    TextView tvRecentBillAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // =========================
        // FIND VIEWS
        // =========================

        cardInventory = findViewById(R.id.cardInventory);
        cardBilling = findViewById(R.id.cardBilling);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        tvRevenue = findViewById(R.id.tvRevenue);
        tvProducts = findViewById(R.id.tvProducts);
        tvLowStock = findViewById(R.id.tvLowStock);

        cardRecentTransaction =
                findViewById(R.id.cardRecentTransaction);

        tvRecentBill =
                findViewById(R.id.tvRecentBill);

        tvRecentBillDate =
                findViewById(R.id.tvRecentBillDate);

        tvRecentBillAmount =
                findViewById(R.id.tvRecentBillAmount);

        databaseHelper = new DatabaseHelper(this);


        // =========================
        // NEW BILL CARD
        // =========================

        cardBilling.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    NewBillActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // INVENTORY CARD
        // =========================

        cardInventory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    InventoryActivity.class
            );

            startActivity(intent);
        });


        // =========================
        // BOTTOM NAVIGATION
        // =========================

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                return true;
            }

            else if (id == R.id.nav_inventory) {

                Intent intent = new Intent(
                        MainActivity.this,
                        InventoryActivity.class
                );

                startActivity(intent);

                return true;
            }

            else if (id == R.id.nav_bills) {

                Intent intent = new Intent(
                        MainActivity.this,
                        TransactionsActivity.class
                );

                startActivity(intent);

                return true;
            }

            else if (id == R.id.nav_profile) {

                // Profile can be implemented later
                return true;
            }

            return false;
        });
    }


    // =========================
    // LOAD DASHBOARD DATA
    // =========================

    private void loadDashboardData() {

        loadRevenue();

        loadProductCount();

        loadLowStock();

        loadRecentTransaction();
    }


    // =========================
    // TODAY'S REVENUE
    // =========================

    private void loadRevenue() {

        double revenue =
                databaseHelper.getTodayRevenue();

        tvRevenue.setText(
                "₹" + format(revenue)
        );
    }


    // =========================
    // TOTAL PRODUCTS
    // =========================

    private void loadProductCount() {

        int count =
                databaseHelper.getProductCount();

        tvProducts.setText(
                String.valueOf(count)
        );
    }


    // =========================
    // LOW STOCK
    // =========================

    private void loadLowStock() {

        int count =
                databaseHelper.getLowStockCount();

        tvLowStock.setText(
                String.valueOf(count)
        );
    }

    // =========================
// LOAD RECENT TRANSACTION
// =========================

    private void loadRecentTransaction() {

        Cursor cursor =
                databaseHelper.getLatestBill();

        if (cursor.moveToFirst()) {

            int billId =
                    cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

            String customer =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "customer_name"
                            )
                    );

            double total =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "total"
                            )
                    );

            String date =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "date"
                            )
                    );

            tvRecentBill.setText(
                    "#"+ billId + "  •  " + customer
            );

            tvRecentBillDate.setText(
                    date
            );

            tvRecentBillAmount.setText(
                    "₹" + format(total)
            );

            cardRecentTransaction.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                BillDetailsActivity.class
                        );

                intent.putExtra(
                        "bill_id",
                        billId
                );

                startActivity(intent);
            });

        } else {

            tvRecentBill.setText(
                    "No transactions yet"
            );

            tvRecentBillDate.setText(
                    "Create your first bill"
            );

            tvRecentBillAmount.setText(
                    "₹0.00"
            );

            cardRecentTransaction.setOnClickListener(null);
        }

        cursor.close();
    }


    // =========================
    // REFRESH WHEN RETURNING
    // =========================

    @Override
    protected void onResume() {

        super.onResume();

        loadDashboardData();
    }


    // =========================
    // FORMAT MONEY
    // =========================

    private String format(double value) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                value
        );
    }
}