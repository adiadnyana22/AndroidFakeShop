package com.adi.finpro.recycler_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.finpro.R;
import com.adi.finpro.model.OrderDetail;

import java.util.List;

public class AdapterHistoryDetail extends RecyclerView.Adapter<AdapterHistoryDetail.ViewHolder> {

    List<OrderDetail> orderDetailList;
    Activity activity;

    public AdapterHistoryDetail(List<OrderDetail> orderDetailList, Activity activity) {
        this.orderDetailList = orderDetailList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.rv_history_detail_item, parent, false);

        return new AdapterHistoryDetail.ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderDetail data = orderDetailList.get(position);

        holder.product_name.setText(data.getProduct().getName());
        holder.product_quantity.setText(String.valueOf(data.getQuantity()));
        holder.product_price.setText("Rp. " + String.valueOf(data.getProduct().getPrice()));
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_name, product_quantity, product_price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            product_name = itemView.findViewById(R.id.product_name);
            product_quantity = itemView.findViewById(R.id.product_quantity);
            product_price = itemView.findViewById(R.id.product_price);
        }
    }
}
