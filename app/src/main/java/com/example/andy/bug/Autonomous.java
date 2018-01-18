package com.example.andy.bug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Andy on 2018-01-17.
 */

public class Autonomous extends AppCompatActivity{
    private static final String TAG = "Autonomous";

    private TextView mText;
    private BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice btDevice;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_ble:
                    mText.setText(btDevice.getName());
                    return true;
                case R.id.navigation_auto:
                    //mTextMessage3.setText(R.string.title_auto);
                    return true;
                case R.id.navigation_manual:
                    //mTextMessage3.setText(R.string.title_manual);
                    return true;
                case R.id.navigation_info:
                    //mTextMessage3.setText(R.string.title_info);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto);

        //Intent intent = getIntent();
        //btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        //btDevice = mBluetoothAdapter.getRemoteDevice();

        mText = (TextView) findViewById(R.id.message1);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.d(TAG, "onCreate started");

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Log.d(TAG, "onCreate -> BLE supported, BluetoothAdapter created");

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Error: no bleAdapter", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "mBluetoothAdapter == null");
        } else {
            mText.setText(btDevice.getName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
