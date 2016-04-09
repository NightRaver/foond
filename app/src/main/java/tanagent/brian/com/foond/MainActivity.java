package tanagent.brian.com.foond;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private List<S3ObjectSummary> s3ObjList;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private AmazonS3 s3;
    private ImageView restaurantImage;
    private ImageView restaurantRating;
    private TextView restaurantName;
    private TextView restaurantAddress;
    private TextView restaurantCity;
    private TextView restaurantState;
    private TextView restaurantZip;
    private Button navButton;

    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMapRef;
    private double latitude;
    private double longitude;

    private static final int cameraZoom = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restaurantImage = (ImageView) findViewById(R.id.restaurant_image);
        restaurantRating = (ImageView) findViewById(R.id.restaurant_rating);
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
                Picasso.with(MainActivity.this).load(data.get("img")).into(restaurantImage);
                Picasso.with(MainActivity.this).load(data.get("rating")).into(restaurantRating);
                restaurantName.setText(data.get("name"));
                restaurantAddress.setText(data.get("address") + " ");
                restaurantCity.setText(data.get("city") + " ");
                restaurantState.setText(data.get("state") + ", ");
                restaurantZip.setText(data.get("zip"));

                Geocoder coder = new Geocoder(MainActivity.this);
                List<Address> address;

                try {
                    address = coder.getFromLocationName(data.get("address") + ", " + data.get("state"), 5);
                    Address location = address.get(0);
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    mapActivity(data.get("name"), location.getLatitude(), location.getLongitude());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        task.execute();

        navButton = (Button) findViewById(R.id.navButton);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?saddr=" + "20.344" + "," + "34.34" + "&daddr=" + latitude + "," + longitude));
//                startActivity(intent);

                Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:" + latitude + "," + longitude));
                i.setClassName("com.google.android.apps.maps",
                        "com.google.android.maps.MapsActivity");
                startActivity(i);
            }
        });
    }

    public void mapActivity(final String name, final double latitude, final double longitude) {
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMapRef = googleMap;

                googleMapRef.addMarker(new MarkerOptions().title(name).position(new LatLng(latitude, longitude)));

                googleMapRef.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), cameraZoom));
            }
        });
    }
}
