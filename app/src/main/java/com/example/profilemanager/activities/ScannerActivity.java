package com.example.profilemanager.activities;
//package net.smallacademy.qrapp;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.profilemanager.databinding.ActivityScannerBinding;
import com.example.profilemanager.utilities.PreferenceManager;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;



public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;
    private PreferenceManager preferenceManager;
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    TextView resultData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        scannerView = binding.scannerView;
        codeScanner = new CodeScanner(this, scannerView);
        resultData = binding.resultsOfQr;

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultData.setText(result.getText());
                    }
                });
            }
        });
    }

    private void setListeners() {
        binding.icBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }


}