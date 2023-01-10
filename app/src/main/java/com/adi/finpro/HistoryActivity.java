package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.adi.finpro.model.Cart;
import com.adi.finpro.model.Order;
import com.adi.finpro.recycler_view.AdapterCart;
import com.adi.finpro.recycler_view.AdapterHistoryCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView rv_history_card;
    AdapterHistoryCard adapterHistoryCard;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    List<Order> orderList;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        rv_history_card = findViewById(R.id.rv_history_card);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_history_card.setLayoutManager(layoutManager);

        getOrder();
    }

    private void getOrder() {
        database.child("Order").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Order order = item.getValue(Order.class);
                    order.setKey(item.getKey());
                    if(order.getEmail().equals(mUser.getEmail())) {
                        orderList.add(order);
                    }
                }

                if(orderList != null && orderList.size() >= 1) {
                    adapterHistoryCard = new AdapterHistoryCard(orderList, HistoryActivity.this);
                    rv_history_card.setAdapter(adapterHistoryCard);
                } else {
                    adapterHistoryCard = new AdapterHistoryCard(new ArrayList<>(), HistoryActivity.this);
                    rv_history_card.setAdapter(adapterHistoryCard);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}