package com.example.nasa_pod;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    // POTD object class
    class POTD {
        public String potdTitle;
        public String potdDescription;
        public String potdDate;
        public String potdImageUrl;
        public POTD(String potdTitle, String potdDate, String potdImageUrl, String potdDescription) {
            this.potdTitle = potdTitle;
            this.potdDate = potdDate;
            this.potdImageUrl = potdImageUrl;
            this.potdDescription = potdDescription;
        }
    }
    // POTD adapter class
    class POTDAdapter extends ArrayAdapter<POTD> {
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
//            TextView tvImageUrl = (TextView) convertView.findViewById(R.id.tvImageUrl);
            ImageView ivImageUrl = (ImageView) convertView.findViewById(R.id.ivImageUrl);
            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

            Glide.with(MainActivity.this).load(newPOTD.potdImageUrl).into(ivImageUrl);

            tvName.setText(newPOTD.potdTitle);
            tvDate.setText(newPOTD.potdDate);

            tvDescription.setText(newPOTD.potdDescription);
            return convertView;
        }
    }

    // Get todays date
    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    String TodaysDate = getTodaysDate();
    String SelectedDateFromUser = TodaysDate;


    // Getting the JSON data from the API URL
    public class GetJSONDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            String jsonData = "";
            try {
                URL url = new URL("https://api.nasa.gov/planetary/apod?date="+SelectedDateFromUser+"&api_key=Ykxp8r2fCKlFjcWg54dxy4U3zaWJQ96EuB87Sn6C");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonData += line;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonData;
        }
    }
    // Calendar Picker function
    public void setSelectedDateFromUser(String selectedDateFromUser) {
        SelectedDateFromUser = selectedDateFromUser;
    }
    public String getSelectedDateFromUser() {
        return SelectedDateFromUser;
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String SelectedDateFromUser = year + "-" + (month + 1) + "-" + dayOfMonth;
        System.out.println("selectedDate: " + SelectedDateFromUser+"-----------------------------------------------------------------------------------------");
        setSelectedDateFromUser(SelectedDateFromUser);




        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
        ListView listView = findViewById(R.id.image_of_the_day_list);
        listView.setAdapter(adapter);

        // Getting the JSON data and putting it where it needs to be
        GetJSONDataTask task = new GetJSONDataTask();
        String jsonData = null;
        try {
            jsonData = task.execute().get();
            String title, date, imageurl, description;
            try {
                JSONObject imageData = new JSONObject(jsonData);
                title = imageData.getString("title");
                date = imageData.getString("date");
                imageurl = imageData.getString("url");
                description = imageData.getString("explanation");
                POTD potd = new POTD(title, date,imageurl,description);
                adapter.add(potd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    // Function grabs the image url from the json data, can be deleted later if not used.
    public String getTheImageURL(){
        String imageurl;
        GetJSONDataTask task = new GetJSONDataTask();
        String jsonData = null;
        try {
            jsonData = task.execute().get();
            try {
                JSONObject obj = new JSONObject(jsonData);
                imageurl = obj.getString("url");
                return imageurl;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    // Main section --------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
//        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
//        ListView listView = findViewById(R.id.image_of_the_day_list);
//        listView.setAdapter(adapter);


//        // Getting the JSON data and putting it where it needs to be
//        GetJSONDataTask task = new GetJSONDataTask();
//        String jsonData = null;
//        try {
//            jsonData = task.execute().get();
//            String title, date, imageurl, description;
//            try {
//                JSONObject imageData = new JSONObject(jsonData);
//                title = imageData.getString("title");
//                date = imageData.getString("date");
//                imageurl = imageData.getString("url");
//                description = imageData.getString("explanation");
//                POTD potd = new POTD(title, date,imageurl,description);
//                adapter.add(potd);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // Printing the ImageURL - if we need it - currently we don't ------------------------------
        String values = getTheImageURL();
        System.out.println(values);
        // Print stuff here. -----------------------------------------------------------------------


        Button button = findViewById(R.id.bPickDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();

            }
        });

//        System.out.println("SelectedDateFromUser: " + SelectedDateFromUser);
        System.out.println(getSelectedDateFromUser()+ "---------------------------------------------------------------------------------------------------------------------");



    }
}