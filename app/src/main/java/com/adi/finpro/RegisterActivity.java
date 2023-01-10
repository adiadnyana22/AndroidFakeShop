package com.adi.finpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    TextView login;
    EditText input_email, input_password, input_confirm_password;
    Button btn_register;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = findViewById(R.id.login);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_confirm_password = findViewById(R.id.input_confirm_password);
        btn_register = findViewById(R.id.btn_register);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mUser != null) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
        }
    }

    private void register() {
        String email = input_email.getText().toString();
        String password = input_password.getText().toString();
        String confirm_password = input_confirm_password.getText().toString();

        if(!email.matches(emailPattern)) {
            input_email.setError("Enter correct email");
        } else if(password.isEmpty() || password.length() < 4) {
            input_password.setError("Password must contain at least 4 characters");
        } else if(!password.equals(confirm_password)) {
            input_confirm_password.setError("Password not match");
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    } else {
                        Toast.makeText(RegisterActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}