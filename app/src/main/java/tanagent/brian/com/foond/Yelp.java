package tanagent.brian.com.foond;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import junit.framework.Assert;

import org.junit.Test;

import java.io.IOException;
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

    private List<YelpDetails> yelpList = new ArrayList<YelpDetails>() {{
        add(new YelpDetails(R.drawable.sample_0, "Fuji", "Huntington Beach"));
        add(new YelpDetails(android.R.drawable.alert_dark_frame, "Fuji", "Huntington Beach"));
        add(new YelpDetails(android.R.drawable.alert_dark_frame, "Fuji", "Huntington Beach"));
        add(new YelpDetails(android.R.drawable.alert_dark_frame, "Fuji", "Huntington Beach"));
        add(new YelpDetails(android.R.drawable.alert_dark_frame, "Fuji", "Huntington Beach"));
    }};

    private ListView listView;
    private YelpAdapter yelpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.yelp);

        //Next task is to find out how to import the images

        //example
//        listView = (ListView) findViewById(R.id.yelp_results);
//        String[] colors = {"red", "orange", "yellow", "green", "blue", "purple"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, colors);
//        listView.setAdapter(adapter);

        listView = (ListView) findViewById(R.id.yelp_results);
        yelpAdapter = new YelpAdapter(this, R.layout.yelp_item, yelpList);
        listView.setAdapter(yelpAdapter);

        setUp();
    }

    public void setUp() {
        // This library uses a YelpAPI object to query against the API
        YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);

        yelpAPI = yelpAPIFactory.createAPI();

        final Map<String, String> params = new HashMap<>();

        // general params
        params.put("term", "food");
        params.put("limit", "6");

        // locale params
        params.put("lang", "fr");

        // Execute the Call object to send the request
        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

        // Pass in a Callback object to send request asynchronously
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
                Log.i("TEST", searchResponse.businesses().toString());
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

        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
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
}
