package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class OrgLoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_login);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etEmail = findViewById(R.id.etOrgEmail);
        etPassword = findViewById(R.id.etOrgPassword);
        MaterialButton btnLogin = findViewById(R.id.btnOrgLogin);
        TextView tvGoToRegister = findViewById(R.id.tvGoToOrgRegister);

        btnLogin.setOnClickListener(v -> login());
        tvGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, OrgRegisterActivity.class)));
    }

    private void login() {
        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        Organization org = dbHelper.authenticateOrganization(email, password);
        if (org == null) {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        session.createLoginSession(org.getId(), org.getName(), "org");
        startActivity(new Intent(this, OrgDashboardActivity.class));
        finish();
    }
}
