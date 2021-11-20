package com.example.expensemanager.model;

public class Data {

    private int Amount;
    private String type;
    private String note;
    private String id;
    private String date;

    public Data() {

    }

    public Data(int amount, String type, String note, String id, String date) {
        Amount = amount;
        this.type = type;
        this.note = note;
        this.id = id;
        this.date = date;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
