package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class AnimationSelect extends AppCompatActivity {
    private BluetoothAdapter BTAdapter = null;
    private BluetoothSocket BTSocket = null;
    private OutputStream outputStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ListView animationList;
    ArrayAdapter<String> adapter;
    SingleLoadedList loadedList;
    public String BTAddress = "";
    TextView statusText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_select);

        animationList = findViewById(R.id.animationListview);
        loadedList = SingleLoadedList.getInstance();
        statusText = findViewById(R.id.statusText2);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,loadedList.animationNames);

        animationList.setAdapter(adapter);

    }
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        BTAddress = blueToothAddress.address;
        try {
            BluetoothDevice device = BTAdapter.getRemoteDevice(BTAddress);
        } catch (Exception e) {
            statusText.setText("Error: Could not get address of SLEDGE");
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            outputStream.write(msgBuffer);
            statusText.setText("Sending data to SLEDGE...");
        } catch (IOException e) {
            statusText.setText("Error: Failed to send data to SLEDGE! Please go back and do Bluetooth Check");
        }
    }
    public void upload(View v) {
        sendData("u");
        statusText.setText("");
        statusText.setText("Uploading...");
        for (animationInfo a : SingleLoadedList.animations) {
            sendData(String.valueOf(a.id) + "/");

            for(int x = 0; x < a.num_inputs - 2; x++) {
                sendData(String.valueOf(a.input_values[x]) + "/");
            }
            sendData("o");

            sendData(String.valueOf(a.input_values[a.input_values.length - 2]) + "/");

            sendData(String.valueOf(a.input_values[a.input_values.length - 1]) + "/");
        }
        sendData("e");
        statusText.append("Finished!");
    }
}