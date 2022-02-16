package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class CreatePreset extends AppCompatActivity {
    public static PresetClass newPreset = new PresetClass();     //used to transfer data between Advanced Customization and Preset Select
    ListView animationList;                 //animation list view
    EditText presetNameTextView;            //the text box that can be edited by user
    private  static ArrayList<String> currentAnimationNames = new ArrayList<>();        //list of names to use with adapter
    public static ArrayAdapter<String> CreatePresetAdapter; //adapter for currentAnimationNames
    SingleLoadedList loadedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_preset);
        animationList = findViewById(R.id.PresetAnimationListView);
        presetNameTextView = findViewById(R.id.EditTextPresetName);
        loadedList = SingleLoadedList.getInstance();
        CreatePresetAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, SingleLoadedList.animationNames);

        animationList.setAdapter(CreatePresetAdapter);


    }
    public static void addAnimationToPreset(animationInfo AI) {
        //newPreset.animations.add(AI);               //add animation object to new preset object
        //newPreset.animationNames.add(AI.name);      //add name of animation to new preset object
        SingleLoadedList.append(AI);
        //currentAnimationNames.add(AI.name);         //add to array for listview
        //CreatePresetAdapter.notifyDataSetChanged(); //updating listview with new animation
    }

    public void onResume() {
        super.onResume();
        CreatePresetAdapter.notifyDataSetChanged();
    }

    public void goToAC(View v) {
        Intent intent = new Intent(this, AdvancedCustomization.class);
        startActivity(intent);
    }

    public void SaveAndReturnToPS(View v) {         //returning to Preset Select
        String presetName = presetNameTextView.getText().toString();
        //newPreset.name = presetNameTextView.getText().toString();       //getting the presets name as we are saving
        if (presetName.equals("")) {
            Toast.makeText(CreatePreset.this, "Preset Name can not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        SingleLoadedList.newPresetName = presetName;
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}