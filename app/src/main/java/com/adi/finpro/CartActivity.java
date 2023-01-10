package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adi.finpro.model.Cart;
import com.adi.finpro.model.OrderDetail;
import com.adi.finpro.model.Product;
import com.adi.finpro.recycler_view.AdapterCart;
import com.adi.finpro.recycler_view.AdapterProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    RecyclerView rv_cart;
    AdapterCart adapterCart;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    TextView text_total;
    Button btn_next;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Cart userCart = null;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        text_total = findViewById(R.id.text_total);
        btn_next = findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        rv_cart = findViewById(R.id.rv_cart);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_cart.setLayoutManager(layoutManager);

        getCart();
    }

    private void getCart() {
        database.child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Cart cart = item.getValue(Cart.class);
                    cart.setKey(item.getKey());
                    if(cart.getEmail().equals(mUser.getEmail())) {
                        userCart = cart.clone();
                        break;
                    }
                }

                if(userCart != null && userCart.getCartDetails() != null) {
                    adapterCart = new AdapterCart(userCart.getCartDetails(), CartActivity.this, userCart);
                    rv_cart.setAdapter(adapterCart);

                    printTotal();
                } else {
                    adapterCart = new AdapterCart(new ArrayList<>(), CartActivity.this, new Cart());
                    rv_cart.setAdapter(adapterCart);

                    text_total.setText("Rp. ");
                    btn_next.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void printTotal() {
        total = 0;
        for (OrderDetail od : userCart.getCartDetails()) {
            total += od.getQuantity() * od.getProduct().getPrice();
        }

        text_total.setText("Rp. " + String.format("%,d", total));
        btn_next.setEnabled(true);
    }
}