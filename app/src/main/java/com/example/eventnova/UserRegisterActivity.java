package com.example.eventnova;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserRegisterActivity extends AppCompatActivity {

    private TextInputEditText etFullName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        dbHelper = new DatabaseHelper(this);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        MaterialButton btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerUser());
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String fullName = String.valueOf(etFullName.getText()).trim();
        String email = String.valueOf(etEmail.getText()).trim();
        String phone = String.valueOf(etPhone.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();
        String confirmPassword = String.valueOf(etConfirmPassword.getText()).trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dbHelper.isUserEmailTaken(email)) {
            Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        long newRowId = dbHelper.registerUser(fullName, email, password, phone);
        if (newRowId == -1) {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
        finish();
    }
}
