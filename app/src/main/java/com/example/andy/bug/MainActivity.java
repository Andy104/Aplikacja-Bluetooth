package com.example.andy.bug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    //Serwisy i ich charakterystyki - wykryte na module
    public static final String UUID_SERVICE_GENERIC_ACCESS = "00001800-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_DEVICE_NAME = "00002a00-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_APPEARANCE = "00002a01-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_PREFERRED_CON_PARAMS = "00002a04-0000-1000-8000-00805f9b34fb";

    public static final String UUID_SERVICE_GENERIC_ATTRIBUTE = "00001801-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_SERVICE_CHANGED = "00002a05-0000-1000-8000-00805f9b34fb";

    public static final String UUID_SERVICE_DEVICE_INFORMATION = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_MODEL_NUMBER = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_SERIAL_NUMBER = "00002a25-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_FIRMWARE_VERSION = "00002a26-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_SOFTWARE_REVISION = "00002a28-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";

    public static final String UUID_SERVICE_BATTERY_SERVOCE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String UUID_CHARACTERISTIC_BATTERY_LEVEL = "00002a19-0000-1000-8000-00805f9b34fb";

    public static final String UUID_SERVICE_NORDIC_DEVICE_FIRMWARE_UPDATE_SERVICE = "00001530-1212-efde-1523-785feabcd123";
    public static final String UUID_CHARACTERISTIC_DFU_PACKET = "00001532-1212-efde-1523-785feabcd123";
    public static final String UUID_CHARACTERISTIC_DFU_CONTROL_POINT = "00001531-1212-efde-1523-785feabcd123";
    public static final String UUID_CHARACTERISTIC_DFU_VERSION = "00001534-1212-efde-1523-785feabcd123";

    public static final String UUID_SERVICE_NORDIC_UART = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_CHARACTERISTIC_UART_RX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_CHARACTERISTIC_UART_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SCAN_PERIOD = 200;

    private static final int MODE_BLE = 0;
    private static final int MODE_AUTO = 1;
    private static final int MODE_MANUAL = 2;
    private static final int MODE_INFO = 3;
    private static final int POWER_OFF = 4;
    private static final int POWER_ON = 5;

    private int currentState;
    private int currentPower;
    private boolean connected;
    private String deviceID;

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private List<ScanFilter> filters;
    private BluetoothGatt mGatt;
    private BluetoothGattService mUartService;
    private BluetoothGattCharacteristic mRx;
    private BluetoothGattCharacteristic mTx;
    private BluetoothGattDescriptor descriptor;
    private BluetoothManager bluetoothManager;
    BluetoothDevice btDevice;

    private ImageButton buttonPower;
    private TextView powerLabel;
    private ImageButton buttonSave;
    private TextView saveLabel;

    //BLE
    private ArrayList<DeviceName> mData;
    private ListView listView;
    private CustomAdapter adapter;
    private TextView mText1;
    private TextView mText2;

    //Manual
    private ImageButton buttonUp;
    private ImageButton buttonDown;
    private ImageButton buttonLeft;
    private ImageButton buttonRight;

    //Information
    private ImageView mLogo;
    private TextView mTextTitle;
    private TextView mTextVersion;
    private TextView mTextCredits;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_ble:
                    mText1.setVisibility(View.VISIBLE);
                    mText2.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.VISIBLE);
                    buttonUp.setVisibility(View.INVISIBLE);
                    buttonDown.setVisibility(View.INVISIBLE);
                    buttonLeft.setVisibility(View.INVISIBLE);
                    buttonRight.setVisibility(View.INVISIBLE);
                    mLogo.setVisibility(View.INVISIBLE);
                    mTextTitle.setVisibility(View.INVISIBLE);
                    mTextVersion.setVisibility(View.INVISIBLE);
                    mTextCredits.setVisibility(View.INVISIBLE);
                    buttonPower.setVisibility(View.INVISIBLE);
                    powerLabel.setVisibility(View.INVISIBLE);
                    buttonSave.setVisibility(View.INVISIBLE);
                    saveLabel.setVisibility(View.INVISIBLE);

                    String text1;
                    String text2;
                    if (connected) {
                        text1 = "Witaj na pokładzie kapitanie!";
                        text2 = deviceID;
                    } else {
                        text1 = "Brak połączonego urządzenia";
                        text2 = null;
                    }
                    mText1.setText(text1);
                    mText2.setText(text2);

                    currentState = MODE_BLE;
                    return true;
                case R.id.navigation_auto:
                    mText1.setVisibility(View.INVISIBLE);
                    mText2.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    buttonUp.setVisibility(View.INVISIBLE);
                    buttonDown.setVisibility(View.INVISIBLE);
                    buttonLeft.setVisibility(View.INVISIBLE);
                    buttonRight.setVisibility(View.INVISIBLE);
                    mLogo.setVisibility(View.INVISIBLE);
                    mTextTitle.setVisibility(View.INVISIBLE);
                    mTextVersion.setVisibility(View.INVISIBLE);
                    mTextCredits.setVisibility(View.INVISIBLE);
                    buttonPower.setVisibility(View.VISIBLE);
                    powerLabel.setVisibility(View.VISIBLE);
                    buttonSave.setVisibility(View.VISIBLE);
                    saveLabel.setVisibility(View.VISIBLE);

                    currentState = MODE_AUTO;
                    return true;
                case R.id.navigation_manual:
                    mText1.setVisibility(View.INVISIBLE);
                    mText2.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    buttonUp.setVisibility(View.VISIBLE);
                    buttonDown.setVisibility(View.VISIBLE);
                    buttonLeft.setVisibility(View.VISIBLE);
                    buttonRight.setVisibility(View.VISIBLE);
                    mLogo.setVisibility(View.INVISIBLE);
                    mTextTitle.setVisibility(View.INVISIBLE);
                    mTextVersion.setVisibility(View.INVISIBLE);
                    mTextCredits.setVisibility(View.INVISIBLE);
                    buttonPower.setVisibility(View.VISIBLE);
                    powerLabel.setVisibility(View.VISIBLE);
                    buttonSave.setVisibility(View.VISIBLE);
                    saveLabel.setVisibility(View.VISIBLE);

                    currentState = MODE_MANUAL;
                    return true;
                case R.id.navigation_info:
                    mText1.setVisibility(View.INVISIBLE);
                    mText2.setVisibility(View.INVISIBLE);
                    listView.setVisibility(View.INVISIBLE);
                    buttonUp.setVisibility(View.INVISIBLE);
                    buttonDown.setVisibility(View.INVISIBLE);
                    buttonLeft.setVisibility(View.INVISIBLE);
                    buttonRight.setVisibility(View.INVISIBLE);
                    mLogo.setVisibility(View.VISIBLE);
                    mTextTitle.setVisibility(View.VISIBLE);
                    mTextVersion.setVisibility(View.VISIBLE);
                    mTextCredits.setVisibility(View.VISIBLE);
                    buttonPower.setVisibility(View.INVISIBLE);
                    powerLabel.setVisibility(View.INVISIBLE);
                    buttonSave.setVisibility(View.INVISIBLE);
                    saveLabel.setVisibility(View.INVISIBLE);

                    currentState = MODE_INFO;
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
        buttonPower = findViewById(R.id.btnPower);
        powerLabel = findViewById(R.id.labelPower);
        buttonSave = findViewById(R.id.btnSave);
        saveLabel = findViewById(R.id.labelSave);

        //BLE
        listView = findViewById(R.id.list);
        mText1 = findViewById(R.id.title);
        mText2 = findViewById(R.id.btConnection);

        //Manual
        buttonUp = findViewById(R.id.btnUp);
        buttonDown = findViewById(R.id.btnDown);
        buttonLeft = findViewById(R.id.btnLeft);
        buttonRight = findViewById(R.id.btnRight);

        //Information
        mLogo = findViewById(R.id.logo);
        mTextTitle = findViewById(R.id.creditsTitle);
        mTextVersion = findViewById(R.id.creditsVersion);
        mTextCredits = findViewById(R.id.creditsText);

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Log.d(TAG, "onCreate started");

        currentState = MODE_BLE;
        currentPower = POWER_OFF;
        connected = false;
        mData = new ArrayList<>();  //nowa lista bez wartości
        navigation.setSelectedItemId(R.id.navigation_ble);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        try {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        } catch (Exception e) {
            Log.e(TAG, "mBluetoothAdapter" + e.toString());
        }
        Log.d(TAG, "onCreate -> BLE supported, BluetoothAdapter created");

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
                if (dataModel.getName() == null) {
                    Toast.makeText(getApplicationContext(), dataModel.getAddress(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), dataModel.getName(), Toast.LENGTH_SHORT).show();
                }

                if (mGatt == null) {
                    mGatt = dataModel.getDevice().connectGatt(getApplicationContext(), true, gattCallback);
                }

                try {
                    mGatt.connect();
                    connected = true;
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

                if (connected) {
                    mText1.setText(R.string.title_hello);
                    if (dataModel.getName() != null) {
                        deviceID = dataModel.getName();
                    } else {
                        deviceID = dataModel.getAddress();
                    }
                    mText2.setText(deviceID);
                    currentState = MODE_AUTO;
                    navigation.setSelectedItemId(R.id.navigation_auto);
                }
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
            mData.add(new DeviceName(btDevice.getName(), btDevice.getAddress(), btDevice));
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                mData.add(new DeviceName(sr.getDevice().getName(), sr.getDevice().getAddress(), btDevice));
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
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            Log.d("onServicesDiscovered", services.toString());

            mUartService = mGatt.getService(UUID.fromString(UUID_SERVICE_NORDIC_UART));
            if (mUartService != null) {
                mTx = mUartService.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_UART_TX));
                mRx = mUartService.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_UART_RX));
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            Log.d("onCharacteristicRead", characteristic.getValue().toString());
            //gatt.disconnect();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.d("onCharacteristicChanged", String.valueOf(characteristic.getValue()));
        }
    };

    public void writeMessage(String str) {
        String value1 = "4";
        byte[] value = value1.getBytes(Charset.forName("UTF-8"));

        if (mTx != null) {
            mTx.setValue(value);
            mGatt.writeCharacteristic(mTx);

            Log.d(TAG, "UART BLE Tx: " + value[0]);
        } else {
            Log.d(TAG, "UART BLE characteristic Tx NOT found ");
        }

        /*
        byte[] value = str.getBytes(Charset.forName("UTF-8"));
        byte[] chunk = null;

        mUartService = mGatt.getService(UUID.fromString(UUID_SERVICE_NORDIC_UART));
        if (mUartService != null) {
            for (int i = 0; i < value.length; i += 20) {
                chunk = Arrays.copyOfRange(value, i, Math.min(i + 20, value.length));
                mTx = mUartService.getCharacteristic(UUID.fromString(UUID_CHARACTERISTIC_UART_TX));
                if (mTx != null) {
                    mTx.setValue(chunk);
                    mTx.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                } else {
                    Log.d(TAG, "UART BLE characteristic Tx NOT found ");
                }
            }
            Toast.makeText(getApplicationContext(), "Send: " + str, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "UART BLE service NOT found ");
        }
        */
    }

    public void readMesssage() {
        String msg = null;

        if (mRx != null) {
            mGatt.readCharacteristic(mRx);
        } else {
            Log.d(TAG, "UART BLE characteristic Rx NOT found ");
        }
    }

    public void moveUp(View view) {
        String str = "Hello";
        writeMessage(str);
    }

    public void moveDown(View view) {
    }

    public void moveLeft(View view) {
    }

    public void moveRight(View view) {
    }

    public void changePwr(View view) {
        if (currentPower == POWER_OFF) {
            currentPower = POWER_ON;
            buttonPower.setImageResource(R.drawable.ic_power_on);
        } else if (currentPower == POWER_ON) {
            currentPower = POWER_OFF;
            buttonPower.setImageResource(R.drawable.ic_power_off);
        }
    }

    public void saveImage(View view) {
    }
}
