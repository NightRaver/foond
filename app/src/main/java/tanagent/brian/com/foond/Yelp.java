package tanagent.brian.com.foond;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import junit.framework.Assert;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Brian on 2/10/2016.
 */
public class Yelp extends Activity {
    private static final String CONSUMER_KEY = "uF70iaE55RtJ5aOCFPRXXQ";
    private static final String CONSUMER_SECRET = "pk3brkYNIwPSWdKP-YwRnTk7Y9M";
    private static final String TOKEN = "gLcpwL-ZNE3zIXGSJGoh4WI_ZfvgMb9Q";
    private static final String TOKEN_SECRET = "peiZEgUYyCGkpVStBbLOg3PMCiM";

    private YelpAPI yelpAPI;
    //    private GPSTracker gps;
    private static final int numOfBusinesses = 6;

    private String restaurantName;
    private String restaurantAddress;
    private String restaurantImage;

    private List<YelpDetails> yelpList = new ArrayList<YelpDetails>();

    private ListView listView;
    private YelpAdapter yelpAdapter;

    private Location lastLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

//    private GoogleApiClient mGoogleApiClient;
    private String latitude;
    private String longitude;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.yelp);
        setUp();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;
                Log.i("Latitude: ", String.valueOf(location.getLatitude()));
                Log.i("Longitude: ", String.valueOf(location.getLongitude()));

                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Log.i("Latitude2: ", latitude + "");
        Log.i("Longitude2: ", longitude + "");

//        buildGoogleApiClient();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            // request permissions
            //fine location
            //coarse location


            Log.i("TEST", "PERMISSION NOT GRANTED");
            return;
        }
        Log.i("TEST", "ABOUT TO REQUEST UPDATES");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

//        gps = new GPSTracker(this);

//        if(gps.canGetLocation()) {
//            double latitude = gps.getLatitude();
//            double longitude = gps.getLongitude();
//
//            Log.i("LOCATION", "Your Location is -\nLat: " + latitude + "\nLong: " + longitude);
//
////            Toast.makeText(
////                    getApplicationContext(),
////                    "Your Location is -\nLat: " + latitude + "\nLong: "
////                            + longitude, Toast.LENGTH_LONG).show();
//        } else {
//            gps.showSettingsAlert();
//        }


    }

    public void setUp() {
        // This library uses a YelpAPI object to query against the API
        YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);

        yelpAPI = yelpAPIFactory.createAPI();

        final Map<String, String> params = new HashMap<>();

        // general params
        params.put("term", "food");
        params.put("limit", String.valueOf(numOfBusinesses));

        // locale params
        params.put("lang", "fr");

        // Execute the Call object to send the request
        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

        // Pass in a Callback object to send request asynchronously
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();

                for (int i = 0; i < numOfBusinesses; i++) {
                    restaurantName = searchResponse.businesses().get(i).name();
                    restaurantAddress = searchResponse.businesses().get(i).location().postalCode();
                    restaurantImage = searchResponse.businesses().get(i).imageUrl();

                    yelpList.add(new YelpDetails(restaurantImage, restaurantName, restaurantAddress));
                }

                listView = (ListView) findViewById(R.id.yelp_results);
                yelpAdapter = new YelpAdapter(Yelp.this, R.layout.yelp_item, yelpList);
                listView.setAdapter(yelpAdapter);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("TEST", "Failed", t);
            }
        };
        call.enqueue(callback);
    }

//    protected synchronized void buildGoogleApiClient() {
//
//
//        if (mGoogleApiClient == null) {
//            mGoogleApiClient = new GoogleApiClient.Builder(this)
//                    .addConnectionCallbacks(this)
//                    .addOnConnectionFailedListener(this)
//                    .addApi(LocationServices.API)
//                    .build();
//        }
//    }

    @Test
    public void testSearchByLocation() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("term", "yelp");

        Call<SearchResponse> call = yelpAPI.search("37.77493,-122.419415", params);
        Response<SearchResponse> response = call.execute();
        Assert.assertEquals(200, response.code());

        SearchResponse searchResponse = response.body();
        Assert.assertNotNull(searchResponse);
    }

    @Test
    public void testSearchByLocationWithOptionalCoordinate() throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("term", "yelp");
        params.put("cll", "37.7867703362929,-122.399958372115");

        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
        Response<SearchResponse> response = call.execute();
        Assert.assertEquals(200, response.code());

        SearchResponse searchResponse = response.body();
        Assert.assertNotNull(searchResponse);
    }


    @Test
    public void testSearchByCoordinateOptions() throws IOException {
        CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(37.7867703362929)
                .longitude(-122.399958372115).build();

        Map<String, String> params = new HashMap<>();
        params.put("term", "yelp");

        Call<SearchResponse> call = yelpAPI.search(coordinate, params);
        Response<SearchResponse> response = call.execute();
        Assert.assertEquals(200, response.code());

        SearchResponse searchResponse = response.body();
        Assert.assertNotNull(searchResponse);
    }

    @Test
    public void testSearchByBoundingBoxOptions() throws IOException {
        BoundingBoxOptions bounds = BoundingBoxOptions.builder()
                .swLatitude(37.900000)
                .swLongitude(-122.500000)
                .neLatitude(37.788022)
                .neLongitude(-122.399797)
                .build();

        Map<String, String> params = new HashMap<>();
        params.put("term", "yelp");

        Call<SearchResponse> call = yelpAPI.search(bounds, params);
        Response<SearchResponse> response = call.execute();
        Assert.assertEquals(200, response.code());

        SearchResponse searchResponse = response.body();
        Assert.assertNotNull(searchResponse);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, locationListener);
    }

//    @Override
//    public void onConnected(Bundle bundle) {
//        Log.i("CONNECTED", "I'm connected");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            // Check Permissions Now
//
//            return;
//        }
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        if (mLastLocation != null) {
//            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//            Log.i("Longitude", "");
//            Log.i("Latitude", "");
//    }

//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.e("FAILED", connectionResult.toString());
//    }
}
