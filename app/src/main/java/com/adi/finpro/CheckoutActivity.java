package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adi.finpro.fragment.MapFragment;
import com.adi.finpro.model.Cart;
import com.adi.finpro.model.Order;
import com.adi.finpro.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CheckoutActivity extends AppCompatActivity {

    EditText input_name, input_email, input_phone_number, input_address_detail;
    Button btn_checkout;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    Cart userCart = null;

    double latitude = 0, longitude =  0;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_phone_number = findViewById(R.id.input_phone_number);
        input_address_detail = findViewById(R.id.input_address_detail);
        btn_checkout = findViewById(R.id.btn_checkout);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        input_email.setText(mUser.getEmail());

        Fragment fragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

        btn_checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkout();
            }
        });

        getCart();
    }

    public void passingLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private void checkout() {
        String name = input_name.getText().toString();
        String email = input_email.getText().toString();
        String phoneNumber = input_phone_number.getText().toString();
        String addressDetail = input_address_detail.getText().toString();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());

        if(name.isEmpty()) {
            input_name.setError("Enter your name");
        } else if(!email.matches(emailPattern)) {
            input_email.setError("Enter correct email");
        } else if(phoneNumber.isEmpty()) {
            input_phone_number.setError("Enter your phone number");
        } else if(addressDetail.isEmpty()) {
            input_address_detail.setError("Enter your address detail");
        } else {
            database.child("Order").push().setValue(new Order(name, email, phoneNumber, addressDetail, latitude, longitude, date, userCart.getCartDetails())).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(CheckoutActivity.this, "Checkout Success", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(CheckoutActivity.this, MainActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CheckoutActivity.this, "Checkout Failed", Toast.LENGTH_SHORT).show();
                }
            });

            database.child("Cart").child(userCart.getKey()).removeValue();
        }
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
}