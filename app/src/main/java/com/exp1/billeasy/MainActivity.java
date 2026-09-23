package com.exp1.billeasy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.exp1.billeasy.analytics.AnalyticsActivity;
import com.exp1.billeasy.analytics.TransactionsActivity;
import com.exp1.billeasy.auth.ProfileActivity;
import com.exp1.billeasy.billing.BillDetailsActivity;
import com.exp1.billeasy.billing.NewBillActivity;
import com.exp1.billeasy.database.DatabaseHelper;
import com.exp1.billeasy.inventory.InventoryActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    MaterialCardView cardInventory;
    MaterialCardView cardBilling;
    BottomNavigationView bottomNavigation;

    TextView tvRevenue;
    TextView tvRevenuePercentage;
    TextView tvProducts;
    TextView tvLowStock;

    DatabaseHelper databaseHelper;

    // Recent transaction cards
    MaterialCardView cardRecentTransaction1;
    MaterialCardView cardRecentTransaction2;
    MaterialCardView cardRecentTransaction3;

    TextView tvRecentBill1;
    TextView tvRecentBillDate1;
    TextView tvRecentBillAmount1;

    TextView tvRecentBill2;
    TextView tvRecentBillDate2;
    TextView tvRecentBillAmount2;

    TextView tvRecentBill3;
    TextView tvRecentBillDate3;
    TextView tvRecentBillAmount3;

    TextView tvViewAll;


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
        tvRevenuePercentage = findViewById(R.id.tvRevenuePercentage);
        tvProducts = findViewById(R.id.tvProducts);
        tvLowStock = findViewById(R.id.tvLowStock);

        cardRecentTransaction1 =
                findViewById(R.id.cardRecentTransaction1);

        cardRecentTransaction2 =
                findViewById(R.id.cardRecentTransaction2);

        cardRecentTransaction3 =
                findViewById(R.id.cardRecentTransaction3);

        tvRecentBill1 =
                findViewById(R.id.tvRecentBill1);

        tvRecentBillDate1 =
                findViewById(R.id.tvRecentBillDate1);

        tvRecentBillAmount1 =
                findViewById(R.id.tvRecentBillAmount1);

        tvRecentBill2 =
                findViewById(R.id.tvRecentBill2);

        tvRecentBillDate2 =
                findViewById(R.id.tvRecentBillDate2);

        tvRecentBillAmount2 =
                findViewById(R.id.tvRecentBillAmount2);

        tvRecentBill3 =
                findViewById(R.id.tvRecentBill3);

        tvRecentBillDate3 =
                findViewById(R.id.tvRecentBillDate3);

        tvRecentBillAmount3 =
                findViewById(R.id.tvRecentBillAmount3);

        tvViewAll =
                findViewById(R.id.tvViewAll);

        MaterialCardView cardAnalyticsToggle = findViewById(R.id.cardAnalyticsToggle);
        if (cardAnalyticsToggle != null) {
            cardAnalyticsToggle.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, AnalyticsActivity.class));
            });
        }

        databaseHelper = new DatabaseHelper(this);

        // Open Analytics from the top right "dots" card
        findViewById(R.id.tvRecentBill1).getRootView().findViewById(R.id.cardRecentTransaction1).getParent(); // Context check dummy
        
        // Let's find the ⋮ card (it doesn't have an ID in the layout I read)
        // I will add an ID to it in the XML first or try to find it via hierarchy.
        // Actually, the card in the header was:
        /*
        <com.google.android.material.card.MaterialCardView
            android:layout_width="46dp"
            android:layout_height="46dp"
            ...
        */


        // =========================
        // NEW BILL
        // =========================

        cardBilling.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            NewBillActivity.class
                    );

            startActivity(intent);
        });


        // =========================
        // INVENTORY
        // =========================

        cardInventory.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            InventoryActivity.class
                    );

            startActivity(intent);
        });


        // =========================
        // VIEW ALL
        // =========================

        tvViewAll.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            MainActivity.this,
                            TransactionsActivity.class
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

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                InventoryActivity.class
                        );

                startActivity(intent);

                return true;
            }

            else if (id == R.id.nav_bills) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                TransactionsActivity.class
                        );

                startActivity(intent);

                return true;
            }

            else if (id == R.id.nav_profile) {

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                ProfileActivity.class
                        );

                startActivity(intent);

                return true;
            }

            return false;
        });
    }


    // =========================
    // LOAD DASHBOARD
    // =========================

    private void loadDashboardData() {

        loadRevenue();

        loadProductCount();

        loadLowStock();

        loadRecentTransactions();
    }


    // =========================
    // TODAY'S REVENUE
    // =========================

    private void loadRevenue() {

        double todayRevenue =
                databaseHelper.getTodayRevenue();
        double yesterdayRevenue =
                databaseHelper.getYesterdayRevenue();

        tvRevenue.setText(
                "₹" + format(todayRevenue)
        );

        if (tvRevenuePercentage != null) {
            if (yesterdayRevenue == 0) {
                if (todayRevenue > 0) {
                    tvRevenuePercentage.setText("↑ 100%");
                    tvRevenuePercentage.setTextColor(android.graphics.Color.parseColor("#8FE3B0"));
                } else {
                    tvRevenuePercentage.setText("0.0%");
                    tvRevenuePercentage.setTextColor(android.graphics.Color.parseColor("#BEC5DB"));
                }
            } else {
                double percentChange = ((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100.0;
                if (percentChange >= 0) {
                    tvRevenuePercentage.setText(String.format(Locale.getDefault(), "↑ %.1f%%", percentChange));
                    tvRevenuePercentage.setTextColor(android.graphics.Color.parseColor("#8FE3B0"));
                } else {
                    tvRevenuePercentage.setText(String.format(Locale.getDefault(), "↓ %.1f%%", Math.abs(percentChange)));
                    tvRevenuePercentage.setTextColor(android.graphics.Color.parseColor("#FF8A80"));
                }
            }
        }
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
    // LOAD 3 RECENT TRANSACTIONS
    // =========================

    private void loadRecentTransactions() {

        Cursor cursor =
                databaseHelper.getRecentBills(3);

        // Hide all initially
        cardRecentTransaction1.setVisibility(
                MaterialCardView.GONE
        );

        cardRecentTransaction2.setVisibility(
                MaterialCardView.GONE
        );

        cardRecentTransaction3.setVisibility(
                MaterialCardView.GONE
        );

        int position = 0;

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    int billId =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            "id"
                                    )
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


                    if (position == 0) {

                        showTransaction(
                                cardRecentTransaction1,
                                tvRecentBill1,
                                tvRecentBillDate1,
                                tvRecentBillAmount1,
                                billId,
                                customer,
                                total,
                                date
                        );

                    }

                    else if (position == 1) {

                        showTransaction(
                                cardRecentTransaction2,
                                tvRecentBill2,
                                tvRecentBillDate2,
                                tvRecentBillAmount2,
                                billId,
                                customer,
                                total,
                                date
                        );

                    }

                    else if (position == 2) {

                        showTransaction(
                                cardRecentTransaction3,
                                tvRecentBill3,
                                tvRecentBillDate3,
                                tvRecentBillAmount3,
                                billId,
                                customer,
                                total,
                                date
                        );
                    }

                    position++;

                } while (
                        cursor.moveToNext()
                                && position < 3
                );
            }

            cursor.close();
        }
    }


    // =========================
    // SHOW SINGLE TRANSACTION
    // =========================

    private void showTransaction(
            MaterialCardView card,
            TextView bill,
            TextView dateText,
            TextView amount,
            int billId,
            String customer,
            double total,
            String date) {

        card.setVisibility(
                MaterialCardView.VISIBLE
        );

        bill.setText(
                "#" + billId + "  •  " + customer
        );

        dateText.setText(date);

        amount.setText(
                "₹" + format(total)
        );


        card.setOnClickListener(v -> {

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
    }


    // =========================
    // REFRESH
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