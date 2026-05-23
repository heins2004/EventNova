package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLoginActivity extends AppCompatActivity {

    private TextInputEditText etUsername;
    private TextInputEditText etPassword;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etUsername = findViewById(R.id.etAdminUsername);
        etPassword = findViewById(R.id.etAdminPassword);
        MaterialButton btnLogin = findViewById(R.id.btnAdminLogin);
        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String user = String.valueOf(etUsername.getText()).trim();
        String pass = String.valueOf(etPassword.getText()).trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!dbHelper.authenticateAdmin(user, pass)) {
            Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        session.createLoginSession(0, "Administrator", "admin");
        startActivity(new Intent(this, AdminDashboardActivity.class));
        finish();
    }
}
