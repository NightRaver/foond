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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.Locale;
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
    private static final int numOfBusinesses = 15;
    private static final double radiusFilter = 40000; // 25 miles (4000 meters)
    private static final int sort = 1;

    private String restaurantName;
    private String restaurantAddress;
    private String restaurantImage;

    private List<YelpDetails> yelpList = new ArrayList<YelpDetails>();

    private ListView listView;
    private YelpAdapter yelpAdapter;

    private Location lastLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitude;
    private double longitude;
    private String currentLocation;

    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String knownName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.yelp);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            // Called when the location is updated
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.i("Latitude:", String.valueOf(location.getLatitude()));
                Log.i("Longitude:", String.valueOf(location.getLongitude()));
                setUp();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            // Checks if the GPS is turned off
            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
                startActivity(intent);
            }
        };

//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Requested permissions for ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, AND INTERNET
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);
            }
            return;
        } else {
            configureButton();
        }

    }

    @SuppressWarnings("ResourceType")
    private void configureButton() {
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
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
        params.put("radius_filter", String.valueOf(radiusFilter));
        params.put("sort", String.valueOf(sort));

        // locale params
        params.put("lang", "en");

        // Execute the Call object to send the request
        Log.i("Latitude1:", String.valueOf(latitude));
        Log.i("Longitude1:", String.valueOf(longitude));
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
            Log.i("GeoCoder", addresses.get(0).getAddressLine(0).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("City", city);
        Log.i("State", state);
        Call<SearchResponse> call = yelpAPI.search(city + ", " + state, params);

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
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 3, locationListener);
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }
}
