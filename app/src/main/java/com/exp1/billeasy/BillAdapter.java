package com.exp1.billeasy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class BillAdapter
        extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private final Context context;
    private final ArrayList<Bill> billList;

    public BillAdapter(
            Context context,
            ArrayList<Bill> billList) {

        this.context = context;
        this.billList = billList;
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

        holder.tvBillNumber.setText(
                "Bill #" + bill.getId()
        );

        String customer = bill.getCustomerName();

        if (customer == null || customer.trim().isEmpty()) {
            customer = "Walk-in Customer";
        }

        holder.tvCustomer.setText(customer);

        holder.tvDate.setText(
                bill.getDate()
        );

        holder.tvTotal.setText(
                "₹" + String.format(
                        "%.2f",
                        bill.getTotal()
                )
        );

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    BillDetailsActivity.class
            );

            intent.putExtra(
                    "bill_id",
                    bill.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return billList.size();
    }

    public static class BillViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvBillNumber;
        TextView tvCustomer;
        TextView tvDate;
        TextView tvTotal;

        public BillViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvBillNumber = itemView.findViewById(
                    R.id.tvBillNumber
            );

            tvCustomer = itemView.findViewById(
                    R.id.tvCustomer
            );

            tvDate = itemView.findViewById(
                    R.id.tvDate
            );

            tvTotal = itemView.findViewById(
                    R.id.tvTotal
            );
        }
    }
}