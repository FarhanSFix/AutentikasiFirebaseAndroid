package com.example.autentikasi_firebase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.appcompat.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DatabaseReference dbRef;
    FirebaseAuth auth;
    ListView listView;
    ArrayList<String> dataList;
    ArrayList<String> keyList;
    ArrayAdapter<String> adapter;
    FloatingActionButton fab;
    Toolbar toolbar;

    SearchView searchView;
    ArrayList<String> fullDataList = new ArrayList<>();
    ArrayList<String> fullKeyList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null || !currentUser.isEmailVerified()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.searchView);
        dbRef = FirebaseDatabase.getInstance().getReference("data");
        listView = findViewById(R.id.listViewData);
        fab = findViewById(R.id.fab);

        dataList = new ArrayList<>();
        keyList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        fab.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TambahDataActivity.class)));

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String key = keyList.get(position);
            Intent intent = new Intent(MainActivity.this, DetailDataActivity.class);
            intent.putExtra("DATA_KEY", key);
            startActivity(intent);
        });

        loadData();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                return true;
            }
        });
    }


    private void loadData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fullDataList.clear();
                fullKeyList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String nama = child.child("nama").getValue(String.class);
                    String umur = child.child("umur").getValue(String.class);
                    fullDataList.add("Nama: " + nama + ", Umur: " + umur);
                    fullKeyList.add(child.getKey());
                }
                dataList.clear();
                dataList.addAll(fullDataList);
                keyList.clear();
                keyList.addAll(fullKeyList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void filterData(String query) {
        dataList.clear();
        keyList.clear();

        for (int i = 0; i < fullDataList.size(); i++) {
            String item = fullDataList.get(i);
            if (item.toLowerCase().contains(query.toLowerCase())) {
                dataList.add(item);
                keyList.add(fullKeyList.get(i));
            }
        }

        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            auth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}