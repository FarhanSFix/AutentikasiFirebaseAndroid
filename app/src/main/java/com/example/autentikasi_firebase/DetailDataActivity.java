package com.example.autentikasi_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DetailDataActivity extends AppCompatActivity {

    EditText editNama, editUmur;
    Button btnUpdate, btnDelete;
    DatabaseReference dbRef;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_data);

        key = getIntent().getStringExtra("DATA_KEY");
        dbRef = FirebaseDatabase.getInstance().getReference("data").child(key);

        editNama = findViewById(R.id.editNama);
        editUmur = findViewById(R.id.editUmur);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                editNama.setText(snapshot.child("nama").getValue(String.class));
                editUmur.setText(snapshot.child("umur").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnUpdate.setOnClickListener(v -> {
            dbRef.child("nama").setValue(editNama.getText().toString());
            dbRef.child("umur").setValue(editUmur.getText().toString());
            Toast.makeText(this, "Data diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnDelete.setOnClickListener(v -> {
            dbRef.removeValue();
            Toast.makeText(this, "Data dihapus", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
