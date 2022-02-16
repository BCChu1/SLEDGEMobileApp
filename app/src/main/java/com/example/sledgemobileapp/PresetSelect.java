package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class PresetSelect extends AppCompatActivity {
    private BluetoothAdapter BTAdapter = null;
    private BluetoothSocket BTSocket = null;
    private OutputStream outputStream = null;
    private BluetoothDevice device = null;
    public static int START_CREATE_PRESET = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String BTAddress = "";
    //above is bluetooth stuff
    ListView presetListview;                    //listview for presets
    ArrayAdapter<String> adapter;               //used to display list of presets in list view
    SingleLoadedList loadedList;                //list to upload into SLEDGE
    ArrayList<String> presetNames = new ArrayList<>();
    ArrayList<PresetClass> presetList = new ArrayList<>();
    PresetClass preset = new PresetClass();     //preset object
    TextView statusText;                        //display some messages to user
    boolean deleteMode = false;
    AlertDialog.Builder builder;                //used for upload pop-up confirmation message
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_select);

        presetListview = findViewById(R.id.presetListview);
        loadedList = SingleLoadedList.getInstance();
        statusText = findViewById(R.id.statusText2);
        preset.animations = SingleLoadedList.animations;
        preset.animationNames = SingleLoadedList.animationNames;
        //testing
        preset.name = "Test";
        //presetList.add(preset);
        //loadAllPresets();
        //presetNames.add("Test");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,presetNames);
        BTAdapter =BluetoothAdapter.getDefaultAdapter();

        presetListview.setAdapter(adapter);

        presetListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (deleteMode) {
                    deletePresetFromFile(i);
                } else {                                    //putting selected preset in list to potentially upload
                    SingleLoadedList.clearAll();
                    SingleLoadedList.animations = presetList.get(i).animations;
                    SingleLoadedList.animationNames = presetList.get(i).animationNames;
                    builder = new AlertDialog.Builder(PresetSelect.this);
                    builder.setCancelable(true);
                    builder.setMessage("Do you want to upload " + presetList.get(i).name +" ?");
                    builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            upload();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SingleLoadedList.animations.clear();
                            SingleLoadedList.animationNames.clear();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();                          //prompt for user if they want to confirm upload to SLEDGE

                }
            }
        });
        loadAllPresets();

    }
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        BTAddress = blueToothAddress.address;

        try {
            device = BTAdapter.getRemoteDevice(BTAddress);
        } catch (Exception e) {
            Toast.makeText(PresetSelect.this, "Error: Could not find device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return;
        }

        try {
            BTSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (SecurityException e1) {
            Toast.makeText(PresetSelect.this, "Error: Could not create socket, permission denied", Toast.LENGTH_SHORT).show();
        } catch (IOException e2) {
            Toast.makeText(PresetSelect.this, "Error: Could not make Bluetooth Socket", Toast.LENGTH_SHORT).show();
        }

        try {
            BTSocket.connect();
        } catch (IOException e1) {
            try {
                BTSocket.close();
            } catch (IOException e2) {
                Toast.makeText(PresetSelect.this, "Error: Could close Bluetooth Connection", Toast.LENGTH_SHORT).show();
            }
        } catch (SecurityException e2) {
            Toast.makeText(PresetSelect.this, "Error: Not allowed to conect to socket", Toast.LENGTH_SHORT).show();
        }

        try {
            outputStream = BTSocket.getOutputStream();
        } catch (IOException e1) {
            Toast.makeText(PresetSelect.this, "Error: Could not create output stream", Toast.LENGTH_SHORT).show();
        }
        sendData("x");


    }

    public void onPause() {
        super.onPause();
        if (BTSocket == null) {
            return;
        }
        try {
            BTSocket.close();
        } catch (IOException e1) {
            statusText.setText("Error: Failed to close Bluetooth connection");
        }
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            if (outputStream != null) {
                outputStream.write(msgBuffer);
                statusText.setText("Sending data to SLEDGE...");
            } else {
                Toast.makeText(PresetSelect.this, "Error: Output stream is null", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            statusText.setText("Error: Failed to send data to SLEDGE! Please go back and do Bluetooth Check");
        }
        statusText.setText("Data sent successfully!");
    }
    public void upload() {
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
                //statusText.setText("Error: Could not load presets!");
                Toast.makeText(PresetSelect.this, "List is null", Toast.LENGTH_SHORT).show();
                //presetList.add(preset);
                return;
            }
            presetNames.clear();
            //testing
            //presetList.add(preset);
            for (PresetClass x : presetList) {
                presetNames.add(x.name);
            }
            adapter.notifyDataSetChanged();
        } catch (FileNotFoundException e1) {
            //statusText.setText("Error: Could not find storage file for presets!");
            Toast.makeText(PresetSelect.this, "Error: Can't find Presets.txt file", Toast.LENGTH_SHORT).show();
        } catch (IOException e2) {
            //statusText.setText("Error: Could not open storage file for presets!");
            Toast.makeText(PresetSelect.this, "Error: Can't read from preset file", Toast.LENGTH_SHORT).show();
        }catch (ClassNotFoundException e3) {            //this one should never occur
            //statusText.setText("Error: Class Not Found Exception!");
            Toast.makeText(PresetSelect.this, "Class not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void deletePresetFromFile(int index) {
        try {
            presetList.remove(index);
            FileOutputStream FOS = openFileOutput("Presets.txt", MODE_PRIVATE);
            ObjectOutputStream OOS = new ObjectOutputStream(FOS);
            OOS.writeObject(presetList);
        } catch (FileNotFoundException e1) {
            Toast.makeText(PresetSelect.this, "Error: Can't find Presets.txt file", Toast.LENGTH_SHORT).show();
        } catch (IOException e2) {
            Toast.makeText(PresetSelect.this, "Error: Can't write to preset file", Toast.LENGTH_SHORT).show();
        }
        loadAllPresets();
    }
    public void addPresetToFile() {
        //spawn new activity for setting preset name and add animations to preset
        //get object returned
        //add to file and update

        try {
            FileOutputStream FOS = openFileOutput("Presets.txt", MODE_PRIVATE);
            ObjectOutputStream OOS = new ObjectOutputStream(FOS);
            presetList.add(preset);
            OOS.writeObject(presetList);
            statusText.setText("Preset successfully added!");
        } catch (FileNotFoundException e1) {
            Toast.makeText(PresetSelect.this, "Error: Can't find Presets.txt file", Toast.LENGTH_SHORT).show();
        } catch (IOException e2) {
            Toast.makeText(PresetSelect.this, "Error: Can't write to preset file", Toast.LENGTH_SHORT).show();
        }
        loadAllPresets();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {         //returning to Preset Select from Create Preset
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_CREATE_PRESET) {
            preset.animations = SingleLoadedList.animations;
            preset.animationNames = SingleLoadedList.animationNames;
            preset.name = SingleLoadedList.newPresetName;
            addPresetToFile();
            SingleLoadedList.clearAll();
            Toast.makeText(PresetSelect.this, "Successfully added a new preset!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(PresetSelect.this, "1.Failed to add preset!", Toast.LENGTH_SHORT).show();
        }
    }
    public void goToCP(View v) {
        Intent intent = new Intent(this, CreatePreset.class);
        startActivityForResult(intent, START_CREATE_PRESET);
    }
}