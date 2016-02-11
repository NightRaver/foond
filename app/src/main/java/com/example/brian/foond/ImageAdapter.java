package com.example.brian.foond;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 2/3/2016.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;

    private List<Food> foods = new ArrayList<Food>();
    private LayoutInflater inflater;


    public ImageAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        foods.add(new Food(R.drawable.sample_0));
        foods.add(new Food(R.drawable.sample_1));
        foods.add(new Food(R.drawable.sample_2));
        foods.add(new Food(R.drawable.sample_3));
        foods.add(new Food(R.drawable.sample_4));
        foods.add(new Food(R.drawable.sample_5));
        foods.add(new Food(R.drawable.sample_6));
        foods.add(new Food(R.drawable.sample_7));
        foods.add(new Food(R.drawable.sample_2));
        foods.add(new Food(R.drawable.sample_4));
        foods.add(new Food(R.drawable.sample_6));
        foods.add(new Food(R.drawable.sample_5));

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
        return foods.get(position).foodId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ImageView picture;

        if(view == null) {
            view = inflater.inflate(R.layout.gridview_item, parent, false);
            view.setTag(R.id.picture, view.findViewById(R.id.picture));
        }

        picture = (ImageView) view.getTag(R.id.picture);

        Food food = (Food) getItem(position);

        picture.setImageResource(food.foodId);

        return view;
    }


    private class Food {
        final int foodId;

        Food(int foodId) {
            this.foodId = foodId;
        }
    }
}
