package tanagent.brian.com.foond;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Brian on 1/31/2016.
 */
public class Select extends Activity{

    private ImageView foodImage;
    private ImageView starButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.select);

        foodImage = (ImageView) findViewById(R.id.food_image);
        Picasso.with(this).load(getIntent().getExtras().getString("url")).into(foodImage);

        starButton = (ImageView) findViewById(R.id.star_button);
        starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Select.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
