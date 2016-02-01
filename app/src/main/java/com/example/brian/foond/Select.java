package com.example.brian.foond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Brian on 1/31/2016.
 */
public class Select extends Activity{

    private ImageButton starButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.select);

        starButton = (ImageButton) findViewById(R.id.star_button);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
