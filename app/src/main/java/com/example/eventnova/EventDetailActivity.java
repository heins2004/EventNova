package com.example.eventnova;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvOrg;
    private TextView tvCategory;
    private TextView tvDescription;
    private TextView tvDateTime;
    private TextView tvLocation;
    private TextView tvPrice;
    private TextView tvSeats;
    private TextView tvPhase;
    private TextView tvVenue;
    private TextView tvTicketCount;
    private TextView tvBookingSummary;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private int eventId;
    private int selectedTickets = 1;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        ImageView ivHero = findViewById(R.id.ivDetailImage);
        tvTitle = findViewById(R.id.tvDetailTitle);
        tvOrg = findViewById(R.id.tvDetailOrg);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvDescription = findViewById(R.id.tvDetailDescription);
        tvDateTime = findViewById(R.id.tvDetailDateTime);
        tvLocation = findViewById(R.id.tvDetailLocation);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvSeats = findViewById(R.id.tvDetailSeats);
        tvPhase = findViewById(R.id.tvDetailPhase);
        tvVenue = findViewById(R.id.tvDetailVenue);
        tvTicketCount = findViewById(R.id.tvTicketCount);
        tvBookingSummary = findViewById(R.id.tvBookingSummary);
        MaterialButton btnDecrease = findViewById(R.id.btnDecreaseTickets);
        MaterialButton btnIncrease = findViewById(R.id.btnIncreaseTickets);
        MaterialButton btnBookNow = findViewById(R.id.btnBookNow);
        MaterialButton btnAddToCalendar = findViewById(R.id.btnAddToCalendar);
        MaterialButton btnViewMyBookings = findViewById(R.id.btnViewMyBookings);

        loadEventDetails();
        if (event != null) {
            EventUtils.loadEventImage(ivHero, event);
        }

        btnDecrease.setOnClickListener(v -> changeTicketCount(-1));
        btnIncrease.setOnClickListener(v -> changeTicketCount(1));
        btnBookNow.setOnClickListener(v -> bookEvent());
        btnAddToCalendar.setOnClickListener(v -> addToCalendar());
        btnViewMyBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyBookingsActivity.class);
            startActivity(intent);
        });
        TopNavHelper.setupUserNav(this, R.id.btnNavUserHome);
    }

    private void loadEventDetails() {
        event = dbHelper.getEventById(eventId);
        if (event == null) {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (selectedTickets > Math.max(1, event.getAvailableSeats())) {
            selectedTickets = Math.max(1, event.getAvailableSeats());
        }

        String phase = EventUtils.getEventPhase(event.getDate(), event.getTime());
        tvTitle.setText(event.getTitle());
        tvOrg.setText("Organizer: " + event.getOrgName());
        tvCategory.setText("Category: " + event.getCategory());
        tvDescription.setText(event.getDescription());
        tvDateTime.setText("When: " + EventUtils.formatEventDate(event.getDate()) + " | " + EventUtils.formatEventTime(event.getTime()));
        tvLocation.setText("City: " + event.getLocation());
        tvVenue.setText("Venue: " + event.getLocation());
        tvPrice.setText("Price: " + EventUtils.formatCurrency(event.getPrice()));
        tvSeats.setText("Seats Left: " + event.getAvailableSeats() + " / " + event.getTotalSeats());
        tvPhase.setText("Booking Status: " + phase);
        updateTicketSummary();
    }

    private void changeTicketCount(int change) {
        if (event == null) {
            return;
        }
        int maxTickets = Math.max(1, event.getAvailableSeats());
        selectedTickets = Math.max(1, Math.min(maxTickets, selectedTickets + change));
        updateTicketSummary();
    }

    private void updateTicketSummary() {
        tvTicketCount.setText(String.valueOf(selectedTickets));
        if (event == null) {
            tvBookingSummary.setText("");
            return;
        }
        double total = selectedTickets * event.getPrice();
        tvBookingSummary.setText(selectedTickets + " ticket(s) | Total " + EventUtils.formatCurrency(total));
    }

    private void bookEvent() {
        if (event == null || EventUtils.isPastEvent(event.getDate(), event.getTime())) {
            Toast.makeText(this, "Bookings are closed for this event", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedTickets > event.getAvailableSeats()) {
            Toast.makeText(this, "Not enough seats left", Toast.LENGTH_SHORT).show();
            return;
        }

        long bookingId = dbHelper.createBooking(session.getUserId(), eventId, selectedTickets);
        if (bookingId == -1) {
            Toast.makeText(this, "Booking failed. Check seat availability.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, BookingBillActivity.class);
        intent.putExtra("BOOKING_ID", (int) bookingId);
        startActivity(intent);
        Toast.makeText(this, "Booking successful", Toast.LENGTH_LONG).show();
        loadEventDetails();
    }

    private void addToCalendar() {
        if (event == null) {
            Toast.makeText(this, "No event data available", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Try Google Calendar specifically first
            Intent preferredIntent = EventUtils.createCalendarIntent(event);
            preferredIntent.setPackage("com.google.android.calendar");
            startActivity(preferredIntent); // throws if not found
            Toast.makeText(this, "Opening Google Calendar...", Toast.LENGTH_SHORT).show();

        } catch (android.content.ActivityNotFoundException e1) {
            try {
                // Fallback: any calendar app
                Intent genericIntent = EventUtils.createCalendarIntent(event);
                startActivity(genericIntent);
                Toast.makeText(this, "Opening calendar...", Toast.LENGTH_SHORT).show();

            } catch (android.content.ActivityNotFoundException e2) {
                Toast.makeText(this, "No calendar app found. Please install Google Calendar.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
