package com.example.nasa_pod;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
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

public class ActivitySavedImagesAndDates extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    DatabaseHelper dbHelper = new DatabaseHelper(ActivitySavedImagesAndDates.this);




    //Read data from the database and use it to populate a custom list view
    public void populateListView() {
        DatabaseHelper dbHelper = new DatabaseHelper(ActivitySavedImagesAndDates.this);
        Cursor data = dbHelper.getData();
        ArrayList<SavedPOTD> saved_image_of_the_day_list = new ArrayList<SavedPOTD>();
        while (data.moveToNext()) {
            int id = data.getInt(0);
            String date = data.getString(2);
            String imageUrl = data.getString(3);
            SavedPOTD savedPOTD = new SavedPOTD(id, date, imageUrl);
            saved_image_of_the_day_list.add(savedPOTD);
        }
        SavedPOTDAdapter savedPOTDAdapter = new SavedPOTDAdapter(this, saved_image_of_the_day_list);
        ListView savedlistView = findViewById(R.id.lvSaved_images_and_dates);
        savedlistView.setAdapter(savedPOTDAdapter);

    }





    private DrawerLayout drawer;

    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(ActivitySavedImagesAndDates.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_saved_pictures:
                Intent intent2 = new Intent(ActivitySavedImagesAndDates.this, ActivitySavedImagesAndDates.class);
                startActivity(intent2);
                break;
            case R.id.nav_my_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyAccountActivity()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return  true;
    }


    // Inflate the menu resource file in the onCreateOptionsMenu() method
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }



    public void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("This interface allows you to view, save, and pick dates for images of the day. To view the list of images, press the 'Saved Pictures' button. You can also view your account information in the drawer menu to the left. There you can input your name to be stored for later.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    // Add an onOptionsItemSelected() method to handle the selection of the menu item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {// Handle the help menu item
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images_and_dates);

        // Set the title and version number
        TextView tvTitle = findViewById(R.id.tv_activity_title);
        tvTitle.setText("Saved Pictures & Dates");
        TextView tvVersion = findViewById(R.id.tv_activity_version);
        tvVersion.setText("v0.5");

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,myToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        populateListView();

        ArrayList<SavedPOTD> saved_image_of_the_day_list = new ArrayList<SavedPOTD>();

        SavedPOTDAdapter adapter = new SavedPOTDAdapter(this, saved_image_of_the_day_list);
        ListView savedlistView = findViewById(R.id.lvSaved_images_and_dates);



        savedlistView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                //Get the selected list item
                SavedPOTD selectedListItem = (SavedPOTD) parent.getItemAtPosition(position);
                int databaseId = selectedListItem.getPotdID();
                //Create an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySavedImagesAndDates.this);
                builder.setTitle("Warning");

                builder.setMessage("Would you like to delete " + selectedListItem +"ID: "+ position+" | "+ databaseId+ "?");
                //Set the positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dbHelper.deleteData(String.valueOf(databaseId));
                        Toast.makeText(ActivitySavedImagesAndDates.this, selectedListItem + " deleted", Toast.LENGTH_SHORT).show();
                        populateListView();
                    }
                });
                //Set the negative button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ActivitySavedImagesAndDates.this, "Action canceled...", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                //Create and show the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            }
        });



    }
}