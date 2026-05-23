package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class RoleSelectionActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private MaterialButtonToggleGroup authModeToggle;
    private MaterialButtonToggleGroup roleToggle;
    private TextView tvAuthTitle;
    private TextView tvAuthSubtitle;
    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPhone;
    private TextInputEditText etLocation;
    private TextInputEditText etDescription;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;
    private Spinner spCategory;
    private View registerOnlyNameField;
    private View registerOnlyPhoneField;
    private View registerOnlyRoleGroup;
    private View registerOnlyConfirmPasswordField;
    private View orgFieldsGroup;
    private boolean registerMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        session = new SessionManager(this);
        if (session.hasRole("user")) {
            openDashboard(UserDashboardActivity.class);
            return;
        }
        if (session.hasRole("org")) {
            openDashboard(OrgDashboardActivity.class);
            return;
        }
        if (session.hasRole("admin")) {
            openDashboard(AdminDashboardActivity.class);
            return;
        }

        setContentView(R.layout.activity_role_selection);
        dbHelper = new DatabaseHelper(this);

        tvAuthTitle = findViewById(R.id.tvAuthTitle);
        tvAuthSubtitle = findViewById(R.id.tvAuthSubtitle);
        authModeToggle = findViewById(R.id.toggleAuthMode);
        roleToggle = findViewById(R.id.toggleRegisterRole);
        etName = findViewById(R.id.etAuthName);
        etEmail = findViewById(R.id.etAuthEmail);
        etPhone = findViewById(R.id.etAuthPhone);
        etLocation = findViewById(R.id.etAuthLocation);
        etDescription = findViewById(R.id.etAuthDescription);
        etPassword = findViewById(R.id.etAuthPassword);
        etConfirmPassword = findViewById(R.id.etAuthConfirmPassword);
        spCategory = findViewById(R.id.spAuthCategory);
        registerOnlyNameField = findViewById(R.id.tilAuthName);
        registerOnlyPhoneField = findViewById(R.id.tilAuthPhone);
        registerOnlyRoleGroup = findViewById(R.id.roleSelectionPanel);
        registerOnlyConfirmPasswordField = findViewById(R.id.tilAuthConfirmPassword);
        orgFieldsGroup = findViewById(R.id.orgRegisterFields);
        MaterialButton btnPrimaryAction = findViewById(R.id.btnAuthPrimaryAction);
        TextView tvSwitchMode = findViewById(R.id.tvSwitchAuthMode);

        String[] categories = {"Music", "Sports", "Tech", "Art", "Food", "Education", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        authModeToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            registerMode = checkedId == R.id.btnModeRegister;
            updateAuthModeUi();
        });

        roleToggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updateRoleUi();
            }
        });

        btnPrimaryAction.setOnClickListener(v -> {
            if (registerMode) {
                registerAccount();
            } else {
                login();
            }
        });

        tvSwitchMode.setOnClickListener(v ->
                authModeToggle.check(registerMode ? R.id.btnModeLogin : R.id.btnModeRegister));

        authModeToggle.check(R.id.btnModeLogin);
        roleToggle.check(R.id.btnRoleUser);
        updateAuthModeUi();
    }

    private void updateAuthModeUi() {
        int registerVisibility = registerMode ? View.VISIBLE : View.GONE;
        registerOnlyNameField.setVisibility(registerVisibility);
        registerOnlyPhoneField.setVisibility(registerVisibility);
        registerOnlyRoleGroup.setVisibility(registerVisibility);
        registerOnlyConfirmPasswordField.setVisibility(registerVisibility);
        orgFieldsGroup.setVisibility(registerMode && roleToggle.getCheckedButtonId() == R.id.btnRoleOrganizer ? View.VISIBLE : View.GONE);

        tvAuthTitle.setText(registerMode ? "Create your account" : "Welcome back");
        tvAuthSubtitle.setText(registerMode
                ? "Register as a guest or organizer from one centered screen."
                : "Use the same login for user, organizer, or admin access.");
        ((MaterialButton) findViewById(R.id.btnAuthPrimaryAction)).setText(registerMode ? "Create Account" : "Log In");
        ((TextView) findViewById(R.id.tvSwitchAuthMode)).setText(registerMode
                ? "Already have an account? Log in"
                : "Need an account? Register here");
    }

    private void updateRoleUi() {
        boolean organizer = roleToggle.getCheckedButtonId() == R.id.btnRoleOrganizer;
        orgFieldsGroup.setVisibility(registerMode && organizer ? View.VISIBLE : View.GONE);
        etName.setHint(organizer ? "Organizer or brand name" : "Full name");
    }

    private void login() {
        String email = String.valueOf(etEmail.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            toast("Please fill all fields");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Enter a valid email address");
            return;
        }

        if (dbHelper.authenticateAdmin(email, password)) {
            session.createLoginSession(0, "Administrator", "admin");
            openDashboard(AdminDashboardActivity.class);
            return;
        }

        User user = dbHelper.authenticateUser(email, password);
        if (user != null) {
            session.createLoginSession(user.getId(), user.getFullName(), "user");
            openDashboard(UserDashboardActivity.class);
            return;
        }

        Organization organization = dbHelper.authenticateOrganization(email, password);
        if (organization != null) {
            session.createLoginSession(organization.getId(), organization.getName(), "org");
            openDashboard(OrgDashboardActivity.class);
            return;
        }

        toast("Invalid credentials");
    }

    private void registerAccount() {
        String name = String.valueOf(etName.getText()).trim();
        String email = String.valueOf(etEmail.getText()).trim();
        String phone = String.valueOf(etPhone.getText()).trim();
        String password = String.valueOf(etPassword.getText()).trim();
        String confirmPassword = String.valueOf(etConfirmPassword.getText()).trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            toast("Please fill all required fields");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Enter a valid email address");
            return;
        }
        if (phone.length() < 10) {
            toast("Enter a valid phone number");
            return;
        }
        if (password.length() < 6) {
            toast("Password must be at least 6 characters");
            return;
        }
        if (!password.equals(confirmPassword)) {
            toast("Passwords do not match");
            return;
        }
        if (dbHelper.isAnyEmailTaken(email)) {
            toast("This email is already registered");
            return;
        }

        boolean organizer = roleToggle.getCheckedButtonId() == R.id.btnRoleOrganizer;
        if (organizer) {
            String location = String.valueOf(etLocation.getText()).trim();
            String description = String.valueOf(etDescription.getText()).trim();
            String category = spCategory.getSelectedItem().toString();
            if (location.isEmpty() || description.isEmpty()) {
                toast("Please complete the organizer profile");
                return;
            }

            long orgId = dbHelper.registerOrganization(name, email, password, phone, category, location, description);
            if (orgId == -1) {
                toast("Registration failed");
                return;
            }
            session.createLoginSession((int) orgId, name, "org");
            openDashboard(OrgDashboardActivity.class);
            return;
        }

        long userId = dbHelper.registerUser(name, email, password, phone);
        if (userId == -1) {
            toast("Registration failed");
            return;
        }
        session.createLoginSession((int) userId, name, "user");
        openDashboard(UserDashboardActivity.class);
    }

    private void openDashboard(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
