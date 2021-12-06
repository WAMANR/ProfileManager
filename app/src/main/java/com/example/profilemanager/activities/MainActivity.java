package com.example.profilemanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.profilemanager.R;
import com.example.profilemanager.databinding.ActivityMainBinding;
import com.example.profilemanager.databinding.ActivityScannerBinding;
import com.example.profilemanager.utilities.Constants;
import com.example.profilemanager.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        if(!preferenceManager.getBoolean(Constants.KEY_USER_PROFILE)){
            binding.icLogout.setVisibility(View.INVISIBLE);
            binding.icScanner.setVisibility(View.INVISIBLE);
            binding.icDisplayQr.setVisibility(View.INVISIBLE);
            binding.icSave.setVisibility(View.INVISIBLE);
            binding.icBack.setVisibility(View.VISIBLE);
            binding.icShare.setVisibility(View.VISIBLE);

        }
        setContentView(binding.getRoot());
        if(preferenceManager.getBoolean(Constants.KEY_USER_PROFILE)) loadProfile(preferenceManager.getString(Constants.KEY_USER_ID));
        else {
            Bundle extras = getIntent().getExtras();
            loadProfile(extras.getString("id"));
            disableEditText();
        }
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
       binding.icScanner.setOnClickListener(v ->{
           Intent intent = new Intent(getApplicationContext(), ScannerActivity.class);
           startActivity(intent);
               });
       binding.icBack.setOnClickListener(v -> {
           Intent intent = new Intent(getApplicationContext(), MainActivity.class);
           preferenceManager.putBoolean(Constants.KEY_USER_PROFILE, true);
           startActivity(intent);
           finish();
       });
       binding.icSave.setOnClickListener(v ->
               saveProfile());
       binding.icDisplayQr.setOnClickListener(v -> {
           binding.profileScrollView.setVisibility(View.INVISIBLE);
           binding.icLogout.setVisibility(View.INVISIBLE);
           binding.icBack.setVisibility(View.VISIBLE);
           binding.qrCodeDisplay.setVisibility(View.VISIBLE);
           try {
               genQrCode();
           } catch (WriterException e) {
               e.printStackTrace();
           }
       });
    }

    private void loadProfile(String id){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()){
                        //exist
                        Map documentData = documentSnapshot.getData();
                        assert documentData != null;
                        enterUserData(documentData);
                    }
                    //don't exist
                }
                //task unsuccessful
            }
        });

    }

    private void enterUserData(Map documentData){

        if(documentData.get("firstname") != null) binding.editFirstname.setText(Objects.requireNonNull(documentData.get("firstname")).toString());
        if(documentData.get("lastname") != null) binding.editLastname.setText(documentData.get("lastname").toString());
        if(documentData.get("usename") != null) binding.editUsename.setText(documentData.get("usename").toString());
        if(documentData.get("birthDate") != null) binding.editBirthDate.setText(documentData.get("birthDate").toString());
        if(documentData.get("textNationalityOther") != null) binding.editNationalityOther.setText(documentData.get("textNationalityOther").toString());
        if(documentData.get("birthCity") != null) binding.editBirthCity.setText(documentData.get("birthCity").toString());
        if(documentData.get("birthCounty") != null) binding.editBirthCounty.setText(documentData.get("birthCounty").toString());
        if(documentData.get("birthOther") != null) binding.editBirthOther.setText(documentData.get("birthOther").toString());
        if(documentData.get("fullAdress") != null) binding.editFullAdress.setText(documentData.get("fullAdress").toString());
        if(documentData.get("postalCode") != null) binding.editPostalCode.setText(documentData.get("postalCode").toString());
        if(documentData.get("city") != null) binding.editCity.setText(documentData.get("city").toString());
        if(documentData.get("country") != null) binding.editCountry.setText(documentData.get("country").toString());
        if(documentData.get("phoneNumber") != null) binding.editPhoneNumber.setText(documentData.get("phoneNumber").toString());
        if(documentData.get("mail") != null) binding.editMailAdress.setText(documentData.get("mail").toString());

        if(documentData.get("supervisory") != null) {
            if (documentData.get("supervisory").equals(true))
                binding.checkboxSupervisory.setChecked(true);
            else binding.checkBoxSupervised.setChecked(true);
        }

        if(documentData.get("nationalityFrench") != null) {
            if (documentData.get("nationalityFrench").equals(true))
                binding.checkboxNationalityFrench.setChecked(true);
            else binding.checkboxNationalityOther.setChecked(true);
        }

        if(documentData.get("birthInFrance") != null) {
            if (documentData.get("birthInFrance").equals(true))
                binding.checkboxBirthFrance.setChecked(true);
            else binding.checkboxBirthOther.setChecked(true);
        }

        if(documentData.get("gender") != null) {
            if (documentData.get("gender").equals("male")) binding.checkboxMale.setChecked(true);
            else if (documentData.get("gender").equals("female"))
                binding.checkboxFemale.setChecked(true);
            else binding.checkBoxOther.setChecked(true);
        }
    }

    private void saveProfile(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(
                "firstname", binding.editFirstname.getText().toString(),
                "lastname", binding.editLastname.getText().toString(),
                "usename", binding.editUsename.getText().toString(),
                "birthDate", binding.editBirthDate.getText().toString(),
                "birthCity", binding.editBirthCity.getText().toString(),
                "birthCounty", binding.editBirthCounty.getText().toString(),
                "birthOther", binding.editBirthOther.getText().toString(),
                "textNationalityOther", binding.editNationalityOther.getText().toString(),
                "fullAdress", binding.editFullAdress.getText().toString(),
                "postalCode", binding.editPostalCode.getText().toString(),
                "city", binding.editCity.getText().toString(),
                "country", binding.editCountry.getText().toString(),
                "phoneNumber", binding.editPhoneNumber.getText().toString(),
                "mail", binding.editMailAdress.getText().toString()
        );

        if(binding.checkboxSupervisory.isChecked()) documentReference.update("supervisory", true);
        else documentReference.update("supervisory", false);

        if(binding.checkboxNationalityFrench.isChecked()) documentReference.update("nationalityFrench", true);
        else documentReference.update("nationalityFrench", false);

        if(binding.checkboxBirthFrance.isChecked()) documentReference.update("birthInFrance", true);
        else documentReference.update("birthInFrance", false);

        if(binding.checkboxMale.isChecked()) documentReference.update("gender", "male");
        else if(binding.checkboxFemale.isChecked()) documentReference.update("gender", "female");
        else if(binding.checkBoxOther.isChecked()) documentReference.update("gender", "other");
        Toast.makeText(getApplicationContext(), "Profile Update successful", Toast.LENGTH_SHORT).show();
    }

    private void genQrCode() throws WriterException {
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix matrix = writer.encode(preferenceManager.getString(Constants.KEY_USER_ID), BarcodeFormat.QR_CODE
        , 350, 350);
        BarcodeEncoder encoder = new BarcodeEncoder();
        Bitmap bitmap = encoder.createBitmap(matrix);
        binding.qrCodeDisplay.setImageBitmap(bitmap);

    }

    private void disableEditText(){

        //editText
        binding.editFirstname.setFocusable(false);
        binding.editLastname.setFocusable(false);
        binding.editUsename.setFocusable(false);
        binding.editBirthDate.setFocusable(false);
        binding.editNationalityOther.setFocusable(false);
        binding.editBirthCity.setFocusable(false);
        binding.editBirthCounty.setFocusable(false);
        binding.editBirthOther.setFocusable(false);
        binding.editFullAdress.setFocusable(false);
        binding.editPostalCode.setFocusable(false);
        binding.editCity.setFocusable(false);
        binding.editCountry.setFocusable(false);
        binding.editPhoneNumber.setFocusable(false);
        binding.editMailAdress.setFocusable(false);
        //Checkbox
        binding.checkboxSupervisory.setEnabled(false);
        binding.checkBoxSupervised.setEnabled(false);
        binding.checkboxMale.setEnabled(false);
        binding.checkboxFemale.setEnabled(false);
        binding.checkBoxOther.setEnabled(false);
        binding.checkboxNationalityOther.setEnabled(false);
        binding.checkboxNationalityFrench.setEnabled(false);
        binding.checkboxBirthFrance.setEnabled(false);
        binding.checkboxBirthOther.setEnabled(false);
    }

}