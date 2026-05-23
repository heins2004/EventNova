package com.example.eventnova;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class OrgBookingsActivity extends AppCompatActivity {

    private final List<Booking> bookingList = new ArrayList<>();
    private AdminBookingAdapter adapter;
    private TextView tvNoBookings;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_bookings);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        RecyclerView rvBookings = findViewById(R.id.rvOrgBookings);
        tvNoBookings = findViewById(R.id.tvNoOrgBookings);
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBookingAdapter(bookingList);
        rvBookings.setAdapter(adapter);
        TopNavHelper.setupOrgNav(this, R.id.btnNavOrgBookings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookingList.clear();
        bookingList.addAll(dbHelper.getOrganizationBookings(session.getUserId()));
        tvNoBookings.setVisibility(bookingList.isEmpty() ? View.VISIBLE : View.GONE);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
