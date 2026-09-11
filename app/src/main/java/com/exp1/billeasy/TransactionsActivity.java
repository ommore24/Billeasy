package com.exp1.billeasy;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    private RecyclerView recyclerTransactions;
    private TextView tvEmpty;
    private MaterialCardView btnBack;

    private DatabaseHelper db;
    private ArrayList<Bill> billList;
    private BillAdapter billAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_transactions);

        // Views
        recyclerTransactions = findViewById(R.id.recyclerTransactions);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnBack = findViewById(R.id.btnBack);

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Database
        db = new DatabaseHelper(this);

        // RecyclerView
        recyclerTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadBills();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (db != null) {
            loadBills();
        }
    }

    private void loadBills() {

        billList = new ArrayList<>();

        Cursor cursor = db.getAllBills();

        if (cursor != null) {

            if (cursor.moveToFirst()) {

                do {

                    int id = cursor.getInt(
                            cursor.getColumnIndexOrThrow("id")
                    );

                    String customerName = cursor.getString(
                            cursor.getColumnIndexOrThrow("customer_name")
                    );

                    double subtotal = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("subtotal")
                    );

                    double discount = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("discount")
                    );

                    double gst = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("gst")
                    );

                    double total = cursor.getDouble(
                            cursor.getColumnIndexOrThrow("total")
                    );

                    String date = cursor.getString(
                            cursor.getColumnIndexOrThrow("date")
                    );

                    billList.add(
                            new Bill(
                                    id,
                                    customerName,
                                    subtotal,
                                    discount,
                                    gst,
                                    total,
                                    date
                            )
                    );

                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        // Empty state
        if (billList.isEmpty()) {

            recyclerTransactions.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.VISIBLE);

        } else {

            recyclerTransactions.setVisibility(View.VISIBLE);
            tvEmpty.setVisibility(View.GONE);

            billAdapter = new BillAdapter(
                    this,
                    billList
            );

            recyclerTransactions.setAdapter(billAdapter);
        }
    }
}