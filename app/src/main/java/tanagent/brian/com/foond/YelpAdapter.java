package tanagent.brian.com.foond;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Brian on 2/24/2016.
 */
public class YelpAdapter extends ArrayAdapter<YelpDetails> {

    private Context context;
    private List<YelpDetails> yelpDetailsList;

    public YelpAdapter(Context context, int resource, List<YelpDetails> objects) {
        super(context, resource, objects);
        this.context = context;
        this.yelpDetailsList = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.i("TEST", "getView() called at position " + position);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.yelp_item, parent, false);

        final YelpDetails yelpDetails = yelpDetailsList.get(position);

        TextView restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
        restaurantName.setText(yelpDetails.getRestaurantName());

        TextView restaurantAddress = (TextView) view.findViewById(R.id.restaurant_address);
        restaurantAddress.setText(yelpDetails.getAddress());

        //Still need to do one for images
        ImageView restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
        restaurantImage.setImageResource(yelpDetails.getPhotoId());

        return view;
    }
}
