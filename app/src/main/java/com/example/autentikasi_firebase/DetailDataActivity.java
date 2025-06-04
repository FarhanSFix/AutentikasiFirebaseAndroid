package com.example.autentikasi_firebase;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
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

    private InterstitialAd mInterstitialAd;

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

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus
                                                         initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712",
                adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd){
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });

        new Handler().postDelayed(() ->{
            if (mInterstitialAd != null){
                mInterstitialAd.show(DetailDataActivity.this);
            }else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        }, 1500);

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
