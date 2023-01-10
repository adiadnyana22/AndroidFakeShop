package com.adi.finpro.recycler_view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.adi.finpro.DetailActivity;
import com.adi.finpro.R;
import com.adi.finpro.model.Cart;
import com.adi.finpro.model.OrderDetail;
import com.adi.finpro.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {
    private List<OrderDetail> orderDetailList;
    private Activity activity;
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    private Cart userCart;

    public AdapterCart(List<OrderDetail> orderDetailList, Activity activity, Cart userCart) {
        this.orderDetailList = orderDetailList;
        this.activity = activity;
        this.userCart = userCart;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View viewItem = inflater.inflate(R.layout.rv_cart_item, parent, false);

        return new AdapterCart.ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrderDetail data = orderDetailList.get(position);

        Picasso.get().load(data.getProduct().getImageLink()).into(holder.image_product);
        holder.text_name.setText(data.getProduct().getName());
        holder.text_price.setText("Rp. "+ String.format("%,d", data.getProduct().getPrice()));
        holder.text_quantity.setText("Quantity : " + String.valueOf(data.getQuantity()));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (int j = 0; j < userCart.getCartDetails().size(); j++){
                            if(userCart.getCartDetails().get(j).getProduct().getName().equals(data.getProduct().getName())) {
                                userCart.getCartDetails().remove(j);
                                break;
                            }
                        }

                        database.child("Cart").child(userCart.getKey()).child("cartDetails").setValue(userCart.getCartDetails()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(activity, "Delete product from cart success", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Delete product from cart failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).setMessage("Delete product from cart\n" + "Product : " + data.getProduct().getName());
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_name, text_price, text_quantity;
        ImageView image_product, delete;
        CardView card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            card_view = itemView.findViewById(R.id.card_item);
            image_product = itemView.findViewById(R.id.image_product);
            text_name = itemView.findViewById(R.id.text_name);
            text_price = itemView.findViewById(R.id.text_price);
            text_quantity = itemView.findViewById(R.id.text_quantity);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
