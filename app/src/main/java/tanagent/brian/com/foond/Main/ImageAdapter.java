package tanagent.brian.com.foond.Main;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import tanagent.brian.com.foond.R;
import tanagent.brian.com.foond.Select;

/**
 * Created by Brian on 2/3/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    private List<Food> foods = new ArrayList<Food>();
    private LayoutInflater inflater;

    public ImageAdapter(Context context, List<Food> foods) {
        inflater = LayoutInflater.from(context);
        this.foods = foods;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView imageView;

        if(view == null) {
            view = inflater.inflate(R.layout.gridview_item, parent, false);
            view.setTag(R.id.picture, view.findViewById(R.id.picture));
        }

        imageView = (ImageView) view.getTag(R.id.picture);

        Food food = (Food) getItem(position);

        final String imageUrl = food.foodId;

        Picasso.with(view.getContext()).load(imageUrl).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Select.class);
                intent.putExtra("url", imageUrl);
                v.getContext().startActivity(intent);
                Log.i("url", imageUrl.toString());
            }
        });

        return view;
    }
}