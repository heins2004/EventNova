package com.example.eventnova;

public class Event {
    private final int id;
    private final int orgId;
    private final String orgName;
    private final String title;
    private final String description;
    private final String category;
    private final String location;
    private final String date;
    private final String time;
    private final double price;
    private final int availableSeats;
    private final int totalSeats;
    private final String image;
    private final String status;

    public Event(int id, int orgId, String title, String description, String category, String location, String date, String time, double price, int availableSeats, int totalSeats, String image, String status) {
        this(id, orgId, "", title, description, category, location, date, time, price, availableSeats, totalSeats, image, status);
    }

    public Event(int id, int orgId, String orgName, String title, String description, String category, String location, String date, String time, double price, int availableSeats, int totalSeats, String image, String status) {
        this.id = id;
        this.orgId = orgId;
        this.orgName = orgName;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.date = date;
        this.time = time;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.image = image;
        this.status = status;
    }

    public int getId() { return id; }
    public int getOrgId() { return orgId; }
    public String getOrgName() { return orgName; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public double getPrice() { return price; }
    public int getAvailableSeats() { return availableSeats; }
    public int getTotalSeats() { return totalSeats; }
    public String getImage() { return image; }
    public String getStatus() { return status; }
}
