package com.exp1.billeasy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter
        extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnTransactionClickListener {
        void onClick(Transaction transaction);
    }


    private ArrayList<Transaction> transactions;

    private OnTransactionClickListener listener;


    public TransactionAdapter(
            ArrayList<Transaction> transactions,
            OnTransactionClickListener listener) {

        this.transactions = transactions;
        this.listener = listener;
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
                        R.layout.item_transaction,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Transaction transaction =
                transactions.get(position);


        holder.tvBillNumber.setText(
                "#" + transaction.getId()
        );


        holder.tvCustomer.setText(
                transaction.getCustomerName()
        );


        holder.tvDate.setText(
                transaction.getDate()
        );


        holder.tvTotal.setText(
                "₹" +
                        String.format(
                                Locale.getDefault(),
                                "%.2f",
                                transaction.getTotal()
                        )
        );


        holder.itemView.setOnClickListener(
                v -> listener.onClick(transaction)
        );
    }


    @Override
    public int getItemCount() {

        return transactions.size();
    }


    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvBillNumber;
        TextView tvCustomer;
        TextView tvDate;
        TextView tvTotal;


        ViewHolder(@NonNull View itemView) {

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
        }
    }
}