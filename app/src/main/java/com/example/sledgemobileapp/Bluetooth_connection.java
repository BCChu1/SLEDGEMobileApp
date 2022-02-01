package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class Bluetooth_connection extends AppCompatActivity {
    TextView statusText;
    ListView deviceList;
    private BluetoothAdapter BTAdapter;
    ArrayAdapter<String> PairedDevicesArrayAdapter;
    private BluetoothSocket skt = null;
    private OutputStream outStrm = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        statusText = (TextView) findViewById(R.id.bluetoothStatusTextView);

        PairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_bluetooth_connection);

        deviceList = (ListView) findViewById(R.id.pairedDeviceList);
        deviceList.setAdapter(PairedDevicesArrayAdapter);
        deviceList.setOnItemClickListener(mDeviceClickListener);
    }
    public void onResume() {
        super.onResume();
        checkBTState();

        PairedDevicesArrayAdapter.clear();
        statusText.setText(" ");
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                PairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            PairedDevicesArrayAdapter.add("No Devices Paired");
        }

    }

    private void checkBTState() {
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (BTAdapter == null) {
            statusText.setText("Error: Device does not support Bluetooth");
            finish();
        } else {
            if (!BTAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            }
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v,int arg2, long arg3) {
            statusText.setText("Connecting...");

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            blueToothAddress.address = address;

            BluetoothDevice device = BTAdapter.getRemoteDevice(address);
            byte[] testMsg = "x".getBytes();

            try {       //creating socket
                skt = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e1) {
                statusText.setText("Error: Could not create Bluetooth Socket");
            }

            try {           //connecting to socket
                skt.connect();
            } catch (IOException e) {
                try {
                    skt.close();
                } catch (IOException e2) {
                    statusText.setText("Error: Could not close Bluetooth Socket");
                }
            }

            try {       //establishing output stream
                outStrm = skt.getOutputStream();
            } catch (IOException e) {
                statusText.setText("Error: Could not set up output stream");
            }

            try {
                outStrm.write(testMsg);
            } catch (IOException e) {
                statusText.setText("Error: Could not write to output stream");
            }
        }
    };
}