package com.example.profilemanager.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.profilemanager.R;
import com.example.profilemanager.databinding.ActivitySignInBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_sign_in);
        setListeners();
        addDataToFirestore();
    }

    private void setListeners(){
        //binding.element.setOnClickListerner(v -> {});
    }

    private void addDataToFirestore(){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("first_name", "Théo");
        data.put("last_name", "Montaigu");
        database.collection("users")
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getApplicationContext(), "Data Inserted", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}