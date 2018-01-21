package com.example.andy.bug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andy on 2018-01-18.
 */

public class Manual extends AppCompatActivity{

    private static final String TAG = "Manual Mode";
    public static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_RX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_DFU = "00001530-1212-EFDE-1523-785FEABCD123";

    private boolean powerOn;
    private byte[] bytes;
    private String str;
    private BluetoothGatt mGatt;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mAdapter;
    private BluetoothManager mManager;
    private BluetoothGattService mUartService;
    private List<BluetoothGattService> mList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = new Bundle();
        mDevice = bundle.getParcelable("device");

        mGatt = mDevice.connectGatt(getApplicationContext(), false, gattCallback);
        mList = mGatt.getServices();

        /*
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = "Hello";

                final byte[] value = str.getBytes(Charset.forName("UTF-8"));
                if (mUartService != null) {
                    // Split the value into chunks (UART service has a maximum number of characters that can be written )
                    for (int i = 0; i < value.length; i += 20) {
                        final byte[] chunk = Arrays.copyOfRange(value, i, Math.min(i + 20, value.length));
                        //mManager.writeService(mUartService, UUID_TX, chunk);
                        final UUID characteristicUuid = UUID.fromString(UUID_TX);
                        final BluetoothGattCharacteristic characteristic = mUartService.getCharacteristic(characteristicUuid);
                        if (characteristic != null) {
                            characteristic.setValue(chunk);
                            mGatt.writeCharacteristic(characteristic);
                        } else {
                            Log.w(TAG, "write: characteristic not found: " + UUID_TX);
                        }
                    }
                } else {
                    Log.w(TAG, "Uart Service not discovered. Unable to send data");
                }
            }
        });
        */
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            gatt.readCharacteristic(services.get(1).getCharacteristics().get
                    (0));
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i("onCharacteristicRead", characteristic.toString());
            gatt.disconnect();
        }
    };
}
