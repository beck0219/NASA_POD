package com.example.nasa_pod;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
/**
 * This class is a subclass of Fragment and is used to display a TextView with the text "Hello World!"
 */
public class TextFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        TextView textView = (TextView) view.findViewById(R.id.tvFragment);
        textView.setText("Hello World!");
        return view;
    }
}
