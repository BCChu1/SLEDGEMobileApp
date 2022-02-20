package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.sledgemobileapp.SingleAvailableList;
import com.example.sledgemobileapp.SingleLoadedList;
import com.example.sledgemobileapp.animationInfo;

import java.util.ArrayList;

import top.defaults.colorpicker.ColorPickerPopup;

public class AdvancedCustomization extends AppCompatActivity {

    ListView listView;
    ListView editListView;
    SingleLoadedList loadedList;
    SingleAvailableList availableList;
    ArrayList<String> variableNames = new ArrayList<>();
    ArrayAdapter<String> editAdapter;
    animationInfo current_animation = null;
    int input_index = 0;
    int temp_color = 0;
    String hexColor = "#";
    TextView user_input_color;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_customization);

        listView = findViewById(R.id.selectableListy);
        editListView = findViewById((R.id.editListy));

        loadedList = SingleLoadedList.getInstance();
        availableList = SingleAvailableList.getInstance();

        user_input_color = findViewById(R.id.variableNameText);
        user_input_color.setTextColor(Color.BLACK);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, availableList.animationNames);
        editAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, variableNames);
        listView.setAdapter(adapter);
        editListView.setAdapter(editAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                View text = findViewById(R.id.variableNameText);
                TextView textview = (TextView) text;
                textview.setText(" ");
                beginEdit(i);
                input_index = 0;
                text = findViewById(R.id.variableNameText);
                textview = (TextView) text;
                textview.setText(current_animation.input_names[0]);
            }
        });

        editListView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                input_index = i;
                View text = findViewById(R.id.variableNameText);
                TextView textview = (TextView) text;
                textview.setText(current_animation.input_names[i]);

                if(current_animation.input_names[i].contains("Color")) {
                    new ColorPickerPopup.Builder(AdvancedCustomization.this).initialColor(
                            Color.RED) // set initial color
                            // of the color
                            // picker dialog
                            .enableBrightness(
                                    true) // enable color brightness
                            // slider or not
                            .enableAlpha(
                                    true) // enable color alpha
                            // changer on slider or
                            // not
                            .okTitle(
                                    "CHOOSE") // this is top right
                            // Choose button
                            .cancelTitle(
                                    "CANCEL") // this is top left
                            // Cancel button which
                            // closes the
                            .showIndicator(
                                    true) // this is the small box
                            // which shows the chosen
                            // color by user at the
                            // bottom of the cancel
                            // button
                            .showValue(
                                    true) // this is the value which
                            // shows the selected
                            // color hex code
                            // the above all values can be made
                            // false to disable them on the
                            // color picker dialog.
                            .build()
                            .show(
                                    view,
                                    new ColorPickerPopup.ColorPickerObserver() {
                                        @Override
                                        public void
                                        onColorPicked(int color) {
                                            // set the color
                                            // which is returned
                                            // by the color
                                            // picker
                                            temp_color = color;
                                            hexColor = "#" + Integer.toHexString(temp_color);

                                            current_animation.input_values[i] = temp_color;
                                            variableNames.clear();
                                            for (int x = 0; x < current_animation.input_names.length; x++) {
                                                variableNames.add(current_animation.input_names[x] + " : " + current_animation.input_values[x]);
                                            }
                                            editAdapter.notifyDataSetChanged();

                                        }
                                    });

                }
            }
        }));
    }

    public void beginEdit(int index) {
        int num_inputs = availableList.animations.get(index).num_inputs;
        current_animation = availableList.animations.get(index);
        variableNames.clear();
        View text = findViewById(R.id.animationNameText);
        TextView textview = (TextView) text;
        textview.setText(current_animation.name);
        for (int x = 0; x < num_inputs; x++) {
            variableNames.add(current_animation.input_names[x] + " : " + current_animation.input_values[x]);
        }
        editAdapter.notifyDataSetChanged();

    }

    public void setVariable(View view) {

        if (current_animation == null) {
            Toast.makeText(AdvancedCustomization.this, "Select an animation", Toast.LENGTH_SHORT).show();
            return;
        }

        int num_inputs = current_animation.num_inputs;
        EditText editText = (EditText) findViewById(R.id.editTextTextVariableValue);
        String message = editText.getText().toString();
        if (message.matches("")) {
            Toast.makeText(AdvancedCustomization.this, "Not available", Toast.LENGTH_SHORT).show();
            return;
        }
        int value = Integer.parseInt(message);
        current_animation.input_values[input_index] = value;
        variableNames.clear();
        for (int x = 0; x < num_inputs; x++) {
            variableNames.add(current_animation.input_names[x] + " : " + current_animation.input_values[x]);
        }
        editAdapter.notifyDataSetChanged();
    }

    public void addAnimation(View view) {

        //copy data
        String inputString = current_animation.inputString;
        String name = current_animation.name;
        int id = current_animation.id;
        animationInfo new_animation = new animationInfo(id, name, inputString);
        for (int x = 0; x < current_animation.num_inputs; x++) {
            new_animation.input_values[x] = current_animation.input_values[x];
        }
        //append new

        //SingleLoadedList.append(new_animation);
        CreatePreset.addAnimationToPreset(new_animation);       //adding animation to this new preset


        Toast.makeText(AdvancedCustomization.this, name + "Animation Added", Toast.LENGTH_SHORT).show();
    }
}