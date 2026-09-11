package com.exp1.billeasy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface CartListener {

        void onQuantityChanged();

        void onRemove(int position);
    }

    private final ArrayList<CartItem> cart;
    private final CartListener listener;

    public CartAdapter(
            ArrayList<CartItem> cart,
            CartListener listener) {

        this.cart = cart;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_cart,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        CartItem item = cart.get(position);

        holder.tvName.setText(item.getProductName());

        holder.tvPrice.setText(
                "₹" + String.format("%.2f", item.getPrice())
        );

        holder.tvQuantity.setText(
                String.valueOf(item.getQuantity())
        );

        holder.tvTotal.setText(
                "₹" + String.format("%.2f", item.getTotal())
        );


        // PLUS BUTTON

        holder.btnPlus.setOnClickListener(v -> {

            int currentQuantity = item.getQuantity();
            int availableStock = item.getAvailableStock();

            if (currentQuantity < availableStock) {

                item.setQuantity(currentQuantity + 1);

                notifyItemChanged(holder.getAdapterPosition());

                listener.onQuantityChanged();

            } else {

                Toast.makeText(
                        v.getContext(),
                        "Only " + availableStock + " item(s) available",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });


        // MINUS BUTTON

        holder.btnMinus.setOnClickListener(v -> {

            int currentQuantity = item.getQuantity();

            if (currentQuantity > 1) {

                item.setQuantity(currentQuantity - 1);

                notifyItemChanged(holder.getAdapterPosition());

                listener.onQuantityChanged();

            } else {

                int adapterPosition =
                        holder.getAdapterPosition();

                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener.onRemove(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cart.size();
    }


    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvPrice;
        TextView tvQuantity;
        TextView tvTotal;

        MaterialButton btnPlus;
        MaterialButton btnMinus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvProductName
            );

            tvPrice = itemView.findViewById(
                    R.id.tvPrice
            );

            tvQuantity = itemView.findViewById(
                    R.id.tvQuantity
            );

            tvTotal = itemView.findViewById(
                    R.id.tvTotal
            );

            btnPlus = itemView.findViewById(
                    R.id.btnPlus
            );

            btnMinus = itemView.findViewById(
                    R.id.btnMinus
            );
        }
    }
}