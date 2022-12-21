package com.example.nasa_pod;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import java.util.ArrayList;

public class AboutNasaActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

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
                Intent intent = new Intent(AboutNasaActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_saved_pictures:
                Intent intent2 = new Intent(AboutNasaActivity.this, ActivitySavedImagesAndDates.class);
                startActivity(intent2);
                break;
            case R.id.nav_about_nasa:
                Intent intent3 = new Intent(AboutNasaActivity.this, AboutNasaActivity.class);
                startActivity(intent3);
                break;
            case R.id.nav_about_artemis:
                Intent intent4 = new Intent(AboutNasaActivity.this, AboutArtemisActivity.class);
                startActivity(intent4);
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
        setContentView(R.layout.activity_about_nasa);

        // Set the title and version number
        TextView tvTitle = findViewById(R.id.tv_activity_title);
        tvTitle.setText("About NASA");
        TextView tvVersion = findViewById(R.id.tv_activity_version);
        tvVersion.setText("v0.3");

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,myToolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();









    }
}


