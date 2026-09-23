package com.exp1.billeasy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exp1.billeasy.billing.BillDetailsActivity;
import com.exp1.billeasy.database.DatabaseHelper;
import com.exp1.billeasy.R;
import com.exp1.billeasy.models.Bill;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Locale;

public class BillAdapter
        extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private final Context context;
    private final ArrayList<Bill> billList;
    private final DatabaseHelper databaseHelper;

    public BillAdapter(
            Context context,
            ArrayList<Bill> billList) {

        this.context = context;
        this.billList = billList;
        this.databaseHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_bill,
                        parent,
                        false
                );

        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull BillViewHolder holder,
            int position) {

        Bill bill = billList.get(position);

        // =========================
        // BILL NUMBER
        // =========================

        holder.tvBillNumber.setText(
                "Bill #" + bill.getId()
        );


        // =========================
        // CUSTOMER
        // =========================

        String customer =
                bill.getCustomerName();

        if (customer == null ||
                customer.trim().isEmpty()) {

            customer = "Walk-in Customer";
        }

        holder.tvCustomer.setText(
                customer
        );


        // =========================
        // DATE
        // =========================

        holder.tvDate.setText(
                bill.getDate()
        );


        // =========================
        // TOTAL
        // =========================

        holder.tvTotal.setText(
                "₹" + String.format(
                        Locale.getDefault(),
                        "%.2f",
                        bill.getTotal()
                )
        );


        // =========================
        // OPEN BILL DETAILS
        // =========================

        holder.itemView.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            context,
                            BillDetailsActivity.class
                    );

            intent.putExtra(
                    "bill_id",
                    bill.getId()
            );

            context.startActivity(intent);
        });


        // =========================
        // DELETE BILL
        // =========================

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Bill?")
                    .setMessage(
                            "Are you sure you want to delete Bill #" +
                                    bill.getId() +
                                    "?\n\nThis action cannot be undone."
                    )
                    .setNegativeButton(
                            "Cancel",
                            null
                    )
                    .setPositiveButton(
                            "Delete",
                            (dialog, which) -> {

                                deleteBill(
                                        bill.getId(),
                                        holder.getAdapterPosition() // Fixed: changed from getBindingAdapterPosition()
                                );
                            }
                    )
                    .show();
        });
    }


    // =========================
    // DELETE BILL
    // =========================

    private void deleteBill(
            int billId,
            int position) {

        if (position == RecyclerView.NO_POSITION) {
            return;
        }


        boolean deleted =
                databaseHelper.deleteBill(billId);


        if (deleted) {

            billList.remove(position);

            notifyItemRemoved(position);

            Toast.makeText(
                    context,
                    "Bill deleted successfully",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            Toast.makeText(
                    context,
                    "Failed to delete bill",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    @Override
    public int getItemCount() {
        return billList.size();
    }

    public void updateList(ArrayList<Bill> newList) {
        billList.clear();
        billList.addAll(newList);
        notifyDataSetChanged();
    }

    // =========================
    // VIEW HOLDER
    // =========================

    public static class BillViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvBillNumber;
        TextView tvCustomer;
        TextView tvDate;
        TextView tvTotal;

        MaterialCardView btnDelete;


        public BillViewHolder(
                @NonNull View itemView) {

            super(itemView);


            tvBillNumber =
                    itemView.findViewById(
                            R.id.tvBillNumber
                    );

            tvCustomer =
                    itemView.findViewById(
                            R.id.tvCustomer
                    );

            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );

            tvTotal =
                    itemView.findViewById(
                            R.id.tvTotal
                    );

            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}