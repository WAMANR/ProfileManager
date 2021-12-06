package com.example.profilemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.profilemanager.databinding.ActivityScannerBinding;
import com.example.profilemanager.utilities.Constants;
import com.example.profilemanager.utilities.PreferenceManager;


public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.icBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
        binding.QrCodeTest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            preferenceManager.putBoolean(Constants.KEY_USER_PROFILE, false);
            startActivity(intent);
            finish();
        });
    }
}