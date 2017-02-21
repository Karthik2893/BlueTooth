package com.hfad.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "Bluetooth App :";
    private int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mLEScanner;
    private ScanSettings settings;
    private BluetoothGatt mGatt;
    private List<ScanFilter> filters;
    private Handler mHandler;
    private static final long SCAN_TIME = 1000;
    boolean mScanning = false;
    private int REQUEST_LOCATION = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this,"BLE Not supported",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Failure");
            finish();
        }
        else
        {
            Toast.makeText(this,"BLE Supported Yayy",Toast.LENGTH_SHORT).show();
            Log.v(LOG_TAG,"Success");
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
            Log.v(LOG_TAG,"Asking Location permissions");
        }
        final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE));
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if(mBluetoothAdapter == null)
        {
            Toast.makeText(this,"Bluetooth Not Supported",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
        }
        else
        {
            if(Build.VERSION.SDK_INT >=21)
            {
                mLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
                settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
//                filters = new ArrayList<ScanFilter>;
            }
        }
        if(Build.VERSION.SDK_INT >=21)
            scanLeDevice(true);
        else
        {
            Log.v(LOG_TAG,"Version too low");
            finish();
            return;
        }
    }
    private void scanLeDevice(final boolean enable)
    {
        if(enable)
        {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.v(LOG_TAG,"Calling stopLeScan");
                    mLEScanner.stopScan(mLeScanCallback);
                }
            },SCAN_TIME);
            mScanning = true;
            Log.v(LOG_TAG,"Calling StartLeScan");
        }
        else
        {
            mScanning = false;
            mLEScanner.stopScan(mLeScanCallback);
        }
    }
}



/*
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
    {
        public void onLeScan(final BluetoothDevice,int rssi,byte[] scanRecord)
        {
            Log.v(LOG_TAG,"Inside OnLeScan Method");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v(LOG_TAG,"Inside RUnnable");
                }
            });
        }
    };*/
