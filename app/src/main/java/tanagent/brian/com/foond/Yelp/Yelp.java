package tanagent.brian.com.foond.Yelp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;
import tanagent.brian.com.foond.R;

/**
 * Created by Brian on 2/10/2016.
 */
public class Yelp extends Activity {
    private static final String CONSUMER_KEY = "uF70iaE55RtJ5aOCFPRXXQ";
    private static final String CONSUMER_SECRET = "pk3brkYNIwPSWdKP-YwRnTk7Y9M";
    private static final String TOKEN = "gLcpwL-ZNE3zIXGSJGoh4WI_ZfvgMb9Q";
    private static final String TOKEN_SECRET = "peiZEgUYyCGkpVStBbLOg3PMCiM";

    private YelpAPI yelpAPI;
    private static final int numOfBusinesses = 10;
    private static final double radiusFilter = 2500; // 1.5 miles (2500 meters)
    private static final int sort = 1;
    private static final String category_filter = "food";
    private static final String term = "restaurants";

    private String restaurantName;
    private String restaurantAddress;
    private String restaurantImage;
    private String restaurantCity;
    private String restaurantState;
    private String restaurantZip;
    private String restaurantURL;
    private String restaurantPhone;
    private String restaurantRating;
    private boolean restaurantAvailability;

    private List<YelpDetails> yelpList = new ArrayList<YelpDetails>();

    private ListView listView;
    private YelpAdapter yelpAdapter;
    private ProgressBar progressBar;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitude;
    private double longitude;

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            // Called when the location is updated
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                new ProgressTask().execute();
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {

            // This library uses a YelpAPI object to query against the API
            YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                    CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);

            yelpAPI = yelpAPIFactory.createAPI();

            final Map<String, String> params = new HashMap<>();

            // general params
            params.put("term", term);
            params.put("limit", String.valueOf(numOfBusinesses));
            params.put("radius_filter", String.valueOf(radiusFilter));
            params.put("sort", String.valueOf(sort));
//            params.put("category_filter", category_filter);

            // locale params
            params.put("lang", "en");

            // Execute the Call object to send the request
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(Yelp.this, Locale.getDefault());

            // translates latitude and longitude to an address
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                address = addresses.get(0).getAddressLine(0);
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                country = addresses.get(0).getCountryName();
                postalCode = addresses.get(0).getPostalCode();
                knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.i("GeoCoder", addresses.get(0).getAddressLine(0).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            CoordinateOptions coordinate = CoordinateOptions.builder()
                    .latitude(latitude)
                    .longitude(longitude).build();

            Call<SearchResponse> call = yelpAPI.search(coordinate, params);

            // Pass in a Callback object to send request asynchronously
            Callback<SearchResponse> callback = new Callback<SearchResponse>() {
                @Override
                public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                    SearchResponse searchResponse = response.body();

                    for (int i = 0; i < numOfBusinesses; i++) {
                        restaurantName = searchResponse.businesses().get(i).name();
                        restaurantAddress = searchResponse.businesses().get(i).location().displayAddress().get(0);
                        restaurantCity = searchResponse.businesses().get(i).location().city();
                        restaurantState = searchResponse.businesses().get(i).location().stateCode();
                        restaurantZip = searchResponse.businesses().get(i).location().postalCode();
                        restaurantImage = searchResponse.businesses().get(i).imageUrl();
                        restaurantURL = searchResponse.businesses().get(i).mobileUrl();
                        restaurantPhone = searchResponse.businesses().get(i).displayPhone();
                        restaurantRating = searchResponse.businesses().get(i).ratingImgUrlSmall();
                        restaurantAvailability = searchResponse.businesses().get(i).isClosed();

                        yelpList.add(new YelpDetails(restaurantImage, restaurantName,
                                restaurantAddress, restaurantCity, restaurantState, restaurantZip,
                                restaurantURL, restaurantPhone, restaurantRating, restaurantAvailability));
                    }

                    setProgressBarIndeterminateVisibility(false);

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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
