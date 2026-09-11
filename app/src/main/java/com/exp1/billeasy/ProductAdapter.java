package com.exp1.billeasy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter
        extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface ProductClickListener {
        void onProductClick(Product product);
    }

    private final ArrayList<Product> productList;
    private final Context context;
    private final ProductClickListener listener;

    // Existing constructor used by Inventory
    public ProductAdapter(
            Context context,
            ArrayList<Product> productList) {

        this.context = context;
        this.productList = productList;
        this.listener = null;
    }

    // Constructor used by New Bill
    public ProductAdapter(
            Context context,
            ArrayList<Product> productList,
            ProductClickListener listener) {

        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_product,
                        parent,
                        false
                );

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ProductViewHolder holder,
            int position) {

        Product product = productList.get(position);

        holder.tvName.setText(
                product.getName()
        );

        holder.tvCategory.setText(
                product.getCategory()
        );

        holder.tvPrice.setText(
                "₹" + String.format("%.2f", product.getPrice())
        );

        holder.tvStock.setText(
                String.valueOf(product.getQuantity())
        );

        holder.itemView.setOnClickListener(v -> {

            // Billing mode
            if (listener != null) {

                listener.onProductClick(product);

                return;
            }

            // Inventory mode
            Intent intent = new Intent(
                    context,
                    ProductDetailsActivity.class
            );

            intent.putExtra(
                    "product_id",
                    product.getId()
            );

            intent.putExtra(
                    "product_name",
                    product.getName()
            );

            intent.putExtra(
                    "product_category",
                    product.getCategory()
            );

            intent.putExtra(
                    "product_price",
                    product.getPrice()
            );

            intent.putExtra(
                    "product_quantity",
                    product.getQuantity()
            );

            intent.putExtra(
                    "product_supplier",
                    product.getSupplier() == null
                            ? ""
                            : product.getSupplier()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCategory;
        TextView tvPrice;
        TextView tvStock;

        public ProductViewHolder(
                @NonNull View itemView) {

            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvProductName
            );

            tvCategory = itemView.findViewById(
                    R.id.tvCategory
            );

            tvPrice = itemView.findViewById(
                    R.id.tvPrice
            );

            tvStock = itemView.findViewById(
                    R.id.tvStock
            );
        }
    }
}