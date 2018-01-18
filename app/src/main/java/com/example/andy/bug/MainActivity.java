package com.example.andy.bug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private final static int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 200;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    BluetoothDevice btDevice;

    private ArrayList<DeviceName> mData;
    private ListView listView;
    private CustomAdapter adapter;
    private TextView mTextMessage1;
    private TextView mTextMessage2;
    private TextView mTextMessage3;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_ble:
                    mTextMessage3.setText(R.string.title_ble);
                    return true;
                case R.id.navigation_auto:
                    mTextMessage3.setText(R.string.title_auto);
                    Intent intent = new Intent(MainActivity.this, Autonomous.class);
                    intent.putExtra("device", BluetoothDevice.EXTRA_DEVICE);
                    startActivityForResult(intent, 3);
                    return true;
                case R.id.navigation_manual:
                    mTextMessage3.setText(R.string.title_manual);
                    return true;
                case R.id.navigation_info:
                    mTextMessage3.setText(R.string.title_info);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mTextMessage1 = (TextView) findViewById(R.id.message1);
        mTextMessage2 = (TextView) findViewById(R.id.message2);
        mTextMessage3 = (TextView) findViewById(R.id.message3);
        listView = (ListView)findViewById(R.id.list);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.d(TAG, "onCreate started");

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        Log.d(TAG, "onCreate -> BLE supported, BluetoothAdapter created");

        mData = new ArrayList<>();  //nowa lista bez wartości

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            filters = new ArrayList<ScanFilter>();
            scanLeDevice(true);
        }

        adapter = new CustomAdapter(mData, getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DeviceName dataModel = mData.get(position);
                Toast.makeText(getApplicationContext(), dataModel.getName(), Toast.LENGTH_SHORT).show();

                if (mGatt == null) {
                    mGatt = btDevice.connectGatt(getApplicationContext(), false, gattCallback);
                } else {
                    mGatt.connect();
                }
                mTextMessage1.setText("Selected device: " + btDevice.getName());
                mTextMessage2.setText("Address: " + btDevice.getAddress());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLEScanner != null){
            mLEScanner.stopScan(mScanCallback);
        }
        if (mGatt != null) {
            mGatt.disconnect();
        }
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mLEScanner.stopScan(mScanCallback);
                }
            }, SCAN_PERIOD);
            mLEScanner.startScan(filters, settings, mScanCallback);
        } else {
            mLEScanner.stopScan(mScanCallback);
        }
    }

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            Log.i("callbackType", String.valueOf(callbackType));
            Log.i("result", result.toString());

            btDevice = result.getDevice();
            mData.add(new DeviceName(btDevice.getName(), btDevice.getAddress()));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                mData.add(new DeviceName(sr.getDevice().getName(), sr.getDevice().getAddress()));
                adapter.notifyDataSetChanged();
                Log.d("ScanResult - Results", sr.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("Scan Failed", "Error Code: " + errorCode);
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            //TextView text = (TextView) findViewById(R.id.message3);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    //mTextMessage3.setText("Status: CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    //text.setText("Status: DISCONNECTED (" + status + ")");
                    break;
                default:
                    //text.setText("Status: OTHER (" + status + ")");
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
