package com.example.projekkhayalan.models;

public class DisabilityType {
    private int id;
    private String name;
    private int iconResourceId;

    public DisabilityType(int id, String name, int iconResourceId) {
        this.id = id;
        this.name = name;
        this.iconResourceId = iconResourceId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }
}