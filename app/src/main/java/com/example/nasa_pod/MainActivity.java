package com.example.nasa_pod;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "potd_db";
    private static final String TABLE_NAME = "potd_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "TITLE";
    private static final String COL_3 = "DATE";
    private static final String COL_4 = "IMAGE_URL";
    private static final String COL_5 = "DESCRIPTION";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_2 + " TEXT, " +
                COL_3 + " TEXT, " +
                COL_4 + " TEXT, " +
                COL_5 + " TEXT" +
                ")";
        db.execSQL(createTableStatement);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String title, String date, String imageUrl, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, date);
        contentValues.put(COL_4, imageUrl);
        contentValues.put(COL_5, description);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }
    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }
    public boolean updateData(String id, String title, String date, String imageUrl, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, id);
        contentValues.put(COL_2, title);
        contentValues.put(COL_3, date);
        contentValues.put(COL_4, imageUrl);
        contentValues.put(COL_5, description);
        db.update(TABLE_NAME, contentValues, "ID = ?", new String[] {id});
        return true;
    }
    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[] {id});
    }
}
public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    String cTitle = "";
    String cDate = "";
    String cImageUrl = "";
    String cDescription = "";
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
            ImageView ivImageUrl = (ImageView) convertView.findViewById(R.id.ivImageUrl);
            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            Glide.with(MainActivity.this).load(newPOTD.potdImageUrl).into(ivImageUrl);
            tvName.setText(newPOTD.potdTitle);
            tvDate.setText(newPOTD.potdDate);
            tvDescription.setText(newPOTD.potdDescription);
            return convertView;
        }
    }
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
    public void setSelectedDateFromUser(String selectedDateFromUser) {
        SelectedDateFromUser = selectedDateFromUser;
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
        for (int i = 0; i < adapter.getCount(); i++) {
            POTD newPOTD = adapter.getItem(i);
            cTitle = newPOTD.potdTitle;
            cDate = newPOTD.potdDate;
            cImageUrl = newPOTD.potdImageUrl;
            cDescription = newPOTD.potdDescription;
        }
    }
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

    //Read data from the database and use it to populate a custom list view
    public void populateListView() {
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        Cursor data = dbHelper.getData();
        ArrayList<POTD> new_image_of_the_day_list = new ArrayList<POTD>();
        while (data.moveToNext()) {
            String title = data.getString(1);
            String date = data.getString(2);
            String imageUrl = data.getString(3);
            String description = data.getString(4);
            POTD potd = new POTD(title, date, imageUrl, description);
            new_image_of_the_day_list.add(potd);
        }
        POTDAdapter adapter = new POTDAdapter(this, new_image_of_the_day_list);
        ListView listView = findViewById(R.id.image_of_the_day_list);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        Button populateButton = findViewById(R.id.bViewList);
        populateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateListView();
            }
        });
    }
}