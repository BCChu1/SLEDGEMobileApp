package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bluetoothCheck(View view) {
        TextView textView = findViewById(R.id.textView);
        textView.setText("Hello! Test. Nathan has entered the chat  .");
    }

    public void nathanButton(View view) {
        TextView textView = findViewById(R.id.textView);
        textView.setText("Nathan's button found.");
    }

}