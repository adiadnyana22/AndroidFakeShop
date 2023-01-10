package com.adi.finpro.recycler_view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.finpro.AddActivity;
import com.adi.finpro.DetailActivity;
import com.adi.finpro.MainActivity;
import com.adi.finpro.R;
import com.adi.finpro.model.Product;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ViewHolder> {
    private List<Product> productList;
    private Activity activity;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public AdapterProduct(List<Product> productList, Activity activity) {
        this.productList = productList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.rv_data_item, parent, false);

        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product data = productList.get(position);

        Picasso.get().load(data.getImageLink()).into(holder.image_product);
        holder.text_name.setText(data.getName());
        holder.text_price.setText("Rp. "+ String.format("%,d", data.getPrice()));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DetailActivity.class);
                intent.putExtra("key", data.getKey());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_name, text_price;
        ImageView image_product;
        CardView card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_item);
            image_product = itemView.findViewById(R.id.image_product);
            text_name = itemView.findViewById(R.id.text_name);
            text_price = itemView.findViewById(R.id.text_price);
        }
    }
}
