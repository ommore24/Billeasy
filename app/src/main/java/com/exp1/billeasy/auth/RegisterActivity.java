package com.exp1.billeasy.auth;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.exp1.billeasy.R;
import com.exp1.billeasy.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText etRegisterUsername, etRegisterPassword, etRegisterConfirmPassword;
    MaterialButton btnRegisterSubmit;
    TextView tvBackToLogin;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterConfirmPassword = findViewById(R.id.etRegisterConfirmPassword);
        btnRegisterSubmit = findViewById(R.id.btnRegisterSubmit);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        btnRegisterSubmit.setOnClickListener(v -> performRegistration());
        tvBackToLogin.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String username = etRegisterUsername.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();
        String confirmPassword = etRegisterConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please enter details into all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.setError("Passwords do not match");
            return;
        }

        boolean success = db.registerUser(username, password);
        if (success) {
            Toast.makeText(this, "Account created successfully! Please login.", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Username already exists!", Toast.LENGTH_SHORT).show();
        }
    }
}