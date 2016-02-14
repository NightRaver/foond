package com.tanagent.android.brian.foond;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Brian on 1/31/2016.
 */
public class Upload extends Activity{

    private Button submitButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.upload);

        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Upload.this, "The photo has been uploaded", Toast.LENGTH_LONG).show();
            }
        });
    }
}
