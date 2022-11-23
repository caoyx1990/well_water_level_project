package com.tencent.wxcloudrun.model;

import javax.persistence.*;

@Entity
@Table(name = "alert")
public class Alert {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int id;

    private int number;

    private String device;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
