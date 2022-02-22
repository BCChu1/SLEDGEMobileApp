package com.example.sledgemobileapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.OutputStream;
import java.util.UUID;

public class blueToothAddress {             //global object for all bluetooth stuff
    public static String address;
    public static BluetoothAdapter BTAdapter = null;
    public static BluetoothSocket BTSocket = null;
    public static OutputStream outputStream = null;
    public static BluetoothDevice device = null;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static blueToothAddress instance = new blueToothAddress();
    public blueToothAddress(){}
    public static blueToothAddress getInstance() {
        return instance;
    }

    public static void resetBTConnection() {
        try {
            BTSocket.close();
        } catch (Exception e) {
            return;
        }
        address = "";
        BTAdapter = null;
        BTSocket = null;
        outputStream = null;
        device = null;
    }
}
