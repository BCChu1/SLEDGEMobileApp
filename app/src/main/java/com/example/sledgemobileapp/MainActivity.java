package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public void goToCP (View view) {                    //function to go to the Color Picker screen, added to onClick attr for button
        Intent intent = new Intent(this, ColorPicker.class);
        startActivity(intent);
    }

    public void goToAS (View view) {                    //function to go to Anim Select screen, added to onClick attr for button
        Intent intent = new Intent(this, PresetSelect.class);
        startActivity(intent);
    }

    public void goToAC(View view) {                     //function to go to Advanced Customization screen, added to onClick attr for button
        Intent intent = new Intent(this, AdvancedCustomization.class);
        startActivity(intent);
    }
    public void goToBC(View view) {
        Intent intent = new Intent(this, Bluetooth_connection.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bluetoothCheck(View view) {
        TextView textView = findViewById(R.id.SLEDGETextView);
        textView.setText("Hello! Test. Nathan has entered the chat  .");
    }

    public void nathanButton(View view) {
        TextView textView = findViewById(R.id.SLEDGETextView);
        textView.setText("Nathan's button found.");
    }
}