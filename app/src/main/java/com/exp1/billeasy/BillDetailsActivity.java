package com.exp1.billeasy;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class BillDetailsActivity extends AppCompatActivity {

    MaterialCardView btnBack;

    TextView tvBillNumber;
    TextView tvCustomer;
    TextView tvDate;
    TextView tvSubtotal;
    TextView tvDiscount;
    TextView tvGst;
    TextView tvGrandTotal;

    RecyclerView recyclerBillItems;

    DatabaseHelper databaseHelper;

    ArrayList<BillItem> billItems =
            new ArrayList<>();

    BillItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_bill_details
        );


        btnBack =
                findViewById(R.id.btnBack);

        tvBillNumber =
                findViewById(R.id.tvBillNumber);

        tvCustomer =
                findViewById(R.id.tvCustomer);

        tvDate =
                findViewById(R.id.tvDate);

        tvSubtotal =
                findViewById(R.id.tvSubtotal);

        tvDiscount =
                findViewById(R.id.tvDiscount);

        tvGst =
                findViewById(R.id.tvGst);

        tvGrandTotal =
                findViewById(R.id.tvGrandTotal);

        recyclerBillItems =
                findViewById(
                        R.id.recyclerBillItems
                );


        databaseHelper =
                new DatabaseHelper(this);


        btnBack.setOnClickListener(
                v -> finish()
        );


        int billId =
                getIntent().getIntExtra(
                        "bill_id",
                        -1
                );


        if (billId == -1) {

            finish();

            return;
        }


        recyclerBillItems.setLayoutManager(
                new LinearLayoutManager(this)
        );


        loadBill(billId);

        loadBillItems(billId);
    }


    private void loadBill(int billId) {

        Cursor cursor =
                databaseHelper.getBill(billId);


        if (cursor.moveToFirst()) {

            double subtotal =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "subtotal"
                            )
                    );

            double discount =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "discount"
                            )
                    );

            double gst =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "gst"
                            )
                    );

            double total =
                    cursor.getDouble(
                            cursor.getColumnIndexOrThrow(
                                    "total"
                            )
                    );

            String customer =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "customer_name"
                            )
                    );

            String date =
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    "date"
                            )
                    );


            tvBillNumber.setText(
                    "BILL #" + billId
            );

            tvCustomer.setText(
                    customer
            );

            tvDate.setText(
                    date
            );

            tvSubtotal.setText(
                    "Subtotal     ₹" +
                            format(subtotal)
            );

            tvDiscount.setText(
                    "Discount     ₹" +
                            format(discount)
            );

            tvGst.setText(
                    "GST           ₹" +
                            format(gst)
            );

            tvGrandTotal.setText(
                    "₹" + format(total)
            );
        }


        cursor.close();
    }


    private void loadBillItems(int billId) {

        billItems.clear();


        Cursor cursor =
                databaseHelper.getBillItems(
                        billId
                );


        if (cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "id"
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "product_name"
                                )
                        );

                double price =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "price"
                                )
                        );

                int quantity =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "quantity"
                                )
                        );

                double total =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "total"
                                )
                        );


                billItems.add(
                        new BillItem(
                                id,
                                name,
                                price,
                                quantity,
                                total
                        )
                );


            } while (cursor.moveToNext());
        }


        cursor.close();


        adapter =
                new BillItemAdapter(
                        billItems
                );

        recyclerBillItems.setAdapter(
                adapter
        );
    }


    private String format(double value) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                value
        );
    }
}