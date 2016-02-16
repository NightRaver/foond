package com.tanagent.android.brian.foond;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Brian on 2/10/2016.
 */
public class Yelp extends Activity {
    private YelpAPI yelpAPI;
    private Response<SearchResponse> response;
    private Callback<SearchResponse> callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                "uF70iaE55RtJ5aOCFPRXXQ",
                "pk3brkYNIwPSWdKP-YwRnTk7Y9M",
                "gLcpwL-ZNE3zIXGSJGoh4WI_ZfvgMb9Q",
                "peiZEgUYyCGkpVStBbLOg3PMCiM"
        );

        // Make API requests to be executed in main thread so we can verify it easily.
//        yelpAPIFactory = AsyncTestUtils.setToRunInMainThread(yelpAPIFactory);

        yelpAPI = yelpAPIFactory.createAPI();

        Map<String, String> params = new HashMap<>();
        // general params
        params.put("term", "food");
        params.put("limit", "3");

        // locale params
        params.put("lang", "fr");

        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG: ", "Something happened");
        }

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();
//                Log.i("TAG: ", "Is this the response?" + searchResponse);
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

//        callback = new Callback<SearchResponse>() {
//            @Override
//            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
//                Log.i("TAG", String.valueOf(response.body()));
//
//
//                if (response.isSuccess())
//                    Log.i("TAG", "I hope this works");
//                else
//                    Log.i("TAG", "I hope this works");
//
//                Log.i("TAG", "I hope this works");
//
//                SearchResponse searchResponse = response.body();
//                // Update UI text with the searchResponse.
//
//                Log.i("TAG", "I hope this works");
//            }
//            @Override
//            public void onFailure(Throwable t) {
//                // HTTP error happened, do something to handle it.
//                Log.e("TAG", "It didn't work", t);
//            }
//        };


    }


    @Before
    public void setUp() {
        YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                "uF70iaE55RtJ5aOCFPRXXQ",
                "pk3brkYNIwPSWdKP-YwRnTk7Y9M",
                "gLcpwL-ZNE3zIXGSJGoh4WI_ZfvgMb9Q",
                "peiZEgUYyCGkpVStBbLOg3PMCiM"
        );

        // Make API requests to be executed in main thread so we can verify it easily.
//        yelpAPIFactory = AsyncTestUtils.setToRunInMainThread(yelpAPIFactory);

        yelpAPI = yelpAPIFactory.createAPI();
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
