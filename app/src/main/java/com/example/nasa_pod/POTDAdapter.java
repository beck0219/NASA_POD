package com.example.nasa_pod;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
/**
 * This class is an adapter for the Picture of the Day (POTD) class.
 * It extends the ArrayAdapter class and takes in a Context and an ArrayList of POTD objects.
 * The getView() method is overridden to inflate the activity_image_of_the_day_list_item layout,
 * set the TextViews and ImageViews with the corresponding POTD data, and return the view.
 */
public class POTDAdapter extends ArrayAdapter<POTD> {
    public POTDAdapter(Context context, ArrayList<POTD> potds) {
        super(context, 0, potds);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        POTD newPOTD = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_image_of_the_day_list_item, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvTitle);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);
        ImageView ivImageUrl = (ImageView) convertView.findViewById(R.id.ivImageUrl);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
        Glide.with(getContext()).load(newPOTD.potdImageUrl).into(ivImageUrl);
        tvName.setText(newPOTD.potdTitle);
        tvDate.setText(newPOTD.potdDate);
        tvDescription.setText(newPOTD.potdDescription);
        return convertView;
    }
}