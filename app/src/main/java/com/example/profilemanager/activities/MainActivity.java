package com.example.profilemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.profilemanager.R;
import com.example.profilemanager.databinding.ActivityMainBinding;
import com.example.profilemanager.utilities.Constants;
import com.example.profilemanager.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
       binding.icLogout.setOnClickListener(v -> binding.logoutConfirmation.setVisibility(View.VISIBLE));
       binding.logoutNo.setOnClickListener(v -> binding.logoutConfirmation.setVisibility(View.INVISIBLE));
       binding.logoutYes.setOnClickListener(v -> {
           preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, false);
           Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
           startActivity(intent);
           finish();
       });
       binding.icScanner.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), ScannerActivity.class)));
    }


}