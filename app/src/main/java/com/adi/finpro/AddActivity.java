package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.adi.finpro.model.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivity extends AppCompatActivity {

    EditText input_image, input_name, input_price, input_description;
    Spinner input_category;
    Button btn_submit;
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        input_image = findViewById(R.id.input_image);
        input_name = findViewById(R.id.input_name);
        input_price = findViewById(R.id.input_price);
        btn_submit = findViewById(R.id.btn_submit);
        input_category = findViewById(R.id.input_category);
        input_description = findViewById(R.id.input_description);

        String[] items = new String[] { "Fashion", "Electronic", "Food", "Beauty" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, items);
        input_category.setAdapter(adapter);

        input_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String image = input_image.getText().toString();
                String name = input_name.getText().toString();
                String category = input_category.getSelectedItem().toString();
                String description = input_description.getText().toString();
                int price = Integer.parseInt(input_price.getText().toString());

                if(image.isEmpty()){
                    input_image.setError("Enter image link");
                } else if(name.isEmpty()){
                    input_name.setError("Enter name");
                } else if(price == 0){
                    input_price.setError("Enter price");
                } else {
                    database.child("Product").push().setValue(new Product(image, name, category, description, price)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AddActivity.this, "Add Product Success", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AddActivity.this, MainActivity.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddActivity.this, "Add Product Failed", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}