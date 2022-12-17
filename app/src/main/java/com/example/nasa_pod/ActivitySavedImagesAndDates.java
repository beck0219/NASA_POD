package com.example.nasa_pod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nasa_pod.DatabaseHelper;
import java.util.ArrayList;
/**
 * ActivitySavedImagesAndDates is an activity that allows users to view, save, and pick dates for images of the day.
 * It contains a list view that is populated with data from the database.
 * It also contains a help menu item that displays a dialog with instructions on how to use the activity.
 * It also contains a long click listener that allows users to delete items from the list view.
 */
public class ActivitySavedImagesAndDates extends Activity {

    //Read data from the database and use it to populate a custom list view
    public void populateListView() {
        DatabaseHelper dbHelper = new DatabaseHelper(ActivitySavedImagesAndDates.this);
        Cursor data = dbHelper.getData();
        ArrayList<SavedPOTD> new_image_of_the_day_list = new ArrayList<SavedPOTD>();
        while (data.moveToNext()) {
            String date = data.getString(2);
            String imageUrl = data.getString(3);
            SavedPOTD potd = new SavedPOTD(date, imageUrl);
            new_image_of_the_day_list.add(potd);
        }
        SavedPOTDAdapter adapter = new SavedPOTDAdapter(this, new_image_of_the_day_list);
        ListView listView = findViewById(R.id.lvSaved_images_and_dates);
        listView.setAdapter(adapter);

    }
    public void storeImageUrlsToSharedPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("imageUrls", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DatabaseHelper dbHelper = new DatabaseHelper(ActivitySavedImagesAndDates.this);
        Cursor data = dbHelper.getData();
        while (data.moveToNext()) {
            String imageUrl = data.getString(3);
            editor.putString("imageUrl", imageUrl);
        }
        editor.apply();
    }




    //Help Menu Item

    public void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("This interface allows you to view, save, and pick dates for images of the day. To view the list of images, press the 'View List' button. To save the current image of the day, press the 'Save POTD' button. To pick a date for the image of the day, press the 'Pick Date' button.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images_and_dates);
        populateListView();

        ArrayList<SavedPOTD> new_image_of_the_day_list = new ArrayList<SavedPOTD>();

        SavedPOTDAdapter adapter = new SavedPOTDAdapter(this, new_image_of_the_day_list);
        ListView listView = findViewById(R.id.lvSaved_images_and_dates);

        // Store the image urls in the shared preferences folder for later use.
        storeImageUrlsToSharedPreferences();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //Get the selected list item
                SavedPOTD selectedListItem = (SavedPOTD) parent.getItemAtPosition(position);
                //Create an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySavedImagesAndDates.this);
                builder.setTitle("Warning");

                builder.setMessage("Would you like to delete " + selectedListItem + "?");
                //Set the positive button
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ActivitySavedImagesAndDates.this, selectedListItem + " deleted", Toast.LENGTH_SHORT).show();
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