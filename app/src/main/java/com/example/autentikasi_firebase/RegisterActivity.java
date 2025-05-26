package com.example.autentikasi_firebase;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    EditText edtUsername, edtEmail, edtPassword;
    Button btnRegister;
    FirebaseAuth auth;
    TextView loginText;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        loginText = findViewById(R.id.loginText);

        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        btnRegister.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            user.sendEmailVerification().addOnCompleteListener(emailTask -> {
                                if (emailTask.isSuccessful()) {
                                    String uid = user.getUid();
                                    usersRef.child(uid).setValue(new User(username, email));

                                    Toast.makeText(this, "Registrasi berhasil. Cek email untuk verifikasi.", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        loginText.setOnClickListener(v ->  startActivity(new Intent(this, LoginActivity.class)));
    }

    public static class User {
        public String username, email;

        public User() {}
        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }
    }
}
