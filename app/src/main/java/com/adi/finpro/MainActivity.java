package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.adi.finpro.model.Product;
import com.adi.finpro.recycler_view.AdapterProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton btn_cart;
    RecyclerView rv_product;
    AdapterProduct adapterProduct;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    List<Product> productList;
    EditText input_search;
    Button btn_search, btn_fashion, btn_electronic, btn_food, btn_beauty;
    Button currCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_cart = findViewById(R.id.btn_cart);
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        input_search = findViewById(R.id.input_search);
        input_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    btn_search.performClick();
                }
                return false;
            }
        });
        btn_search = findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!input_search.getText().toString().isEmpty()){
                    searchProductFromDatabase(input_search.getText().toString());
                } else {
                    getProductFromDatabase();
                }
            }
        });

        btn_fashion = findViewById(R.id.btn_fashion);
        btn_electronic = findViewById(R.id.btn_electronic);
        btn_food = findViewById(R.id.btn_food);
        btn_beauty = findViewById(R.id.btn_beauty);

        rv_product = findViewById(R.id.rv_data);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        rv_product.setLayoutManager(layoutManager);
        rv_product.setItemAnimator(new DefaultItemAnimator());

        getProductFromDatabase();
    }

    private void getProductFromDatabase() {
        database.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    product.setKey(item.getKey());
                    if(currCategory != null) {
                        if (product.getCategory().equals(currCategory.getText().toString()))
                            productList.add(product);
                    } else {
                        productList.add(product);
                    }
                }

                adapterProduct = new AdapterProduct(productList, MainActivity.this);
                rv_product.setAdapter(adapterProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(menu instanceof MenuBuilder){
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_appbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_item_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.menu_item_history:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void searchProductFromDatabase(String searchData) {
        database.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    product.setKey(item.getKey());
                    if(currCategory != null) {
                        if (product.getName().toLowerCase().contains(searchData.toLowerCase()) && product.getCategory().equals(currCategory.getText().toString()))
                            productList.add(product);
                    } else {
                        if (product.getName().toLowerCase().contains(searchData.toLowerCase()))
                            productList.add(product);
                    }
                }

                adapterProduct = new AdapterProduct(productList, MainActivity.this);
                rv_product.setAdapter(adapterProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void categoryChange(View view) {
        if(currCategory != null && currCategory.equals(view)){
            currCategory.setBackgroundColor(Color.GRAY);
            currCategory = null;

            if(!input_search.getText().toString().isEmpty()){
                searchProductFromDatabase(input_search.getText().toString());
            } else {
                getProductFromDatabase();
            }
        } else {
            if (currCategory != null) currCategory.setBackgroundColor(Color.GRAY);
            currCategory = (Button) view;
            view.setBackgroundColor(Color.BLACK);
            if(input_search.getText().toString().isEmpty()){
                database.child("Product").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Product product = item.getValue(Product.class);
                            product.setKey(item.getKey());
                            if(product.getCategory().equals(((Button) view).getText().toString())) productList.add(product);
                        }

                        adapterProduct = new AdapterProduct(productList, MainActivity.this);
                        rv_product.setAdapter(adapterProduct);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                database.child("Product").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Product product = item.getValue(Product.class);
                            product.setKey(item.getKey());
                            if(product.getName().toLowerCase().contains(input_search.getText().toString().toLowerCase()) && product.getCategory().equals(((Button) view).getText().toString())) productList.add(product);
                        }

                        adapterProduct = new AdapterProduct(productList, MainActivity.this);
                        rv_product.setAdapter(adapterProduct);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
}