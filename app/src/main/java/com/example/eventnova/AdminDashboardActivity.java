package com.example.eventnova;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager session;
    private TextView tvUsers;
    private TextView tvOrgs;
    private TextView tvEvents;
    private TextView tvBookings;
    private TextView tvTopSellingEvent;
    private TextView tvTopSellingMeta;
    private LinearLayout chartContainer;
    private View usersSection;
    private View orgsSection;
    private View eventsSection;
    private View bookingsSection;
    private MaterialCardView usersCard;
    private MaterialCardView orgsCard;
    private MaterialCardView eventsCard;
    private MaterialCardView bookingsCard;
    private MaterialButton btnShowLiveEvents;
    private MaterialButton btnShowPastEvents;
    private final List<User> userList = new ArrayList<>();
    private final List<Organization> orgList = new ArrayList<>();
    private final List<Event> liveEventList = new ArrayList<>();
    private final List<Event> pastEventList = new ArrayList<>();
    private final List<Booking> bookingList = new ArrayList<>();
    private final List<Event> visibleEventList = new ArrayList<>();
    private UserAdapter userAdapter;
    private OrgAdapter orgAdapter;
    private EventAdapter eventAdapter;
    private AdminBookingAdapter bookingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        dbHelper = new DatabaseHelper(this);
        session = new SessionManager(this);

        tvUsers = findViewById(R.id.tvTotalUsers);
        tvOrgs = findViewById(R.id.tvTotalOrgs);
        tvEvents = findViewById(R.id.tvTotalEvents);
        tvBookings = findViewById(R.id.tvTotalBookings);
        tvTopSellingEvent = findViewById(R.id.tvTopSellingEvent);
        tvTopSellingMeta = findViewById(R.id.tvTopSellingMeta);
        chartContainer = findViewById(R.id.layoutBookingChart);
        usersSection = findViewById(R.id.layoutUsersSection);
        orgsSection = findViewById(R.id.layoutOrgsSection);
        eventsSection = findViewById(R.id.layoutEventsSection);
        bookingsSection = findViewById(R.id.cardBookingInsightsSection);
        usersCard = findViewById(R.id.cardOverviewUsers);
        orgsCard = findViewById(R.id.cardOverviewOrgs);
        eventsCard = findViewById(R.id.cardOverviewEvents);
        bookingsCard = findViewById(R.id.cardOverviewBookings);
        RecyclerView rvUsers = findViewById(R.id.rvOverviewUsers);
        RecyclerView rvOrgs = findViewById(R.id.rvOverviewOrgs);
        RecyclerView rvEvents = findViewById(R.id.rvOverviewEvents);
        RecyclerView rvBookings = findViewById(R.id.rvOverviewBookings);
        TextView tvNoEvents = findViewById(R.id.tvNoEvents);
        TextView tvNoBookings = findViewById(R.id.tvNoAdminBookings);
        btnShowLiveEvents = findViewById(R.id.btnShowLiveEvents);
        btnShowPastEvents = findViewById(R.id.btnShowPastEvents);
        MaterialButton btnLogout = findViewById(R.id.btnAdminLogout);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvOrgs.setLayoutManager(new LinearLayoutManager(this));
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setNestedScrollingEnabled(false);
        rvOrgs.setNestedScrollingEnabled(false);
        rvEvents.setNestedScrollingEnabled(true);
        rvBookings.setNestedScrollingEnabled(false);
        userAdapter = new UserAdapter(userList);
        orgAdapter = new OrgAdapter(orgList);
        eventAdapter = new EventAdapter(visibleEventList, event -> {});
        bookingAdapter = new AdminBookingAdapter(bookingList);
        rvUsers.setAdapter(userAdapter);
        rvOrgs.setAdapter(orgAdapter);
        rvEvents.setAdapter(eventAdapter);
        rvBookings.setAdapter(bookingAdapter);
        tvNoEvents.setVisibility(View.GONE);
        tvNoBookings.setVisibility(View.GONE);
        usersCard.setOnClickListener(v -> showSection(Section.USERS));
        orgsCard.setOnClickListener(v -> showSection(Section.ORGANIZATIONS));
        eventsCard.setOnClickListener(v -> showSection(Section.EVENTS));
        bookingsCard.setOnClickListener(v -> showSection(Section.BOOKINGS));
        btnShowLiveEvents.setOnClickListener(v -> showEventGroup(false));
        btnShowPastEvents.setOnClickListener(v -> showEventGroup(true));

        btnLogout.setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, RoleSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        tvUsers.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_USERS)));
        tvOrgs.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_ORGANIZATIONS)));
        tvEvents.setText(String.valueOf(dbHelper.getActiveEventCount()));
        tvBookings.setText(String.valueOf(dbHelper.getCount(DatabaseHelper.TABLE_BOOKINGS)));
        loadOverviewLists();
        loadSalesAnalytics();
        showSection(Section.EVENTS);
        showEventGroup(false);
    }

    private void showSection(Section section) {
        usersSection.setVisibility(section == Section.USERS ? View.VISIBLE : View.GONE);
        orgsSection.setVisibility(section == Section.ORGANIZATIONS ? View.VISIBLE : View.GONE);
        eventsSection.setVisibility(section == Section.EVENTS ? View.VISIBLE : View.GONE);
        bookingsSection.setVisibility(section == Section.BOOKINGS ? View.VISIBLE : View.GONE);

        styleOverviewCard(usersCard, section == Section.USERS);
        styleOverviewCard(orgsCard, section == Section.ORGANIZATIONS);
        styleOverviewCard(eventsCard, section == Section.EVENTS);
        styleOverviewCard(bookingsCard, section == Section.BOOKINGS);
    }

    private void loadOverviewLists() {
        userList.clear();
        orgList.clear();
        liveEventList.clear();
        pastEventList.clear();
        bookingList.clear();
        visibleEventList.clear();

        userList.addAll(dbHelper.getAllUsers());
        orgList.addAll(dbHelper.getAllOrganizations());
        for (Event event : dbHelper.getAllEvents()) {
            if ("completed".equalsIgnoreCase(event.getStatus())) {
                pastEventList.add(event);
            } else {
                liveEventList.add(event);
            }
        }
        bookingList.addAll(dbHelper.getAllBookings());

        userAdapter.notifyDataSetChanged();
        orgAdapter.notifyDataSetChanged();
        bookingAdapter.notifyDataSetChanged();
        showEventGroup(btnShowPastEvents != null && btnShowPastEvents.isChecked());
        TextView tvNoBookings = findViewById(R.id.tvNoAdminBookings);
        tvNoBookings.setVisibility(bookingList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showEventGroup(boolean showPastEvents) {
        if (btnShowLiveEvents == null || btnShowPastEvents == null) {
            return;
        }

        visibleEventList.clear();
        visibleEventList.addAll(showPastEvents ? pastEventList : liveEventList);
        eventAdapter.notifyDataSetChanged();

        styleToggleButton(btnShowLiveEvents, !showPastEvents);
        styleToggleButton(btnShowPastEvents, showPastEvents);

        TextView tvEventsTitle = findViewById(R.id.tvEventsSectionTitle);
        TextView tvNoEvents = findViewById(R.id.tvNoEvents);
        tvEventsTitle.setText(showPastEvents ? "Past Events" : "Live Events");
        tvNoEvents.setText(showPastEvents ? "No past events yet." : "No live events available.");
        tvNoEvents.setVisibility(visibleEventList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadSalesAnalytics() {
        Event topSellingEvent = dbHelper.getTopSellingEvent();
        int topSoldTickets = topSellingEvent == null ? 0 : dbHelper.getTicketsSoldForEvent(topSellingEvent.getId());
        if (topSellingEvent == null || topSoldTickets <= 0) {
            tvTopSellingEvent.setText("No bookings yet");
            tvTopSellingMeta.setText("Ticket sales will appear here after the first booking.");
        } else {
            tvTopSellingEvent.setText(topSellingEvent.getTitle());
            tvTopSellingMeta.setText(topSoldTickets + " tickets sold");
        }

        List<EventSalesStat> salesStats = dbHelper.getTopEventSalesStats(5);
        chartContainer.removeAllViews();
        if (salesStats.isEmpty() || salesStats.get(0).getTicketsSold() <= 0) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No booking data yet.");
            emptyView.setTextColor(ContextCompat.getColor(this, R.color.colorTextMuted));
            chartContainer.addView(emptyView);
            return;
        }

        int maxTickets = 1;
        for (EventSalesStat stat : salesStats) {
            maxTickets = Math.max(maxTickets, stat.getTicketsSold());
        }

        for (EventSalesStat stat : salesStats) {
            chartContainer.addView(createChartRow(stat, maxTickets));
        }
    }

    private View createChartRow(EventSalesStat stat, int maxTickets) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(0, dp(8), 0, dp(8));

        TextView label = new TextView(this);
        label.setText(stat.getTitle() + "  •  " + stat.getTicketsSold() + " tickets");
        label.setTextColor(ContextCompat.getColor(this, R.color.colorTextPrimary));
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        View bar = new View(this);
        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
                Math.max(dp(28), (int) (((float) stat.getTicketsSold() / maxTickets) * dp(260))),
                dp(12)
        );
        barParams.topMargin = dp(6);
        bar.setLayoutParams(barParams);
        bar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));

        TextView meta = new TextView(this);
        meta.setText(stat.getBookingCount() + " booking(s)");
        meta.setTextColor(ContextCompat.getColor(this, R.color.colorTextMuted));
        meta.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        meta.setPadding(0, dp(4), 0, 0);

        row.addView(label);
        row.addView(bar);
        row.addView(meta);
        return row;
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    private void styleOverviewCard(MaterialCardView card, boolean selected) {
        int primary = ContextCompat.getColor(this, R.color.colorPrimary);
        int stroke = ContextCompat.getColor(this, R.color.colorStroke);
        int defaultBackground = ContextCompat.getColor(this, R.color.colorCard);
        int selectedBackground = ContextCompat.getColor(this, R.color.colorPrimarySoft);
        card.setStrokeWidth(selected ? dp(2) : dp(1));
        card.setStrokeColor(selected ? primary : stroke);
        card.setCardElevation(selected ? dp(4) : dp(2));
        card.setCardBackgroundColor(selected ? selectedBackground : defaultBackground);
        card.setRippleColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent)));
    }

    private void styleToggleButton(MaterialButton button, boolean selected) {
        int primary = ContextCompat.getColor(this, R.color.colorPrimary);
        int white = ContextCompat.getColor(this, R.color.white);
        int text = ContextCompat.getColor(this, R.color.colorTextPrimary);
        int stroke = ContextCompat.getColor(this, R.color.colorStroke);
        button.setChecked(selected);
        button.setBackgroundTintList(ColorStateList.valueOf(selected ? primary : white));
        button.setStrokeColor(ColorStateList.valueOf(selected ? primary : stroke));
        button.setTextColor(selected ? white : text);
    }

    private enum Section {
        USERS,
        ORGANIZATIONS,
        EVENTS,
        BOOKINGS
    }
}
