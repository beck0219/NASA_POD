package com.example.nasa_pod;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * This class is an adapter for the SavedPOTD class.
 * It is used to display the saved images and dates in a list view.
 *
 */
public class SavedPOTDAdapter extends ArrayAdapter<SavedPOTD> {
    public SavedPOTDAdapter(Context context, ArrayList<SavedPOTD> savedPotds) {
        super(context, 0, savedPotds);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SavedPOTD savedPOTD = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_saved_images_and_dates_item, parent, false);
        }
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvSavedDate);
        ImageView ivImageUrl = (ImageView) convertView.findViewById(R.id.ivSavedImage);

        tvDate.setText(savedPOTD.potdDate);
        Glide.with(getContext()).load(savedPOTD.potdImageUrl).into(ivImageUrl);

        return convertView;
    }
}