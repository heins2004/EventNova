package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = new SessionManager(this);
            Intent intent;
            if (session.hasRole("user")) {
                intent = new Intent(this, UserDashboardActivity.class);
            } else if (session.hasRole("org")) {
                intent = new Intent(this, OrgDashboardActivity.class);
            } else if (session.hasRole("admin")) {
                intent = new Intent(this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(this, RoleSelectionActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}
