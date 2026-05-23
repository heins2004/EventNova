package com.example.eventnova;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventNova.db";
    private static final int DATABASE_VERSION = 3;

    // Tables
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ORGANIZATIONS = "organizations";
    public static final String TABLE_EVENTS = "events";
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String TABLE_ADMINS = "admins";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, full_name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL, phone TEXT NOT NULL, profile_image TEXT, role TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_ORGANIZATIONS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, org_name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL, phone TEXT NOT NULL, description TEXT, logo TEXT, category TEXT NOT NULL, location TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, org_id INTEGER NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, category TEXT NOT NULL, location TEXT NOT NULL, date TEXT NOT NULL, time TEXT NOT NULL, price REAL NOT NULL, available_seats INTEGER NOT NULL, total_seats INTEGER NOT NULL, image TEXT, status TEXT NOT NULL, FOREIGN KEY(org_id) REFERENCES organizations(id))");
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER NOT NULL, event_id INTEGER NOT NULL, booking_date TEXT NOT NULL, num_tickets INTEGER NOT NULL, total_price REAL NOT NULL, status TEXT NOT NULL, FOREIGN KEY(user_id) REFERENCES users(id), FOREIGN KEY(event_id) REFERENCES events(id))");
        db.execSQL("CREATE TABLE " + TABLE_ADMINS + " (id INTEGER PRIMARY KEY AUTOINCREMENT, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");

        ContentValues adminValues = new ContentValues();
        adminValues.put("email", "admin@gmail.com");
        adminValues.put("password", "123456");
        db.insert(TABLE_ADMINS, null, adminValues);

        seedData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORGANIZATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMINS);
        onCreate(db);
    }

    private void seedData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO organizations (org_name, email, password, phone, description, logo, category, location) VALUES ('TechSphere', 'techsphere@eventnova.com', 'tech123', '9876543210', 'Hands-on conferences and workshops for modern builders.', '', 'Tech', 'Bengaluru')");
        db.execSQL("INSERT INTO organizations (org_name, email, password, phone, description, logo, category, location) VALUES ('Rhythm House', 'rhythm@eventnova.com', 'music123', '9123456780', 'Live music nights and curated artist experiences.', '', 'Music', 'Mumbai')");
        db.execSQL("INSERT INTO organizations (org_name, email, password, phone, description, logo, category, location) VALUES ('PlayPeak', 'sports@eventnova.com', 'sports123', '9988776655', 'Community-first sports events and leagues.', '', 'Sports', 'Delhi')");

        db.execSQL("INSERT INTO events (org_id, title, description, category, location, date, time, price, available_seats, total_seats, image, status) VALUES (1, 'Android Builders Summit', 'A one-day Android engineering summit with practical sessions on app architecture, testing, and performance.', 'Tech', 'Bengaluru', '2026-06-22', '10:00', 799.0, 120, 120, '', 'active')");
        db.execSQL("INSERT INTO events (org_id, title, description, category, location, date, time, price, available_seats, total_seats, image, status) VALUES (2, 'Indie Sunset Sessions', 'An evening of unplugged performances, food stalls, and small-stage collaborations.', 'Music', 'Mumbai', '2026-07-05', '18:30', 499.0, 80, 80, '', 'active')");
        db.execSQL("INSERT INTO events (org_id, title, description, category, location, date, time, price, available_seats, total_seats, image, status) VALUES (3, 'City Futsal Cup', '5-a-side amateur futsal tournament with group rounds and knockout fixtures.', 'Sports', 'Delhi', '2026-08-14', '09:00', 299.0, 64, 64, '', 'active')");
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, role FROM " + TABLE_USERS + " WHERE email=? AND password=? AND role='user'", new String[]{email, password});
        try {
            if (cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("role"))
                );
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public Organization authenticateOrganization(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, org_name, email, phone, category, location FROM " + TABLE_ORGANIZATIONS + " WHERE email=? AND password=?", new String[]{email, password});
        try {
            if (cursor.moveToFirst()) {
                return new Organization(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location"))
                );
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public boolean authenticateAdmin(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + TABLE_ADMINS + " WHERE email=? AND password=?", new String[]{email, password});
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public boolean isUserEmailTaken(String email) {
        return isValuePresent(TABLE_USERS, "email", email);
    }

    public boolean isOrganizationEmailTaken(String email) {
        return isValuePresent(TABLE_ORGANIZATIONS, "email", email);
    }

    public boolean isAdminEmailTaken(String email) {
        return isValuePresent(TABLE_ADMINS, "email", email);
    }

    public boolean isAnyEmailTaken(String email) {
        return isUserEmailTaken(email) || isOrganizationEmailTaken(email) || isAdminEmailTaken(email);
    }

    private boolean isValuePresent(String table, String column, String value) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM " + table + " WHERE " + column + "=?", new String[]{value});
        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public long registerUser(String fullName, String email, String password, String phone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", fullName);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("profile_image", "");
        values.put("role", "user");
        return db.insert(TABLE_USERS, null, values);
    }

    public long registerOrganization(String name, String email, String password, String phone, String category, String location, String description) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("org_name", name);
        values.put("email", email);
        values.put("password", password);
        values.put("phone", phone);
        values.put("description", description);
        values.put("logo", "");
        values.put("category", category);
        values.put("location", location);
        return db.insert(TABLE_ORGANIZATIONS, null, values);
    }

    public List<Event> getActiveEvents(String query) {
        syncExpiredEvents();
        String sql = "SELECT e.*, o.org_name FROM " + TABLE_EVENTS + " e JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id WHERE e.status='active'";
        List<String> args = new ArrayList<>();
        if (query != null && !query.trim().isEmpty()) {
            sql += " AND (e.title LIKE ? OR e.category LIKE ? OR o.org_name LIKE ? OR e.location LIKE ?)";
            String likeQuery = "%" + query.trim() + "%";
            args.add(likeQuery);
            args.add(likeQuery);
            args.add(likeQuery);
            args.add(likeQuery);
        }
        sql += " ORDER BY e.date, e.time";
        return readEvents(sql, args.toArray(new String[0]));
    }

    public List<Event> getEventsByOrganization(int orgId) {
        syncExpiredEvents();
        return readEvents("SELECT e.*, o.org_name FROM " + TABLE_EVENTS + " e JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id WHERE e.org_id=? AND e.status='active' ORDER BY e.date, e.time", new String[]{String.valueOf(orgId)});
    }

    public List<Event> getAllEvents() {
        syncExpiredEvents();
        return readEvents("SELECT e.*, o.org_name FROM " + TABLE_EVENTS + " e JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id ORDER BY e.id DESC", null);
    }

    public Event getEventById(int eventId) {
        syncExpiredEvents();
        List<Event> events = readEvents("SELECT e.*, o.org_name FROM " + TABLE_EVENTS + " e JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id WHERE e.id=?", new String[]{String.valueOf(eventId)});
        return events.isEmpty() ? null : events.get(0);
    }

    private List<Event> readEvents(String sql, String[] selectionArgs) {
        List<Event> events = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, selectionArgs);
        try {
            while (cursor.moveToNext()) {
                events.add(new Event(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("org_id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("available_seats")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("total_seats")),
                        cursor.getString(cursor.getColumnIndexOrThrow("image")),
                        cursor.getString(cursor.getColumnIndexOrThrow("status"))
                ));
            }
        } finally {
            cursor.close();
        }
        return events;
    }

    public long createEvent(int orgId, String title, String description, String category, String location, String date, String time, double price, int totalSeats, String image) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("org_id", orgId);
        values.put("title", title);
        values.put("description", description);
        values.put("category", category);
        values.put("location", location);
        values.put("date", date);
        values.put("time", time);
        values.put("price", price);
        values.put("available_seats", totalSeats);
        values.put("total_seats", totalSeats);
        values.put("image", image == null ? "" : image);
        values.put("status", "active");
        return db.insert(TABLE_EVENTS, null, values);
    }

    public boolean updateEvent(int eventId, String title, String description, String category, String location, String date, String time, double price, int totalSeats, String image) {
        SQLiteDatabase db = getWritableDatabase();
        Event currentEvent = getEventById(eventId);
        if (currentEvent == null) {
            return false;
        }

        int confirmedTickets = getConfirmedTicketsForEvent(eventId);
        if (totalSeats < confirmedTickets) {
            return false;
        }

        int newAvailableSeats = totalSeats - confirmedTickets;
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("description", description);
        values.put("category", category);
        values.put("location", location);
        values.put("date", date);
        values.put("time", time);
        values.put("price", price);
        values.put("total_seats", totalSeats);
        values.put("available_seats", newAvailableSeats);
        values.put("image", image == null ? "" : image);
        return db.update(TABLE_EVENTS, values, "id=?", new String[]{String.valueOf(eventId)}) > 0;
    }

    public boolean deleteEventIfNoConfirmedBookings(int eventId) {
        if (getConfirmedTicketsForEvent(eventId) > 0) {
            return false;
        }
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_EVENTS, "id=?", new String[]{String.valueOf(eventId)}) > 0;
    }

    private int getConfirmedTicketsForEvent(int eventId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COALESCE(SUM(num_tickets), 0) AS booked FROM " + TABLE_BOOKINGS + " WHERE event_id=? AND status='confirmed'", new String[]{String.valueOf(eventId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndexOrThrow("booked")) : 0;
        } finally {
            cursor.close();
        }
    }

    public long createBooking(int userId, int eventId, int numTickets) {
        SQLiteDatabase db = getWritableDatabase();
        Event event = getEventById(eventId);
        if (event == null || !"active".equalsIgnoreCase(event.getStatus()) || EventUtils.isPastEvent(event.getDate(), event.getTime()) || numTickets <= 0 || numTickets > event.getAvailableSeats()) {
            return -1;
        }

        db.beginTransaction();
        try {
            ContentValues bookingValues = new ContentValues();
            bookingValues.put("user_id", userId);
            bookingValues.put("event_id", eventId);
            bookingValues.put("booking_date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
            bookingValues.put("num_tickets", numTickets);
            bookingValues.put("total_price", numTickets * event.getPrice());
            bookingValues.put("status", "confirmed");

            long bookingId = db.insert(TABLE_BOOKINGS, null, bookingValues);
            if (bookingId == -1) {
                return -1;
            }

            ContentValues eventValues = new ContentValues();
            eventValues.put("available_seats", event.getAvailableSeats() - numTickets);
            db.update(TABLE_EVENTS, eventValues, "id=?", new String[]{String.valueOf(eventId)});

            db.setTransactionSuccessful();
            return bookingId;
        } finally {
            db.endTransaction();
        }
    }

    public boolean cancelBooking(int bookingId, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT event_id, num_tickets, status FROM " + TABLE_BOOKINGS + " WHERE id=? AND user_id=?", new String[]{String.valueOf(bookingId), String.valueOf(userId)});
        try {
            if (!cursor.moveToFirst() || "cancelled".equalsIgnoreCase(cursor.getString(cursor.getColumnIndexOrThrow("status")))) {
                return false;
            }

            int eventId = cursor.getInt(cursor.getColumnIndexOrThrow("event_id"));
            int numTickets = cursor.getInt(cursor.getColumnIndexOrThrow("num_tickets"));
            db.beginTransaction();
            try {
                ContentValues bookingValues = new ContentValues();
                bookingValues.put("status", "cancelled");
                db.update(TABLE_BOOKINGS, bookingValues, "id=?", new String[]{String.valueOf(bookingId)});

                db.execSQL("UPDATE " + TABLE_EVENTS + " SET available_seats = available_seats + ? WHERE id=?", new Object[]{numTickets, eventId});
                db.setTransactionSuccessful();
                return true;
            } finally {
                db.endTransaction();
            }
        } finally {
            cursor.close();
        }
    }

    public List<Booking> getUserBookings(int userId) {
        syncExpiredEvents();
        String sql = "SELECT b.*, e.title, e.date, e.time, e.location, e.category, o.org_name FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_EVENTS + " e ON b.event_id=e.id " +
                "JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id " +
                "WHERE b.user_id=? ORDER BY e.date DESC, e.time DESC";
        return readBookings(sql, new String[]{String.valueOf(userId)}, false);
    }

    public List<Booking> getOrganizationBookings(int orgId) {
        syncExpiredEvents();
        String sql = "SELECT b.*, u.full_name, e.title, e.date, e.time, e.location, e.category, o.org_name FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON b.user_id=u.id " +
                "JOIN " + TABLE_EVENTS + " e ON b.event_id=e.id " +
                "JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id " +
                "WHERE e.org_id=? ORDER BY e.date DESC, e.time DESC";
        return readBookings(sql, new String[]{String.valueOf(orgId)}, true);
    }

    public List<Booking> getAllBookings() {
        syncExpiredEvents();
        String sql = "SELECT b.*, u.full_name, e.title, e.date, e.time, e.location, e.category, o.org_name FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_USERS + " u ON b.user_id=u.id " +
                "JOIN " + TABLE_EVENTS + " e ON b.event_id=e.id " +
                "JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id ORDER BY e.date DESC, e.time DESC";
        return readBookings(sql, null, true);
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT b.*, e.title, e.date, e.time, e.location, e.category, o.org_name FROM " + TABLE_BOOKINGS + " b " +
                "JOIN " + TABLE_EVENTS + " e ON b.event_id=e.id " +
                "JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id " +
                "WHERE b.id=?";
        List<Booking> bookings = readBookings(sql, new String[]{String.valueOf(bookingId)}, false);
        return bookings.isEmpty() ? null : bookings.get(0);
    }

    private List<Booking> readBookings(String sql, String[] args, boolean includeUserName) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, args);
        try {
            while (cursor.moveToNext()) {
                if (includeUserName) {
                    bookings.add(new Booking(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("event_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("title")),
                            cursor.getString(cursor.getColumnIndexOrThrow("booking_date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("category")),
                            cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_tickets")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                } else {
                    bookings.add(new Booking(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("event_id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("title")),
                            cursor.getString(cursor.getColumnIndexOrThrow("booking_date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("date")),
                            cursor.getString(cursor.getColumnIndexOrThrow("time")),
                            cursor.getString(cursor.getColumnIndexOrThrow("location")),
                            cursor.getString(cursor.getColumnIndexOrThrow("category")),
                            cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("num_tickets")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")),
                            cursor.getString(cursor.getColumnIndexOrThrow("status"))
                    ));
                }
            }
        } finally {
            cursor.close();
        }
        return bookings;
    }

    public User getUserById(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, role FROM " + TABLE_USERS + " WHERE id=?", new String[]{String.valueOf(userId)});
        try {
            if (cursor.moveToFirst()) {
                return new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("role"))
                );
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public Organization getOrganizationById(int orgId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, org_name, email, phone, category, location FROM " + TABLE_ORGANIZATIONS + " WHERE id=?", new String[]{String.valueOf(orgId)});
        try {
            if (cursor.moveToFirst()) {
                return new Organization(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location"))
                );
            }
            return null;
        } finally {
            cursor.close();
        }
    }

    public String getOrganizationDescription(int orgId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT description FROM " + TABLE_ORGANIZATIONS + " WHERE id=?", new String[]{String.valueOf(orgId)});
        try {
            return cursor.moveToFirst() ? cursor.getString(cursor.getColumnIndexOrThrow("description")) : "";
        } finally {
            cursor.close();
        }
    }

    public boolean updateUserProfile(int userId, String name, String phone, String password) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("full_name", name);
        values.put("phone", phone);
        if (password != null && !password.trim().isEmpty()) {
            values.put("password", password.trim());
        }
        return db.update(TABLE_USERS, values, "id=?", new String[]{String.valueOf(userId)}) > 0;
    }

    public boolean updateOrganizationProfile(int orgId, String name, String phone, String description, String location) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("org_name", name);
        values.put("phone", phone);
        values.put("description", description);
        values.put("location", location);
        return db.update(TABLE_ORGANIZATIONS, values, "id=?", new String[]{String.valueOf(orgId)}) > 0;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, full_name, email, phone, role FROM " + TABLE_USERS + " WHERE role='user' ORDER BY id DESC", null);
        try {
            while (cursor.moveToNext()) {
                users.add(new User(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("full_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("role"))
                ));
            }
        } finally {
            cursor.close();
        }
        return users;
    }

    public List<Organization> getAllOrganizations() {
        List<Organization> orgs = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, org_name, email, phone, category, location FROM " + TABLE_ORGANIZATIONS + " ORDER BY id DESC", null);
        try {
            while (cursor.moveToNext()) {
                orgs.add(new Organization(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("org_name")),
                        cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                        cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location"))
                ));
            }
        } finally {
            cursor.close();
        }
        return orgs;
    }

    public int getCount(String table) {
        syncExpiredEvents();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) AS total FROM " + table, null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndexOrThrow("total")) : 0;
        } finally {
            cursor.close();
        }
    }

    public int getActiveEventCount() {
        syncExpiredEvents();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) AS total FROM " + TABLE_EVENTS + " WHERE status='active'", null);
        try {
            return cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndexOrThrow("total")) : 0;
        } finally {
            cursor.close();
        }
    }

    public Event getTopSellingEvent() {
        syncExpiredEvents();
        String sql = "SELECT e.*, o.org_name FROM " + TABLE_EVENTS + " e " +
                "JOIN " + TABLE_ORGANIZATIONS + " o ON e.org_id=o.id " +
                "LEFT JOIN " + TABLE_BOOKINGS + " b ON e.id=b.event_id AND b.status='confirmed' " +
                "GROUP BY e.id ORDER BY COALESCE(SUM(b.num_tickets), 0) DESC, e.date ASC LIMIT 1";
        List<Event> events = readEvents(sql, null);
        return events.isEmpty() ? null : events.get(0);
    }

    public int getTicketsSoldForEvent(int eventId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COALESCE(SUM(num_tickets), 0) AS tickets_sold FROM " + TABLE_BOOKINGS + " WHERE event_id=? AND status='confirmed'", new String[]{String.valueOf(eventId)});
        try {
            return cursor.moveToFirst() ? cursor.getInt(cursor.getColumnIndexOrThrow("tickets_sold")) : 0;
        } finally {
            cursor.close();
        }
    }

    public List<EventSalesStat> getTopEventSalesStats(int limit) {
        syncExpiredEvents();
        List<EventSalesStat> stats = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT e.id, e.title, COALESCE(SUM(b.num_tickets), 0) AS tickets_sold, " +
                        "COALESCE(COUNT(CASE WHEN b.status='confirmed' THEN b.id END), 0) AS booking_count " +
                        "FROM " + TABLE_EVENTS + " e " +
                        "LEFT JOIN " + TABLE_BOOKINGS + " b ON e.id=b.event_id AND b.status='confirmed' " +
                        "GROUP BY e.id ORDER BY tickets_sold DESC, booking_count DESC, e.date ASC LIMIT ?",
                new String[]{String.valueOf(limit)});
        try {
            while (cursor.moveToNext()) {
                stats.add(new EventSalesStat(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("tickets_sold")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("booking_count"))
                ));
            }
        } finally {
            cursor.close();
        }
        return stats;
    }

    public void syncExpiredEvents() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, date, time, status FROM " + TABLE_EVENTS, null);
        try {
            while (cursor.moveToNext()) {
                int eventId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                String nextStatus = EventUtils.isPastEvent(date, time) ? "completed" : ("cancelled".equalsIgnoreCase(status) ? "cancelled" : "active");
                if (!nextStatus.equalsIgnoreCase(status)) {
                    ContentValues values = new ContentValues();
                    values.put("status", nextStatus);
                    db.update(TABLE_EVENTS, values, "id=?", new String[]{String.valueOf(eventId)});
                }
            }
        } finally {
            cursor.close();
        }
    }
}
