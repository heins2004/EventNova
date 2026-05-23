package com.example.eventnova;

public class EventSalesStat {
    private final int eventId;
    private final String title;
    private final int ticketsSold;
    private final int bookingCount;

    public EventSalesStat(int eventId, String title, int ticketsSold, int bookingCount) {
        this.eventId = eventId;
        this.title = title;
        this.ticketsSold = ticketsSold;
        this.bookingCount = bookingCount;
    }

    public int getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public int getTicketsSold() {
        return ticketsSold;
    }

    public int getBookingCount() {
        return bookingCount;
    }
}
