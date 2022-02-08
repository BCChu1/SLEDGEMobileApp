package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private ArrayAdapter<String> PairedDevicesArrayAdapter;
    private BluetoothSocket skt = null;
    private OutputStream outStrm = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    final static int BLUETOOTH_PERMISSION_CODE = 420;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);
        statusText = (TextView) findViewById(R.id.bluetoothStatusTextView);

        PairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        ListView deviceList = (ListView) findViewById(R.id.pairedDeviceList);
        deviceList.setAdapter(PairedDevicesArrayAdapter);
        deviceList.setOnItemClickListener(mDeviceClickListener);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_PERMISSION_CODE );
            }
        } catch (SecurityException e) {
            statusText.setText("Permission to use Bluetooth denied");
        }
    }

    public void onResume() {
        super.onResume();
        checkBTState();

        PairedDevicesArrayAdapter.clear();
        statusText.setText(" ");
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    PairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else {
                PairedDevicesArrayAdapter.add("No Devices Paired");
            }
        } catch (SecurityException e){
            statusText.setText("Error: couldn't retrieve linked devices");
        }
    }
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch(requestCode) {
            case BLUETOOTH_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                }
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
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(enableIntent);
            }
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            int error = 0;
            statusText.setText("Connecting...");

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            blueToothAddress.address = address;

            BluetoothDevice device = BTAdapter.getRemoteDevice(address);
            byte[] testMsg = "x".getBytes();

            try {       //creating socket
                skt = device.createRfcommSocketToServiceRecord(MY_UUID);
                statusText.append("Creating socket...");
            } catch (IOException e1) {
                statusText.setText("Error: Could not create Bluetooth Socket");
                error = 1;
            } catch (SecurityException e2) {
                statusText.setText("Error: Permission to create Bluetooth Socket denied");
                error = 1;
            }

            try {           //connecting to socket
                skt.connect();
                statusText.append("Connecting to socket...");
            } catch (IOException e) {
                try {
                    skt.close();
                } catch (IOException e2) {
                    statusText.setText("Error: Could not close Bluetooth Socket");
                    error = 1;
                }

            } catch (SecurityException e3) {
                    statusText.setText("Error: Permission for Bluetooth Connection denied");
                    error = 1;
                }

            try {       //establishing output stream
                outStrm = skt.getOutputStream();
                statusText.append("Creating output stream...");
            } catch (IOException e) {
                statusText.setText("Error: Could not set up output stream");
                error = 1;
            }

            try {
                outStrm.write(testMsg);
                statusText.append("Writing test output...");
            } catch (IOException e) {
                statusText.setText("Error: Could not write to output stream");
                error = 1;
            }
            if (error == 0) {
                statusText.append("Connected!");
            } else {
                blueToothAddress.address = "";
            }

        }
    };
}