package com.exp1.billeasy.auth;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exp1.billeasy.R;
import com.exp1.billeasy.analytics.AnalyticsActivity;
import com.exp1.billeasy.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    MaterialCardView btnBack;
    TextView btnEditToggle;
    MaterialButton btnSaveProfile, btnCancelEdit, btnAnalytics, btnLocateOnMap, btnLogout;
    
    // View Mode elements
    LinearLayout layoutViewMode;
    TextView tvDisplayStoreNameHeader, tvDisplayOwnerNameHeader;
    TextView tvViewStoreName, tvViewOwnerName, tvViewPhone, tvViewGST, tvViewAddress;

    // Edit Mode elements
    LinearLayout layoutEditMode;
    TextInputEditText etStoreName, etOwnerName, etPhone, etGST, etAddress;
    
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db = new DatabaseHelper(this);

        // Header and Toggles
        btnBack = findViewById(R.id.btnBack);
        btnEditToggle = findViewById(R.id.btnEditToggle);
        tvDisplayStoreNameHeader = findViewById(R.id.tvDisplayStoreNameHeader);
        tvDisplayOwnerNameHeader = findViewById(R.id.tvDisplayOwnerNameHeader);

        // View Mode
        layoutViewMode = findViewById(R.id.layoutViewMode);
        tvViewStoreName = findViewById(R.id.tvViewStoreName);
        tvViewOwnerName = findViewById(R.id.tvViewOwnerName);
        tvViewPhone = findViewById(R.id.tvViewPhone);
        tvViewGST = findViewById(R.id.tvViewGST);
        tvViewAddress = findViewById(R.id.tvViewAddress);

        // Edit Mode
        layoutEditMode = findViewById(R.id.layoutEditMode);
        etStoreName = findViewById(R.id.etStoreName);
        etOwnerName = findViewById(R.id.etOwnerName);
        etPhone = findViewById(R.id.etPhone);
        etGST = findViewById(R.id.etGST);
        etAddress = findViewById(R.id.etAddress);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnCancelEdit = findViewById(R.id.btnCancelEdit);

        // Actions
        btnAnalytics = findViewById(R.id.btnAnalytics);
        btnLocateOnMap = findViewById(R.id.btnLocateOnMap);
        btnLogout = findViewById(R.id.btnLogout);

        // Initial Data Load
        loadProfileData();
        showViewMode();

        // Listeners
        btnBack.setOnClickListener(v -> finish());
        
        btnEditToggle.setOnClickListener(v -> showEditMode());
        
        btnCancelEdit.setOnClickListener(v -> showViewMode());

        btnSaveProfile.setOnClickListener(v -> saveProfile());

        btnAnalytics.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AnalyticsActivity.class);
            startActivity(intent);
        });

        btnLocateOnMap.setOnClickListener(v -> locateOnMap());

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void showViewMode() {
        layoutViewMode.setVisibility(View.VISIBLE);
        layoutEditMode.setVisibility(View.GONE);
        btnEditToggle.setVisibility(View.VISIBLE);
    }

    private void showEditMode() {
        layoutViewMode.setVisibility(View.GONE);
        layoutEditMode.setVisibility(View.VISIBLE);
        btnEditToggle.setVisibility(View.GONE);
        
        // Prep edit fields with current data
        etStoreName.setText(tvViewStoreName.getText().toString().equals("Not provided") ? "" : tvViewStoreName.getText());
        etOwnerName.setText(tvViewOwnerName.getText().toString().equals("Not provided") ? "" : tvViewOwnerName.getText());
        etPhone.setText(tvViewPhone.getText().toString().equals("Not provided") ? "" : tvViewPhone.getText());
        etGST.setText(tvViewGST.getText().toString().equals("Not provided") ? "" : tvViewGST.getText());
        etAddress.setText(tvViewAddress.getText().toString().equals("Not provided") ? "" : tvViewAddress.getText());
    }

    private void loadProfileData() {
        Cursor cursor = db.getStoreProfile();
        if (cursor != null && cursor.moveToFirst()) {
            String store = cursor.getString(cursor.getColumnIndexOrThrow("store_name"));
            String owner = cursor.getString(cursor.getColumnIndexOrThrow("owner_name"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String gst = cursor.getString(cursor.getColumnIndexOrThrow("gst_number"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));

            tvDisplayStoreNameHeader.setText(store != null && !store.isEmpty() ? store : "BillEasy Store");
            tvDisplayOwnerNameHeader.setText(owner == null || owner.isEmpty() ? "Store Owner" : owner);

            tvViewStoreName.setText(store == null || store.isEmpty() ? "Not provided" : store);
            tvViewOwnerName.setText(owner == null || owner.isEmpty() ? "Not provided" : owner);
            tvViewPhone.setText(phone == null || phone.isEmpty() ? "Not provided" : phone);
            tvViewGST.setText(gst == null || gst.isEmpty() ? "Not provided" : gst);
            tvViewAddress.setText(address == null || address.isEmpty() ? "Not provided" : address);
            
            cursor.close();
        }
    }

    private void saveProfile() {
        String storeName = etStoreName.getText().toString().trim();
        String ownerName = etOwnerName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String gst = etGST.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (storeName.isEmpty()) {
            etStoreName.setError("Store name is required");
            return;
        }

        boolean success = db.updateProfile(storeName, ownerName, phone, address, gst);
        if (success) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            loadProfileData(); // Sync labels
            showViewMode();    // Return to view state
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void locateOnMap() {
        String address = tvViewAddress.getText().toString().trim();
        if (address.isEmpty() || address.equals("Not provided")) {
            Toast.makeText(this, "Please enter an address in Edit mode first", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Google Maps not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    getSharedPreferences("BillEasyPrefs", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();

                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}