package com.example.sledgemobileapp;

import java.io.Serializable;
import java.util.ArrayList;

public class SingleLoadedList implements Serializable {
    public static ArrayList<animationInfo> animations = new ArrayList<>();
    public static ArrayList<String> animationNames = new ArrayList<>();

    private static SingleLoadedList instance = new SingleLoadedList();

    public static String newPresetName = "";

    private SingleLoadedList(){}

    public static SingleLoadedList getInstance() {
        return instance;
    }

    public static void append(animationInfo new_animation) {
        animations.add(new_animation);
        animationNames.add(new_animation.name);
    }

    public void remove(int index) {
        animations.remove(index);
        animationNames.remove(index);
    }
    public static void clearAll() {
        animations.clear();
        animationNames.clear();
        newPresetName = "";
    }
}
