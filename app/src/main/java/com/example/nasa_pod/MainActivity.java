package com.example.nasa_pod;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import androidx.fragment.app.FragmentManager;


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
 * MainActivity is the main activity of the application.
 * It extends AppCompatActivity and implements DatePickerDialog.OnDateSetListener and NavigationView.OnNavigationItemSelectedListener.
 * It contains methods to get JSON data from the NASA API, get the current date, set the selected date from the user, show a date picker dialog,
 * show a help dialog, and handle the back button, options menu, and navigation menu.
 */
public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, NavigationView.OnNavigationItemSelectedListener {
    String cTitle = "";
    String cDate = "";
    String cImageUrl = "";
    String cDescription = "";
    String TodaysDate = getTodaysDate();
    String SelectedDateFromUser = TodaysDate;
    private DrawerLayout drawer;

    /**
     * This class is used to get JSON data from the NASA API.
     * It extends the AsyncTask class and overrides the doInBackground() method.
     * The doInBackground() method creates a URL connection to the NASA API,
     * sends a GET request, and reads the response from the API.
     * The response is then returned as a String.
     */
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

    /**
     * This method retrieves JSON data from a remote source and adds it to an ArrayList of POTD objects.
     * It then sets the adapter for the ListView and creates a GetJSONDataTask object.
     * It then parses the JSON data and creates a POTD object with the data.
     * Finally, it iterates through the adapter and assigns the data to the corresponding variables.
     */
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
    /**
     * Gets the current date in the format yyyy-MM-dd.
     * @return the current date in the format yyyy-MM-dd.
     */
    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    /**
     * Sets the selected date from the user.
     * @param selectedDateFromUser the selected date from the user.
     */
    public void setSelectedDateFromUser(String selectedDateFromUser) {
        SelectedDateFromUser = selectedDateFromUser;
    }
    /**
     * Shows a date picker dialog.
     */
    public void showDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    /**
     * Sets the selected date from the user and gets the JSON data.
     * @param view the view.
     * @param year the year.
     * @param month the month.
     * @param dayOfMonth the day of the month.
     */
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String SelectedDateFromUser = year + "-" + (month + 1) + "-" + dayOfMonth;
        setSelectedDateFromUser(SelectedDateFromUser);
        getJSONData();
    }

    /**
     * This method is called when the back button is pressed.
     * If the navigation drawer is open, it will close the drawer.
     * Otherwise, it will call the superclass's onBackPressed() method.
     */
    @Override
    public void onBackPressed(){
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    /**
     * This method is called when the options menu is created.
     * It inflates the menu from the xml file R.menu.menu_help and returns true.
     *
     * @param menu The options menu in which items are placed.
     * @return true for the menu to be displayed; false otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_help, menu);
        return true;
    }

    /**
     * Displays a help dialog with instructions on how to use the interface.
     * The dialog contains a title, message, and an OK button.
     * When the OK button is pressed, the dialog is dismissed.
     */
    public void showHelpDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
        builder.setMessage("This interface allows you to view, save, and pick dates for images of the day. To view the list of images, press the 'Saved Images' button located in the app drawer to the left. To get more details about the current Image of the Day, select it to show a dropdown description. To save the current image of the day, press the 'Save POTD' button. To pick a date for the image of the day, press the 'Pick Date' button.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * This method is called when an options item is selected.
     *
     * @param item The selected menu item
     * @return true if the item is the help menu item, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_help) {// Handle the help menu item
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when an item in the navigation menu is selected.
     * Depending on the item selected, it will either start a new activity, or replace the current fragment with a new one.
     *
     * @param item The item that was selected in the navigation menu
     * @return true if the item was selected, false otherwise
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_saved_pictures:
                Intent intent2 = new Intent(MainActivity.this, ActivitySavedImagesAndDates.class);
                startActivity(intent2);
                break;
            case R.id.nav_my_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyAccountActivity()).commit();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return  true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvTitle = findViewById(R.id.tv_activity_title);
        tvTitle.setText("Picture Of The Day");
        TextView tvVersion = findViewById(R.id.tv_activity_version);
        tvVersion.setText("v0.2");

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,myToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getJSONData();

        /**
         * This code sets an OnClickListener for the button with the id bPickDate.
         * When the button is clicked, the showDatePickerDialog() method is called.
         */
        Button button = findViewById(R.id.bPickDate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        /**
         * This code creates a button and sets an onClickListener to it. When the button is clicked,
         * a DatabaseHelper object is created and the insertData method is called with the given parameters.
         * Finally, a toast notification is shown to the user.
         */
        Button insertButton = findViewById(R.id.bSavePOTD);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
                dbHelper.insertData(cTitle, cDate, cImageUrl, cDescription);
                // Show toast notification
                Toast.makeText(getApplicationContext(), "Picture Saved!", Toast.LENGTH_SHORT).show();
            }
        });

        ListView listView = findViewById(R.id.image_of_the_day_list);
        /**
         * Sets an OnItemClickListener for the listView. When an item is clicked, the visibility of the descriptionTextView is toggled and a Toast is displayed.
         * @param adapterView The AdapterView where the click happened.
         * @param view The view within the AdapterView that was clicked.
         * @param i The position of the view in the adapter.
         * @param l The row id of the item that was clicked.
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView descriptionTextView = (TextView) view.findViewById(R.id.tvDescription);
                if (descriptionTextView.getVisibility() == View.VISIBLE) {
                    descriptionTextView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Less Details", Toast.LENGTH_SHORT).show();

                } else {
                    descriptionTextView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "More Details", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}