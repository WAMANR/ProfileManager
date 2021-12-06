package com.example.profilemanager.activities;
//package net.smallacademy.qrapp;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.profilemanager.databinding.ActivityScannerBinding;
import com.example.profilemanager.utilities.PreferenceManager;
import com.google.zxing.Result;



public class ScannerActivity extends AppCompatActivity {

    private ActivityScannerBinding binding;
    private PreferenceManager preferenceManager;
    TextView resultData;
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        CodeScannerView scannerView = binding.scannerView;
        mCodeScanner = new CodeScanner(this, scannerView);
        resultData = binding.resultsOfQr;

        mCodeScanner.setDecodeCallback(new DecodeCallback() {
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
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void setListeners() {
        binding.icBack.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
        /*
        binding.QrCodeTest.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            preferenceManager.putBoolean(Constants.KEY_USER_PROFILE, false);
            startActivity(intent);
            finish();
        });
         */
    }


}