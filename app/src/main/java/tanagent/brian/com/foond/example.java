package tanagent.brian.com.foond;

import android.app.Activity;
import android.os.Bundle;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Brian on 2/10/2016.
 */
public class example extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.example);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.yelp.com/v2/search?term=food&location=San+Francisco")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
}
