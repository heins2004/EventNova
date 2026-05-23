package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private final List<Booking> allBookings = new ArrayList<>();
    private final List<Booking> visibleBookings = new ArrayList<>();
    private BookingAdapter adapter;
    private TextView tvNoBookings;
    private DatabaseHelper dbHelper;
    private SessionManager session;
    private boolean showingPastEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        RecyclerView rvBookings = findViewById(R.id.rvMyBookings);
        tvNoBookings = findViewById(R.id.tvNoBookings);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggleBookingFilter);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(visibleBookings, true, new BookingAdapter.OnBookingActionListener() {
            @Override
            public void onCancelClick(Booking booking) {
                cancelBooking(booking);
            }

            @Override
            public void onViewBillClick(Booking booking) {
                Intent intent = new Intent(MyBookingsActivity.this, BookingBillActivity.class);
                intent.putExtra("BOOKING_ID", booking.getId());
                startActivity(intent);
            }
        });
        rvBookings.setAdapter(adapter);

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            showingPastEvents = checkedId == R.id.btnPastEvents;
            applyFilter();
        });
        toggleGroup.check(R.id.btnUpcomingEvents);

        TopNavHelper.setupUserNav(this, R.id.btnNavUserBookings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allBookings.clear();
        allBookings.addAll(dbHelper.getUserBookings(session.getUserId()));
        applyFilter();
    }

    private void applyFilter() {
        visibleBookings.clear();
        for (Booking booking : allBookings) {
            boolean isPast = EventUtils.isPastEvent(booking.getEventDate(), booking.getEventTime());
            if (showingPastEvents == isPast) {
                visibleBookings.add(booking);
            }
        }
        tvNoBookings.setText(showingPastEvents ? "No past events yet" : "No upcoming bookings found");
        tvNoBookings.setVisibility(visibleBookings.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
    }

    private void cancelBooking(Booking booking) {
        if (!dbHelper.cancelBooking(booking.getId(), session.getUserId())) {
            Toast.makeText(this, "Unable to cancel booking", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Booking cancelled", Toast.LENGTH_SHORT).show();
        onResume();
    }
}
