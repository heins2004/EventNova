package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class UserProfileActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etPhone;
    private TextInputEditText etNewPassword;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.etProfileName);
        etPhone = findViewById(R.id.etProfilePhone);
        etNewPassword = findViewById(R.id.etProfilePassword);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdateProfile);
        MaterialButton btnLogout = findViewById(R.id.btnLogout);
        loadUserData();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnLogout.setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        TopNavHelper.setupUserNav(this, R.id.btnNavUserProfile);
    }

    private void loadUserData() {
        User user = dbHelper.getUserById(session.getUserId());
        if (user == null) {
            return;
        }
        etName.setText(user.getFullName());
        etPhone.setText(user.getPhone());
    }

    private void updateProfile() {
        String name = String.valueOf(etName.getText()).trim();
        String phone = String.valueOf(etPhone.getText()).trim();
        String password = String.valueOf(etNewPassword.getText()).trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.isEmpty() && password.length() < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dbHelper.updateUserProfile(session.getUserId(), name, phone, password)) {
            Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
            return;
        }

        session.createLoginSession(session.getUserId(), name, "user");
        etNewPassword.setText("");
        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
