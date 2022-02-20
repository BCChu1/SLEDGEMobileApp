package com.example.sledgemobileapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import top.defaults.colorpicker.ColorPickerPopup;

public class ColorPicker extends AppCompatActivity {

    // text view variable to set the color for GFG text
    private TextView gfgTextView;

    private TextView hexColorTextView;
    private TextView hueColorTextView;

    // two buttons to open color picker dialog and one to
    // set the color for GFG text
    private Button mSetColorButton, mPickColorButton;

    // view box to preview the selected color
    private View mColorPreview_hue;
    private View mColorPreview_hex;

    // this is the default color of the preview box
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_picker);

        // register the GFG text with appropriate ID
        //gfgTextView = findViewById(R.id.gfg_heading);

        // register the GFG text with appropriate ID
        hexColorTextView = findViewById(R.id.hexColorText);
        hexColorTextView.setTextColor(Color.BLACK);

        hueColorTextView = findViewById(R.id.hueColorText);
        hueColorTextView.setTextColor(Color.BLACK);

        // register two of the buttons with their
        // appropriate IDs
        mPickColorButton = findViewById(R.id.pick_color_button);
        mSetColorButton = findViewById(R.id.set_color_button);

        // and also register the view which shows the
        // preview of the color chosen by the user
        mColorPreview_hex = findViewById(R.id.preview_selected_color_hex);
        mColorPreview_hue = findViewById(R.id.preview_selected_color_hue);

        // set the default color to 0 as it is black
        mDefaultColor = 0;

        // handling the Pick Color Button to open color
        // picker dialog
        mPickColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new ColorPickerPopup.Builder(ColorPicker.this).initialColor(
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
                                        v,
                                        new ColorPickerPopup.ColorPickerObserver() {
                                            @Override
                                            public void
                                            onColorPicked(int color) {
                                                // set the color
                                                // which is returned
                                                // by the color
                                                // picker
                                                mDefaultColor = color;

                                                // now as soon as
                                                // the dialog closes
                                                // set the preview
                                                // box to returned
                                                // color
                                                mColorPreview_hex.setBackgroundColor(mDefaultColor);


                                                String hexColor = "#" + Integer.toHexString(mDefaultColor);

                                                hexColorTextView.setText(hexColor);


                                                float[] hsv = new float[3];
                                                int[] currentRGB;
                                                currentRGB = getRGB(color);

                                                //int R = (color >> 16) & 0xff;
                                                //int G = (color >> 8) & 0xff;
                                                //int B = (color) & 0xff;

                                                Color.colorToHSV(color, hsv);
                                                hueColorTextView.setText("HSV: " + hsv[0] + ", " + hsv[1] + ", " + hsv[2] + "\n" + "RGB:" + currentRGB[0] + ", " + currentRGB[1] + ", " + currentRGB[2]);

                                                mColorPreview_hue.setBackgroundColor(Color.HSVToColor(hsv));

                                            }
                                        });
                    }
                });

        // handling the Set Color button to set the selected
        // color for the GFG text.
        mSetColorButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // now change the value of the GFG text
                        // as well.
                        //gfgTextView.setBackgroundColor(mDefaultColor);


                    }
                });
    }

    public static int[] getRGB(final int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return new int[] {r, g, b};
    }


}