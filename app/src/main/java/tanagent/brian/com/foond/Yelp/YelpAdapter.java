package tanagent.brian.com.foond.Yelp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import tanagent.brian.com.foond.R;
import tanagent.brian.com.foond.Upload;

/**
 * Created by Brian on 2/24/2016.
 */
public class YelpAdapter extends ArrayAdapter<YelpDetails> {

    private Context context;
    private Picasso yelpImage;
    private List<YelpDetails> yelpDetailsList;

    public YelpAdapter(Context context, int resource, List<YelpDetails> objects) {
        super(context, resource, objects);
        this.context = context;
        this.yelpDetailsList = objects;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.yelp_item, parent, false);

        final YelpDetails yelpDetails = yelpDetailsList.get(position);

        TextView restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
        restaurantName.setText(yelpDetails.getRestaurantName());

        TextView restaurantAddress = (TextView) view.findViewById(R.id.restaurant_address);
        restaurantAddress.setText(yelpDetails.getRestaurantAddress());

        TextView restaurantCity = (TextView) view.findViewById(R.id.restaurant_city);
        restaurantCity.setText(yelpDetails.getRestaurantCity());

        ImageView restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
        yelpImage.with(this.context).load(yelpDetails.getRestaurantImage()).into(restaurantImage);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.putExtra("restaurantName", yelpDetails.getRestaurantName());
                intent.putExtra("restaurantAddress", yelpDetails.getRestaurantAddress());
                intent.putExtra("restaurantCity", yelpDetails.getRestaurantCity());
                intent.putExtra("restaurantState", yelpDetails.getRestaurantState());
                intent.putExtra("restaurantZip", yelpDetails.getRestaurantZip());
                intent.putExtra("restaurantImg", yelpDetails.getRestaurantImage());
                intent.putExtra("restaurantURL", yelpDetails.getRestaurantURL());
                intent.putExtra("restaurantPhone", yelpDetails.getRestaurantPhone());
                intent.putExtra("restaurantRating", yelpDetails.getRestaurantRating());
                intent.putExtra("restaurantAvailability", yelpDetails.isRestaurantAvailability());

                ((Activity)context).setResult(2, intent);
                ((Activity)context).finish();
            }
        });

        return view;
    }
}
