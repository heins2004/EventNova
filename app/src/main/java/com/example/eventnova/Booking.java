package com.example.eventnova;

public class Booking {
    private final int id;
    private final int userId;
    private final String userName;
    private final int eventId;
    private final String eventTitle;
    private final String bookingDate;
    private final String eventDate;
    private final String eventTime;
    private final String location;
    private final String category;
    private final String organizerName;
    private final int numTickets;
    private final double totalPrice;
    private final String status;

    public Booking(int id, int userId, int eventId, String eventTitle, String bookingDate, String eventDate, String eventTime, String location, String category, String organizerName, int numTickets, double totalPrice, String status) {
        this(id, userId, "", eventId, eventTitle, bookingDate, eventDate, eventTime, location, category, organizerName, numTickets, totalPrice, status);
    }

    public Booking(int id, int userId, String userName, int eventId, String eventTitle, String bookingDate, String eventDate, String eventTime, String location, String category, String organizerName, int numTickets, double totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.bookingDate = bookingDate;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.location = location;
        this.category = category;
        this.organizerName = organizerName;
        this.numTickets = numTickets;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public int getEventId() { return eventId; }
    public String getEventTitle() { return eventTitle; }
    public String getBookingDate() { return bookingDate; }
    public String getEventDate() { return eventDate; }
    public String getEventTime() { return eventTime; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public String getOrganizerName() { return organizerName; }
    public int getNumTickets() { return numTickets; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
}
