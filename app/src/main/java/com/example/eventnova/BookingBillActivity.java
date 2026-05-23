package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class BookingBillActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_bill);

        dbHelper = new DatabaseHelper(this);

        int bookingId = getIntent().getIntExtra("BOOKING_ID", -1);
        Booking booking = dbHelper.getBookingById(bookingId);
        if (booking == null) {
            Toast.makeText(this, "Booking bill not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvBillId = findViewById(R.id.tvBillId);
        TextView tvEventTitle = findViewById(R.id.tvBillEventTitle);
        TextView tvOrganizer = findViewById(R.id.tvBillOrganizer);
        TextView tvEventDate = findViewById(R.id.tvBillEventDate);
        TextView tvVenue = findViewById(R.id.tvBillVenue);
        TextView tvBookingDate = findViewById(R.id.tvBillBookingDate);
        TextView tvTicketCount = findViewById(R.id.tvBillTickets);
        TextView tvTicketPrice = findViewById(R.id.tvBillTicketPrice);
        TextView tvTotal = findViewById(R.id.tvBillTotal);
        TextView tvStatus = findViewById(R.id.tvBillStatus);
        MaterialButton btnOpenBookings = findViewById(R.id.btnOpenBookings);
        MaterialButton btnDone = findViewById(R.id.btnBillDone);

        Event event = dbHelper.getEventById(booking.getEventId());
        double unitPrice = event == null || booking.getNumTickets() == 0
                ? booking.getTotalPrice()
                : booking.getTotalPrice() / booking.getNumTickets();

        tvBillId.setText("Bill #" + booking.getId());
        tvEventTitle.setText(booking.getEventTitle());
        tvOrganizer.setText("Organizer: " + booking.getOrganizerName());
        tvEventDate.setText(EventUtils.formatEventDate(booking.getEventDate()) + " | " + EventUtils.formatEventTime(booking.getEventTime()));
        tvVenue.setText(booking.getLocation() + " | " + booking.getCategory());
        tvBookingDate.setText("Booked on " + EventUtils.formatEventDate(booking.getBookingDate()));
        tvTicketCount.setText(String.valueOf(booking.getNumTickets()));
        tvTicketPrice.setText(EventUtils.formatCurrency(unitPrice));
        tvTotal.setText(EventUtils.formatCurrency(booking.getTotalPrice()));
        tvStatus.setText(booking.getStatus().toUpperCase());

        btnOpenBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyBookingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        btnDone.setOnClickListener(v -> finish());
    }
}
