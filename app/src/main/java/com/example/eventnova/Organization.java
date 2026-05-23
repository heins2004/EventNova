package com.example.eventnova;

public class Organization {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String category;
    private String location;

    public Organization(int id, String name, String email, String phone, String category, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.category = category;
        this.location = location;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
}
