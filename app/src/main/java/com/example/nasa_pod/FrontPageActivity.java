package com.example.nasa_pod;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;


import com.google.android.material.navigation.NavigationView;

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

public class FrontPageActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String cTitle = "";
    String cDate = "";
    String cImageUrl = "";
    String cDescription = "";
    String TodaysDate = getTodaysDate();
    String SelectedDateFromUser = TodaysDate;



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

    public void getJSONData(){
        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
        ListView listView = findViewById(R.id.image_of_the_day_list);
        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
        listView.setAdapter(adapter);
        GetJSONDataTask task = new GetJSONDataTask();
        String jsonData = null;
        try {
            jsonData = task.execute().get();
            String title, date, imageurl, description;
            try {
                JSONObject imageData = new JSONObject(jsonData);
                title = imageData.getString("title");
                date = imageData.getString("date");
                imageurl = imageData.getString("hdurl");
                description = imageData.getString("explanation");
                POTD potd = new POTD(title, date,imageurl,description);
                adapter.add(potd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < adapter.getCount(); i++) {
            POTD newPOTD = adapter.getItem(i);
            cTitle = newPOTD.potdTitle;
            cDate = newPOTD.potdDate;
            cImageUrl = newPOTD.potdImageUrl;
            cDescription = newPOTD.potdDescription;
        }
    }

    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void setSelectedDateFromUser(String selectedDateFromUser) {
        SelectedDateFromUser = selectedDateFromUser;
    }

    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String SelectedDateFromUser = year + "-" + (month + 1) + "-" + dayOfMonth;
        setSelectedDateFromUser(SelectedDateFromUser);
        getJSONData();
    }






//    private DrawerLayout drawer;
//
//    @Override
//    public void onBackPressed(){
//        if (drawer.isDrawerOpen(GravityCompat.START)){
//            drawer.closeDrawer(GravityCompat.START);
//        }else {
//            super.onBackPressed();
//        }
//    }

//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_account:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessageFragment()).commit();
//                break;
//            case R.id.nav_saved_pictures:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SavedPicturesFragment()).commit();
//                break;
//            case R.id.nav_logout:
//                Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show();
//                break;
//        }
//        drawer.closeDrawer(GravityCompat.START);
//        return  true;
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);



//        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,myToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();




        getJSONData();

        Button button = findViewById(R.id.bPickDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        Button insertButton = findViewById(R.id.bSavePOTD);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(FrontPageActivity.this);
                dbHelper.insertData(cTitle, cDate, cImageUrl, cDescription);
            }
        });

        Button loadSavedListView = findViewById(R.id.bViewList);
        loadSavedListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPageActivity.this, ActivitySavedImagesAndDates.class);
                startActivity(intent);
            }
        });


    }
}