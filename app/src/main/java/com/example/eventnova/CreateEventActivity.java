package com.example.eventnova;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class CreateEventActivity extends AppCompatActivity {

    private TextInputEditText etTitle;
    private TextInputEditText etDesc;
    private TextInputEditText etLocation;
    private TextInputEditText etDate;
    private TextInputEditText etTime;
    private TextInputEditText etPrice;
    private TextInputEditText etSeats;
    private Spinner spCategory;
    private ImageView ivEventPreview;
    private TextView tvEventImageLabel;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private String selectedImageUri = "";
    private final ActivityResultLauncher<String[]> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri == null) {
                    return;
                }
                try {
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException ignored) {
                }
                selectedImageUri = uri.toString();
                updateSelectedImage(Uri.parse(selectedImageUri));
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        etTitle = findViewById(R.id.etCreateTitle);
        etDesc = findViewById(R.id.etCreateDesc);
        etLocation = findViewById(R.id.etCreateLocation);
        etDate = findViewById(R.id.etCreateDate);
        etTime = findViewById(R.id.etCreateTime);
        etPrice = findViewById(R.id.etCreatePrice);
        etSeats = findViewById(R.id.etCreateSeats);
        spCategory = findViewById(R.id.spCategory);
        ivEventPreview = findViewById(R.id.ivCreateEventPreview);
        tvEventImageLabel = findViewById(R.id.tvCreateEventImageLabel);
        MaterialButton btnSubmit = findViewById(R.id.btnSubmitEvent);
        MaterialButton btnPickImage = findViewById(R.id.btnPickCreateEventImage);
        String[] categories = {"Music", "Sports", "Tech", "Art", "Food", "Education", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch(new String[]{"image/*"}));
        btnSubmit.setOnClickListener(v -> createEvent());
        TopNavHelper.setupOrgNav(this, R.id.btnNavOrgEvents);
    }

    private void createEvent() {
        String title = String.valueOf(etTitle.getText()).trim();
        String desc = String.valueOf(etDesc.getText()).trim();
        String category = spCategory.getSelectedItem().toString();
        String location = String.valueOf(etLocation.getText()).trim();
        String date = String.valueOf(etDate.getText()).trim();
        String time = String.valueOf(etTime.getText()).trim();
        String priceStr = String.valueOf(etPrice.getText()).trim();
        String seatsStr = String.valueOf(etSeats.getText()).trim();

        if (title.isEmpty() || desc.isEmpty() || location.isEmpty() || date.isEmpty() || time.isEmpty() || priceStr.isEmpty() || seatsStr.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int seats = Integer.parseInt(seatsStr);
            if (price < 0 || seats <= 0) {
                Toast.makeText(this, "Price and seat count must be valid", Toast.LENGTH_SHORT).show();
                return;
            }
            long id = dbHelper.createEvent(session.getUserId(), title, desc, category, location, date, time, price, seats, selectedImageUri);
            if (id == -1) {
                Toast.makeText(this, "Failed to create event", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Event created successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "Enter valid numeric values", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                etDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) ->
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void updateSelectedImage(Uri imageUri) {
        ivEventPreview.setImageURI(imageUri);
        if (ivEventPreview.getDrawable() != null) {
            tvEventImageLabel.setText("Selected image");
        } else {
            ivEventPreview.setImageResource(EventUtils.getEventImageRes(spCategory.getSelectedItem().toString()));
            tvEventImageLabel.setText("Could not load the selected image");
        }
    }
}
