package com.example.eventnova;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

public class EditEventActivity extends AppCompatActivity {

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
    private int eventId;
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
        setContentView(R.layout.activity_edit_event);

        dbHelper = new DatabaseHelper(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        etTitle = findViewById(R.id.etEditTitle);
        etDesc = findViewById(R.id.etEditDesc);
        etLocation = findViewById(R.id.etEditLocation);
        etDate = findViewById(R.id.etEditDate);
        etTime = findViewById(R.id.etEditTime);
        etPrice = findViewById(R.id.etEditPrice);
        etSeats = findViewById(R.id.etEditSeats);
        spCategory = findViewById(R.id.spEditCategory);
        ivEventPreview = findViewById(R.id.ivEditEventPreview);
        tvEventImageLabel = findViewById(R.id.tvEditEventImageLabel);
        MaterialButton btnUpdate = findViewById(R.id.btnUpdateEvent);
        MaterialButton btnDelete = findViewById(R.id.btnDeleteEvent);
        MaterialButton btnPickImage = findViewById(R.id.btnPickEditEventImage);
        String[] categories = {"Music", "Sports", "Tech", "Art", "Food", "Education", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
        btnPickImage.setOnClickListener(v -> imagePickerLauncher.launch(new String[]{"image/*"}));
        loadEventData();

        btnUpdate.setOnClickListener(v -> updateEvent());
        btnDelete.setOnClickListener(v -> deleteEvent());
        TopNavHelper.setupOrgNav(this, R.id.btnNavOrgEvents);
    }

    private void loadEventData() {
        Event event = dbHelper.getEventById(eventId);
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etTitle.setText(event.getTitle());
        etDesc.setText(event.getDescription());
        etLocation.setText(event.getLocation());
        etDate.setText(event.getDate());
        etTime.setText(event.getTime());
        etPrice.setText(String.valueOf(event.getPrice()));
        etSeats.setText(String.valueOf(event.getTotalSeats()));
        selectedImageUri = event.getImage() == null ? "" : event.getImage();

        for (int i = 0; i < spCategory.getCount(); i++) {
            if (spCategory.getItemAtPosition(i).toString().equalsIgnoreCase(event.getCategory())) {
                spCategory.setSelection(i);
                break;
            }
        }

        if (!selectedImageUri.isEmpty()) {
            updateSelectedImage(Uri.parse(selectedImageUri));
        } else {
            ivEventPreview.setImageResource(EventUtils.getEventImageRes(event.getCategory()));
            tvEventImageLabel.setText("Default category image");
        }
    }

    private void updateEvent() {
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
            if (!dbHelper.updateEvent(eventId, title, desc, category, location, date, time, price, seats, selectedImageUri)) {
                Toast.makeText(this, "Unable to update event. Total seats may be less than confirmed bookings.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException exception) {
            Toast.makeText(this, "Enter valid numeric values", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteEvent() {
        if (!dbHelper.deleteEventIfNoConfirmedBookings(eventId)) {
            Toast.makeText(this, "Cannot delete event with confirmed bookings", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
        finish();
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
