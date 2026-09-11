package com.exp1.billeasy;
import android.content.Intent;

import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class TransactionsActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    RecyclerView recyclerTransactions;

    DatabaseHelper databaseHelper;

    ArrayList<Transaction> transactions =
            new ArrayList<>();

    TransactionAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_transactions
        );


        btnBack =
                findViewById(R.id.btnBack);

        recyclerTransactions =
                findViewById(
                        R.id.recyclerTransactions
                );


        databaseHelper =
                new DatabaseHelper(this);


        btnBack.setOnClickListener(
                v -> finish()
        );


        recyclerTransactions.setLayoutManager(
                new LinearLayoutManager(this)
        );


        loadTransactions();
    }


    private void loadTransactions() {

        transactions.clear();


        Cursor cursor =
                databaseHelper.getAllBills();


        if (cursor.moveToFirst()) {

            do {

                int id =
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


                transactions.add(
                        new Transaction(
                                id,
                                customer,
                                total,
                                date
                        )
                );

            } while (cursor.moveToNext());
        }


        cursor.close();


        adapter =
                new TransactionAdapter(
                        transactions,
                        transaction -> {

                            Intent intent = new Intent(
                                    TransactionsActivity.this,
                                    BillDetailsActivity.class
                            );

                            intent.putExtra(
                                    "bill_id",
                                    transaction.getId()
                            );

                            startActivity(intent);

                        }
                );


        recyclerTransactions.setAdapter(
                adapter
        );
    }
}