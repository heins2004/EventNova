package com.example.eventnova;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserDashboardActivity extends AppCompatActivity {

    private final List<Event> eventList = new ArrayList<>();
    private EventAdapter adapter;
    private DatabaseHelper dbHelper;
    private String activeQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        dbHelper = new DatabaseHelper(this);
        SessionManager session = new SessionManager(this);

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        EditText etSearch = findViewById(R.id.etSearch);
        RecyclerView rvEvents = findViewById(R.id.rvEvents);
        tvWelcome.setText("Welcome, " + session.getUserName());

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(eventList, event -> {
            Intent intent = new Intent(this, EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });
        rvEvents.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activeQuery = String.valueOf(s);
                loadEvents(activeQuery);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        TopNavHelper.setupUserNav(this, R.id.btnNavUserHome);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents(activeQuery);
    }

    private void loadEvents(String query) {
        eventList.clear();
        eventList.addAll(dbHelper.getActiveEvents(query));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
