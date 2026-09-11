package com.exp1.billeasy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class BillItemAdapter
        extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {

    private ArrayList<BillItem> items;


    public BillItemAdapter(
            ArrayList<BillItem> items) {

        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
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

        BillItem item =
                items.get(position);


        holder.tvName.setText(
                item.getName()
        );


        holder.tvQuantity.setText(
                "Qty: " + item.getQuantity()
        );


        holder.tvPrice.setText(
                "₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                item.getPrice()
                        )
        );


        holder.tvTotal.setText(
                "₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                item.getTotal()
                        )
        );
    }


    @Override
    public int getItemCount() {

        return items.size();
    }


    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvQuantity;
        TextView tvPrice;
        TextView tvTotal;


        ViewHolder(@NonNull View itemView) {

            super(itemView);


            tvName =
                    itemView.findViewById(
                            R.id.tvName
                    );

            tvQuantity =
                    itemView.findViewById(
                            R.id.tvQuantity
                    );

            tvPrice =
                    itemView.findViewById(
                            R.id.tvPrice
                    );

            tvTotal =
                    itemView.findViewById(
                            R.id.tvTotal
                    );
        }
    }
}