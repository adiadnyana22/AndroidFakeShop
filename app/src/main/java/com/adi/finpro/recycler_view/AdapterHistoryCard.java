package com.adi.finpro.recycler_view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.finpro.HistoryActivity;
import com.adi.finpro.R;
import com.adi.finpro.model.Order;
import com.adi.finpro.model.OrderDetail;

import java.util.List;

public class AdapterHistoryCard extends RecyclerView.Adapter<AdapterHistoryCard.ViewHolder> {

    List<Order> orderList;
    Activity activity;

    public AdapterHistoryCard(List<Order> orderList, Activity activity) {
        this.orderList = orderList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.rv_history_card_item, parent, false);

        return new AdapterHistoryCard.ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Order data = orderList.get(position);

        holder.text_date.setText(data.getDate());

        int total = 0;
        for (OrderDetail od: data.getOrderDetails()) {
            total += od.getQuantity() * od.getProduct().getPrice();
        }
        holder.text_total.setText("Rp. " + String.valueOf(total));

        AdapterHistoryDetail adapterHistoryDetail = new AdapterHistoryDetail(data.getOrderDetails(), activity);
        holder.rv_history_detail.setAdapter(adapterHistoryDetail);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_history_detail;
        TextView text_date, text_total;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rv_history_detail = itemView.findViewById(R.id.rv_history_detail);
            text_date = itemView.findViewById(R.id.text_date);
            text_total = itemView.findViewById(R.id.text_total);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
            rv_history_detail.setLayoutManager(layoutManager);
        }
    }
}
