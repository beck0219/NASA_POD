package com.example.nasa_pod;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

public class MyAccountActivity extends Fragment implements View.OnClickListener{

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_my_account, container, false);
        EditText editText = (EditText) view.findViewById(R.id.editText);
        Button submitButton = (Button) view.findViewById(R.id.button);
        TextView nameTextView = (TextView) view.findViewById(R.id.textView2);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        nameTextView.setText(name);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                nameTextView.setText(name);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("name", name);
                editor.apply();


                // Show snackbar notification
                Snackbar.make(view, "Name submitted!", Snackbar.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {

    }
}