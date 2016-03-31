package tanagent.brian.com.foond;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<S3ObjectSummary> s3ObjList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView restaurantCity;
    private TextView restaurantState;
    private TextView restaurantZip;
    private Button navButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantName = (TextView) findViewById(R.id.restaurant_name);
        restaurantAddress = (TextView) findViewById(R.id.restaurant_address);
        restaurantCity = (TextView) findViewById(R.id.restaurant_city);
        restaurantState = (TextView) findViewById(R.id.restaurant_state);
        restaurantZip = (TextView) findViewById(R.id.restaurant_zip);

        AsyncTask<Void, Void, Map<String, String>> task = new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... params) {
                credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        Constants.COGNITO_POOL_ID, // Identity Pool ID
                        Regions.US_EAST_1 // Region
                );

                // Instantiate an S3 Client
                // Create an S3 client
                s3 = new AmazonS3Client(credentialsProvider);

                // Set the region of your s3 bucket
                s3.setRegion(Region.getRegion(Regions.US_EAST_1));

                s3ObjList = s3.listObjects(Constants.BUCKET_NAME).getObjectSummaries();

                // get the image name
                ObjectMetadata metadata = s3.getObjectMetadata(Constants.BUCKET_NAME, getIntent().getExtras().getString("key"));

//                // This is the one I need to use getUserMetaData
                Log.i("Object Meta Data: ", metadata.getUserMetadata().toString());

//                // you can use this to iterate through keysets
                Log.i("name", String.valueOf(metadata.getUserMetadata().keySet()));

//                // you can use this to get the value
                Log.i("whatever", metadata.getUserMetadata().get("name"));

                return metadata.getUserMetadata();
            }

            @Override
            protected void onPostExecute(Map<String, String> data) {
                restaurantName.setText(data.get("name"));
                restaurantAddress.setText(data.get("address"));
                restaurantCity.setText(data.get("city"));
                restaurantState.setText(data.get("state"));
                restaurantZip.setText(data.get("zip"));
            }
        };

        task.execute();

        navButton = (Button) findViewById(R.id.navButton);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GMap.class);
                startActivity(intent);
                Log.i("key", getIntent().getExtras().getString("key"));
            }
        });
    }
}
