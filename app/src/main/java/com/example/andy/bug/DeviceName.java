package com.example.andy.bug;

/**
 * Created by Andy on 2018-01-11.
 */

public class DeviceName {
    String Name;
    String Address;

    public DeviceName(String name, String address) {
        this.Name = name;
        this.Address = address;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }
}
