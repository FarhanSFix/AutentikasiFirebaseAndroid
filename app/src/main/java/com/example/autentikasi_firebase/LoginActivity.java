package com.example.autentikasi_firebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    EditText edtIdentifier, edtPassword;
    Button btnLogin;

    TextView btnRegister, btnReset;
    FirebaseAuth auth;
    DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inisialisasi view
        edtIdentifier = findViewById(R.id.edtIdentifier); // bisa email atau username
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnReset = findViewById(R.id.btnResetPassword);

        // Inisialisasi Firebase
        auth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Tombol Register dan Reset Password
        btnRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
        btnReset.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        // Tombol Login
        btnLogin.setOnClickListener(v -> {
            String identifier = edtIdentifier.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            if (identifier.contains("@")) {
                // Jika input mengandung @, anggap sebagai email
                loginWithEmail(identifier, password);
            } else {
                // Kalau tidak, cari email dari username
                findEmailFromUsername(identifier, password);
            }
        });
    }

    private void findEmailFromUsername(String username, String password) {
        usersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                String email = userSnap.child("email").getValue(String.class);
                                if (email != null) {
                                    loginWithEmail(email, password);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Email tidak ditemukan", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loginWithEmail(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Email belum diverifikasi", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    } else {
                        String errorMsg = "Login gagal";
                        if (task.getException() != null) {
                            errorMsg += ": " + task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


