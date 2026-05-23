package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OrgProfileActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etPhone;
    private TextInputEditText etLocation;
    private TextInputEditText etDesc;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_profile);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etName = findViewById(R.id.etOrgProfName);
        etPhone = findViewById(R.id.etOrgProfPhone);
        etLocation = findViewById(R.id.etOrgProfLoc);
        etDesc = findViewById(R.id.etOrgProfDesc);
        MaterialButton btnUpdate = findViewById(R.id.btnOrgUpdateProf);
        MaterialButton btnLogout = findViewById(R.id.btnOrgLogout);
        loadOrgData();

        btnUpdate.setOnClickListener(v -> updateProfile());
        btnLogout.setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        TopNavHelper.setupOrgNav(this, R.id.btnNavOrgProfile);
    }

    private void loadOrgData() {
        Organization organization = dbHelper.getOrganizationById(session.getUserId());
        if (organization == null) {
            return;
        }
        etName.setText(organization.getName());
        etPhone.setText(organization.getPhone());
        etLocation.setText(organization.getLocation());
        etDesc.setText(dbHelper.getOrganizationDescription(session.getUserId()));
    }

    private void updateProfile() {
        String name = String.valueOf(etName.getText()).trim();
        String phone = String.valueOf(etPhone.getText()).trim();
        String location = String.valueOf(etLocation.getText()).trim();
        String desc = String.valueOf(etDesc.getText()).trim();

        if (name.isEmpty() || phone.isEmpty() || location.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 10) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dbHelper.updateOrganizationProfile(session.getUserId(), name, phone, desc, location)) {
            Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
            return;
        }

        session.createLoginSession(session.getUserId(), name, "org");
        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
