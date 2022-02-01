package com.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MyDate {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LocalDateTime getDate() {
        return this.date;
    }
    private void setDate(LocalDateTime date) {
        this.date = date;
    }
    private LocalDateTime date;

    public String getFormattedDate() {
        return this.dateFormatter.format(this.date);
    }

    public String getFormattedTime() {
        return this.timeFormatter.format(this.date);
    }

    public MyDate(LocalDateTime date) {
        this.setDate(date);
    }

    public MyDate() {
        this.setDate(LocalDateTime.now());
    }
}
