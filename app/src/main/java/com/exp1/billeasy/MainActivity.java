package com.exp1.billeasy;
import android.content.Intent;

import android.os.Bundle;
import android.widget.Toast;

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

        cardInventory = findViewById(R.id.cardInventory);
        cardBilling = findViewById(R.id.cardBilling);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        cardBilling.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    NewBillActivity.class
            );

            startActivity(intent);
        });

        // Inventory button
        cardInventory.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    InventoryActivity.class
            );

            startActivity(intent);

        });



        // Bottom navigation
        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                return true;

            } else if (id == R.id.nav_inventory) {

                Toast.makeText(
                        MainActivity.this,
                        "Inventory",
                        Toast.LENGTH_SHORT
                ).show();

                return true;

            } else if (id == R.id.nav_bills) {

                Toast.makeText(
                        MainActivity.this,
                        "Bills",
                        Toast.LENGTH_SHORT
                ).show();

                return true;

            } else if (id == R.id.nav_profile) {

                Toast.makeText(
                        MainActivity.this,
                        "Profile",
                        Toast.LENGTH_SHORT
                ).show();

                return true;
            }

            return false;
        });
    }
}