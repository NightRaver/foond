package tanagent.brian.com.foond;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.GridView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 1/30/2016.
 */
public class Main extends AppCompatActivity {

    private GridView gridView;
    private List<S3ObjectSummary> s3ObjList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private List<Food> foods = new ArrayList<Food>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main);

        AsyncTask<Void, Void, List<Food>> task = new AsyncTask<Void, Void, List<Food>>() {
            @Override
            protected List<Food> doInBackground(Void... params) {
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

                for (S3ObjectSummary summary : s3ObjList) {
                    foods.add(new Food("https://s3.amazonaws.com/" + summary.getBucketName() + "/" + summary.getKey()));
                }

                return foods;
            }

            @Override
            protected void onPostExecute(List<Food> foods) {
                // Calls the grid view.
                gridView = (GridView) findViewById(R.id.grid_view);
                gridView.setAdapter(new ImageAdapter(Main.this, foods));
            }
        };

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload:
                Intent upload = new Intent(this, Upload.class);
                startActivity(upload);
                break;

            // Uncomment this to add features
            // Remember to uncomment the codes in menu.xml

            /*
            case R.id.distance:
                Intent distance = new Intent(this, Distance.class);
                startActivity(distance);
                break;
            case R.id.account:
                Intent account = new Intent(this, Account.class);
                startActivity(account);
                break;
             */
        }

        return super.onOptionsItemSelected(item);
    }
}
