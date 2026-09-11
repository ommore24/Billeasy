package com.exp1.billeasy;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    MaterialCardView cardInventory;
    MaterialCardView cardBilling;
    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Find views
        cardInventory = findViewById(R.id.cardInventory);
        cardBilling = findViewById(R.id.cardBilling);
        bottomNavigation = findViewById(R.id.bottomNavigation);


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


            // HOME
            if (id == R.id.nav_home) {

                // Already on Home
                return true;
            }


            // INVENTORY
            else if (id == R.id.nav_inventory) {

                Intent intent = new Intent(
                        MainActivity.this,
                        InventoryActivity.class
                );

                startActivity(intent);

                return true;
            }


            // BILLS / TRANSACTIONS
            else if (id == R.id.nav_bills) {

                Intent intent = new Intent(
                        MainActivity.this,
                        TransactionsActivity.class
                );

                startActivity(intent);

                return true;
            }


            // PROFILE
            else if (id == R.id.nav_profile) {

                // Profile module can be added later
                return true;
            }


            return false;
        });
    }
}