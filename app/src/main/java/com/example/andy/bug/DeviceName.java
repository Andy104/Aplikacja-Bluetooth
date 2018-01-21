package com.example.andy.bug;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Andy on 2018-01-11.
 */

public class DeviceName {
    String Name;
    String Address;
    BluetoothDevice Device;

    public DeviceName(String name, String address, BluetoothDevice device) {
        this.Name = name;
        this.Address = address;
        this.Device = device;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public BluetoothDevice getDevice() {
        return Device;
    }
}
