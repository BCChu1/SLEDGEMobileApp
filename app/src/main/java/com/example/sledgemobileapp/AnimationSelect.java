package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class AnimationSelect extends AppCompatActivity {
    private BluetoothAdapter BTAdapter = null;
    private BluetoothSocket BTSocket = null;
    private OutputStream outputStream = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    ListView presetListview;
    ArrayAdapter<String> adapter;
    SingleLoadedList loadedList;
    ArrayList<String> presetNames = new ArrayList<>();
    ArrayList<PresetClass> presetList = new ArrayList<>();
    PresetClass preset = new PresetClass();
    public String BTAddress = "";
    TextView statusText;
    boolean deleteMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_select);

        presetListview = findViewById(R.id.presetListview);
        loadedList = SingleLoadedList.getInstance();
        statusText = findViewById(R.id.statusText2);
        preset.animations = SingleLoadedList.animations;
        preset.animationNames = SingleLoadedList.animationNames;

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,presetNames);

        presetListview.setAdapter(adapter);

        presetListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (deleteMode) {
                    adapter.notifyDataSetChanged();
                } else {                                    //putting selected preset in list to potentially upload
                    SingleLoadedList.animations.clear();
                    SingleLoadedList.animationNames.clear();
                    SingleLoadedList.animations = presetList.get(i).animations;
                    SingleLoadedList.animationNames = presetList.get(i).animationNames;
                    //upload here?

                }
            }
        });

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
    public void deleteToggle(View v) {
        if (deleteMode) {
            deleteMode = false;
            v.setBackgroundColor(Color.BLUE);
        } else {
            deleteMode = true;
            v.setBackgroundColor(Color.RED);

        }
    }
    public void loadAllPresets() {
        try {
            FileInputStream fileInputStream = openFileInput("Presets.txt");
            ObjectInputStream OIS = new ObjectInputStream(fileInputStream);
            presetList = (ArrayList<PresetClass>) OIS.readObject();
            if (presetList == null) {
                statusText.setText("Error: Could not load presets!");
                return;
            }
            presetNames.clear();
            for (PresetClass x : presetList) {
                presetNames.add(x.name);
            }
            adapter.notifyDataSetChanged();
        } catch (FileNotFoundException e1) {
            statusText.setText("Error: Could not find storage file for presets!");
        } catch (IOException e2) {
            statusText.setText("Error: Could not open storage file for presets!");
        }catch (ClassNotFoundException e3) {
            statusText.setText("Error: Class Not Found Exception!");
        }
    }

    public void deletePresetFromFile(int index) {
        try {
            presetList.remove(index);
            FileOutputStream FOS = openFileOutput("Presets.txt", MODE_PRIVATE);
            ObjectOutputStream OOS = new ObjectOutputStream(FOS);
            OOS.writeObject(presetList);
        } catch (FileNotFoundException e1) {

        } catch (IOException e2) {

        }
        loadAllPresets();
    }
}