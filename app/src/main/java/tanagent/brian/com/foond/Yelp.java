package tanagent.brian.com.foond;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.BoundingBoxOptions;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Converter;
import retrofit.Response;
import retrofit.Retrofit;
import retrofit.http.GET;
import tanagent.brian.com.foond.YelpParseClasses.Search;
import tanagent.brian.com.foond.YelpParseClasses.YelpV2API;

/**
 * Created by Brian on 2/10/2016.
 */
public class Yelp extends Activity {
    private static final String CONSUMER_KEY = "uF70iaE55RtJ5aOCFPRXXQ";
    private static final String CONSUMER_SECRET = "pk3brkYNIwPSWdKP-YwRnTk7Y9M";
    private static final String TOKEN = "gLcpwL-ZNE3zIXGSJGoh4WI_ZfvgMb9Q";
    private static final String TOKEN_SECRET = "peiZEgUYyCGkpVStBbLOg3PMCiM";

    private YelpAPI yelpAPI;
    private Response<SearchResponse> response;
    private Callback<SearchResponse> callback;

    Search places;

    private Business business,loadedBusiness, favoritedBusiness;
    private String name;
    private String address;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String rating;
    private String url;
    private String mobileUrl;
    private String ratingUrl;
    private String snippet;
    private String locationInfo;
    private double latitude, longitude;

    // filters
    private String location;
    private String category;
    private double distance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUp();


    }


    @Before
    public void setUp() {
        // This library uses a YelpAPI object to query against the API
        YelpAPIFactory yelpAPIFactory = new YelpAPIFactory(
                CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);

        // Make API requests to be executed in main thread so we can verify it easily.
//        yelpAPIFactory = AsyncTestUtils.setToRunInMainThread(yelpAPIFactory);

        yelpAPI = yelpAPIFactory.createAPI();

        final Map<String, String> params = new HashMap<>();

        // general params
        params.put("term", "food");
        params.put("limit", "3");

        // locale params
        params.put("lang", "fr");

        // Execute the Call object to send the request
        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);
        try {
            Response<SearchResponse> response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Pass in a Callback object to send request asynchronously
        Callback<SearchResponse> callback = new Callback<SearchResponse>() {
            @Override
            public void onResponse(Response<SearchResponse> response, Retrofit retrofit) {
                SearchResponse searchResponse = response.body();

            }

            @Override
            public void onFailure(Throwable t) {

            }
        };

        call.enqueue(callback);
    }

    public void search() {
        OAuthService service = new ServiceBuilder().provider(YelpV2API.class).
                apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET).build();
        Token accessToken = new Token(TOKEN, TOKEN_SECRET);
        OAuthRequest request = (OAuthRequest) new OAuthRequest(Verb.GET,
                "http://api.yelp.com/v2/search");

        request.addQuerystringParameter("location", location);
        request.addQuerystringParameter("category_filter", category);
        if(distance != 0)
            request.addQuerystringParameter("radius_filter", String.valueOf(distance));

        request.addQuerystringParameter("sort", "1");

        request.addQuerystringParameter("offset", "20");

        request.addQuerystringParameter("limit", "20");

        service.signRequest(accessToken, request);
        org.scribe.model.Response response = request.send();
        String rawData = response.getBody();

        try {
            JSONObject json = new JSONObject(rawData);
            JSONArray businesses;

        } catch (JSONException e) {
            e.printStackTrace();
        }


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
