package com.example.andy.robotbt;

/**
 * Created by Andy on 2018-01-11.
 */

public class DeviceName {
    String Name;
    String Address;
    int ID;

    public DeviceName(String name, String address, int id) {
        this.Name = name;
        this.Address = address;
        this.ID = id;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public int getID() {
        return ID;
    }
}
