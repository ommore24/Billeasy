package com.exp1.billeasy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exp1.billeasy.R;
import com.exp1.billeasy.models.BillItem;

import java.util.ArrayList;
import java.util.Locale;

public class BillItemAdapter
        extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {

    private final ArrayList<BillItem> items;

    public BillItemAdapter(ArrayList<BillItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_bill_detail,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        BillItem item = items.get(position);

        holder.tvProductName.setText(
                item.getProductName()
        );

        holder.tvQuantity.setText(
                "Qty: " + item.getQuantity()
        );

        holder.tvPrice.setText(
                "₹" + format(item.getPrice())
        );

        holder.tvTotal.setText(
                "₹" + format(item.getTotal())
        );
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String format(double value) {

        return String.format(
                Locale.getDefault(),
                "%.2f",
                value
        );
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvProductName;
        TextView tvQuantity;
        TextView tvPrice;
        TextView tvTotal;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(
                    R.id.tvProductName
            );

            tvQuantity = itemView.findViewById(
                    R.id.tvQuantity
            );

            tvPrice = itemView.findViewById(
                    R.id.tvPrice
            );

            tvTotal = itemView.findViewById(
                    R.id.tvTotal
            );
        }
    }
}