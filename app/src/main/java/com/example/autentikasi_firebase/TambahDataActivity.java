package com.example.autentikasi_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.*;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TambahDataActivity extends AppCompatActivity {
    EditText editNama, editUmur;
    Button btnSimpan;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        dbRef = FirebaseDatabase.getInstance().getReference("data");
        editNama = findViewById(R.id.editNama);
        editUmur = findViewById(R.id.editUmur);
        btnSimpan = findViewById(R.id.btnSimpan);

        btnSimpan.setOnClickListener(v -> {
            String nama = editNama.getText().toString();
            String umur = editUmur.getText().toString();

            if (nama.isEmpty() || umur.isEmpty()) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> data = new HashMap<>();
            data.put("nama", nama);
            data.put("umur", umur);

            dbRef.push().setValue(data).addOnSuccessListener(unused -> {
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
