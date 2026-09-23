package com.exp1.billeasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.exp1.billeasy.R;
import com.exp1.billeasy.models.Product;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class ProductBillingAdapter
        extends RecyclerView.Adapter<ProductBillingAdapter.ViewHolder> {

    public interface ProductListener {
        void onProductSelected(Product product);
    }

    private final Context context;
    private final ArrayList<Product> productList;
    private final ProductListener listener;

    public ProductBillingAdapter(
            Context context,
            ArrayList<Product> productList,
            ProductListener listener) {

        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.item_product_billing,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Product product =
                productList.get(position);

        holder.tvName.setText(
                product.getName()
        );

        holder.tvCategory.setText(
                product.getCategory()
        );

        holder.tvPrice.setText(
                "₹" +
                        String.format(
                                "%.2f",
                                product.getPrice()
                        )
        );

        holder.tvStock.setText(
                "Stock: " +
                        product.getQuantity()
        );


        holder.btnAdd.setOnClickListener(v -> {

            listener.onProductSelected(product);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCategory;
        TextView tvPrice;
        TextView tvStock;

        MaterialButton btnAdd;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            tvName =
                    itemView.findViewById(
                            R.id.tvProductName
                    );

            tvCategory =
                    itemView.findViewById(
                            R.id.tvCategory
                    );

            tvPrice =
                    itemView.findViewById(
                            R.id.tvPrice
                    );

            tvStock =
                    itemView.findViewById(
                            R.id.tvStock
                    );

            btnAdd =
                    itemView.findViewById(
                            R.id.btnAdd
                    );
        }
    }
}