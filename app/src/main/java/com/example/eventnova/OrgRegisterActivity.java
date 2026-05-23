package com.example.eventnova;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OrgRegisterActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etLocation;
    private TextInputEditText etDesc;
    private TextInputEditText etPassword;
    private Spinner spCategory;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_register);

        dbHelper = new DatabaseHelper(this);

        etName = findViewById(R.id.etOrgName);
        etEmail = findViewById(R.id.etOrgRegEmail);
        etPhone = findViewById(R.id.etOrgPhone);
        etLocation = findViewById(R.id.etOrgLocation);
        etDesc = findViewById(R.id.etOrgDesc);
        etPassword = findViewById(R.id.etOrgRegPassword);
        spCategory = findViewById(R.id.spOrgCategory);
        MaterialButton btnRegister = findViewById(R.id.btnOrgRegister);

        String[] categories = {"Music", "Sports", "Tech", "Art", "Food", "Education", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        btnRegister.setOnClickListener(v -> registerOrg());
        findViewById(R.id.tvGoToOrgLogin).setOnClickListener(v -> finish());
    }

    private void registerOrg() {
        String name = String.valueOf(etName.getText()).trim();
        String email = String.valueOf(etEmail.getText()).trim();
        String phone = String.valueOf(etPhone.getText()).trim();
        String location = String.valueOf(etLocation.getText()).trim();
        String desc = String.valueOf(etDesc.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();
        String category = spCategory.getSelectedItem().toString();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || location.isEmpty() || desc.isEmpty() || password.isEmpty()) {
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
        if (dbHelper.isOrganizationEmailTaken(email)) {
            Toast.makeText(this, "Email is already registered", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = dbHelper.registerOrganization(name, email, password, phone, category, location, desc);
        if (id == -1) {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Organization registered", Toast.LENGTH_SHORT).show();
        finish();
    }
}
