package com.tanagent.android.brian.foond;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Brian on 1/29/2016.
 */
public class Distance extends Activity {

    private SeekBar seekBar;
    private TextView currentValueTextView;
    private TextView distance;

    private Button imperialButton;
    private Button metricButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.distance);

        seekBar = (SeekBar) findViewById(R.id.seekbar);
        currentValueTextView = (TextView) findViewById(R.id.seekBarValueTextView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentValueTextView.setText(Integer.toString(progress) + " ");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imperialButton = (Button) findViewById(R.id.imperial);
        distance = (TextView) findViewById(R.id.distance);
        imperialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance.setText("miles");
            }

        });

        metricButton = (Button) findViewById(R.id.metric);
        metricButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance.setText("km");
            }
        });
    }
}
