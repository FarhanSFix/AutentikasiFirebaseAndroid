package com.example.autentikasi_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TambahDataActivity extends AppCompatActivity {
    EditText editNama, editUmur;
    Button btnSimpan;
    DatabaseReference dbRef;

    //inisialisasi (sebelum OnCreate)
    private RewardedAd rewardedAd;
    //activitynya sesuaikan nama di project masing2
    private final String TAG = "TambahDataActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data);

        dbRef = FirebaseDatabase.getInstance().getReference("data");
        editNama = findViewById(R.id.editNama);
        editUmur = findViewById(R.id.editUmur);
        btnSimpan = findViewById(R.id.btnSimpan);

        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.toString());
                        rewardedAd = null;
                    }
                    @Override
                    public void onAdLoaded(@NonNull RewardedAd ad) {
                        rewardedAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                    }
                });

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
                if (rewardedAd != null) {
                    Activity activityContext = TambahDataActivity.this;
                    rewardedAd.show(activityContext, rewardItem -> {
                        // Handle the reward.
                        Log.d(TAG, "The user earned the reward.");
                        int rewardAmount = rewardItem.getAmount();
                        String rewardType = rewardItem.getType();
                    });
                } else {
                    Log.d(TAG, "The rewarded ad wasn't ready yet.");
                }
                Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            });
        });
    }
}
