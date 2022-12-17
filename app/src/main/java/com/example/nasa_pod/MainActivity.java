package com.example.nasa_pod;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
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
/**
 * MainActivity is the main activity of the application. It is responsible for displaying the image of the day,
 * allowing the user to pick a date for the image of the day, and saving the image of the day.
 */
public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String cTitle = "";
    String cDate = "";
    String cImageUrl = "";
    String cDescription = "";
    String TodaysDate = getTodaysDate();
    String SelectedDateFromUser = TodaysDate;

    /**
     * Displays a help dialog to the user.
     */
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
    /**
     * Gets the current date in the format yyyy-MM-dd.
     * @return The current date in the format yyyy-MM-dd.
     */
    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    /**
     * Sets the selected date from the user.
     * @param selectedDateFromUser The selected date from the user.
     */
    public void setSelectedDateFromUser(String selectedDateFromUser) {
        SelectedDateFromUser = selectedDateFromUser;
    }
    /**
     * Displays a date picker dialog to the user.
     */
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * GetJSONDataTask is an AsyncTask that retrieves JSON data from a given URL.
     *
     */
    public class GetJSONDataTask extends AsyncTask<Void, Void, String> {
        /**
         * This method retrieves JSON data from a given URL.
         *
         * @param voids This parameter is not used.
         * @return      The JSON data retrieved from the URL.
         */
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
    /**
     * This method is called when a date is set by the user.
     * It sets the selected date from the user and creates a new list of POTD objects.
     * It also creates a new POTDAdapter and sets the list view adapter to the new adapter.
     * It then creates a new GetJSONDataTask and gets the json data from the task.
     * It then parses the json data and creates a new POTD object with the parsed data.
     * It then adds the new POTD object to the adapter and sets the progress bar to invisible.
     * It then loops through the adapter and sets the cTitle, cDate, cImageUrl, and cDescription variables to the values of the POTD object.
     *
     * @param view The DatePicker view
     * @param year The year selected by the user
     * @param month The month selected by the user
     * @param dayOfMonth The day of the month selected by the user
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String SelectedDateFromUser = year + "-" + (month + 1) + "-" + dayOfMonth;
        setSelectedDateFromUser(SelectedDateFromUser);

        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
        ListView listView = findViewById(R.id.image_of_the_day_list);
        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
        listView.setAdapter(adapter);

        ProgressBar progressBar = findViewById(R.id.progressBar);

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
                progressBar.setVisibility(View.INVISIBLE);
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
    /**
     * This method is called when the options menu is created.
     * It inflates the menu from the XML file and returns true.
     *
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /**
     * This method is called when an item is selected from the menu.
     * It checks the id of the item and if it is the action_help item, it calls the showHelpDialog method.
     *
     * @param item The MenuItem selected
     * @return true if the item is selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Sets the name of the user.
     * @param view The view of the application.
     */
    public void setName(View view) {
        EditText editText = findViewById(R.id.editText);
        TextView tvName = findViewById(R.id.tvName);
        String name = editText.getText().toString();
        tvName.setText(name);
    }


    /**
     * This method is called when the activity is created.
     * It sets the content view to the activity_main layout,
     * initializes the toolbar, drawerLayout, and navigationView,
     * creates a progress bar, creates an array list of POTD objects,
     * creates a POTDAdapter, creates a list view, creates a GetJSONDataTask,
     * creates a JSONObject, creates a POTD object, adds the POTD object to the adapter,
     * sets the visibility of the progress bar to invisible,
     * creates a button, sets an onClickListener for the button,
     * creates an insertButton, sets an onClickListener for the insertButton,
     * creates a loadSavedListView, sets an onClickListener for the loadSavedListView,
     * creates a button2, sets an onClickListener for the button2.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar;
        DrawerLayout drawerLayout;
        NavigationView navigationView;

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
        ListView listView = findViewById(R.id.image_of_the_day_list);
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

                progressBar.setVisibility(View.INVISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                dbHelper.insertData(cTitle, cDate, cImageUrl, cDescription);
            }
        });

        Button loadSavedListView = findViewById(R.id.bViewList);
        loadSavedListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ActivitySavedImagesAndDates.class);
                startActivity(intent);
            }
        });

        Button button2 = findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvFragment = findViewById(R.id.tvFragment);
                EditText editText = findViewById(R.id.editText);
                TextView tvName = findViewById(R.id.tvName);
                String name = editText.getText().toString();
                tvName.setText(name);
//                tvFragment.setText(name);
                editText.setText("");
            }
        });

    }
}