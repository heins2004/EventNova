package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class OrgDashboardActivity extends AppCompatActivity {

    private final List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_dashboard);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvOrgWelcome);
        TextView tvSub = findViewById(R.id.tvOrgSubheading);
        MaterialButton btnCreate = findViewById(R.id.btnCreateEvent);
        RecyclerView rvEvents = findViewById(R.id.rvOrgEvents);
        tvWelcome.setText("Hello, " + session.getUserName());
        tvSub.setText("Manage your live and completed events from one place.");
        btnCreate.setOnClickListener(v -> startActivity(new Intent(this, CreateEventActivity.class)));

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(this, EditEventActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });
        rvEvents.setAdapter(adapter);

        TopNavHelper.setupOrgNav(this, R.id.btnNavOrgEvents);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventList.clear();
        eventList.addAll(dbHelper.getEventsByOrganization(session.getUserId()));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
