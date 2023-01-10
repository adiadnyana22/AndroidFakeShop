package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adi.finpro.model.Cart;
import com.adi.finpro.model.OrderDetail;
import com.adi.finpro.model.Product;
import com.adi.finpro.recycler_view.AdapterProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    TextView text_name, text_category, text_price, text_description;
    ImageView image_product;
    EditText input_quantity;
    Button btn_plus, btn_minus, btn_add_to_cart;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    Product productDetail;
    Cart userCart = null;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        text_name = findViewById(R.id.text_name);
        text_category = findViewById(R.id.text_category);
        text_price = findViewById(R.id.text_price);
        text_description = findViewById(R.id.text_description);
        image_product = findViewById(R.id.image_product);
        input_quantity = findViewById(R.id.input_quantity);
        btn_minus = findViewById(R.id.btn_minus);
        btn_plus = findViewById(R.id.btn_plus);
        btn_add_to_cart = findViewById(R.id.btn_add_to_cart);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(input_quantity.getText().toString()) > 1)
                    input_quantity.setText(String.valueOf(Integer.parseInt(input_quantity.getText().toString()) - 1));
            }
        });

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(input_quantity.getText().toString()) < 100)
                    input_quantity.setText(String.valueOf(Integer.parseInt(input_quantity.getText().toString()) + 1));
            }
        });

        btn_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

        getProductFromDatabase();
        getCart();
    }

    private void getProductFromDatabase() {
        database.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    product.setKey(item.getKey());
                    if(product.getKey().equals(getIntent().getStringExtra("key"))) {
                        productDetail = product.clone();
                        Picasso.get().load(product.getImageLink()).into(image_product);
                        text_name.setText(product.getName());
                        text_category.setText(product.getCategory());
                        text_price.setText("Rp. "+ String.format("%,d", product.getPrice()));
                        text_description.setText(product.getDescription());
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addToCart() {
        if(userCart == null){
            Cart cart = new Cart();
            cart.setEmail(mUser.getEmail());
            cart.setCartDetails(new ArrayList<>());
            cart.getCartDetails().add(new OrderDetail(productDetail, Integer.parseInt(input_quantity.getText().toString())));

            database.child("Cart").push().setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetailActivity.this, "Add to cart success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, "Add to cart failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            boolean flag = false;
            if(userCart.getCartDetails() == null) userCart.setCartDetails(new ArrayList<>());
            List<OrderDetail> cartList = userCart.getCartDetails();

            for (int i = 0; i < cartList.size(); i++) {
                if(cartList.get(i).getProduct().getKey().equals(productDetail.getKey())){
                    cartList.get(i).setQuantity(cartList.get(i).getQuantity() + Integer.parseInt(input_quantity.getText().toString()));
                    flag = true;
                }
            }

            if(!flag) {
                cartList.add(new OrderDetail(productDetail, Integer.parseInt(input_quantity.getText().toString())));
            }

            Cart updatedCart = userCart.clone();
            updatedCart.setKey(null);

            database.child("Cart").child(userCart.getKey()).setValue(updatedCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DetailActivity.this, "Add to cart success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(DetailActivity.this, "Add to cart failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}