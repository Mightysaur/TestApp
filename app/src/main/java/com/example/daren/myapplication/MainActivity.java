package com.example.daren.myapplication;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.WriteAbortedException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public BluetoothConnectionService getMainBluetoothConnection() {
        return btActivity.getmBluetoothConnection();
    }

    private BluetoothConnectionService MainBluetoothConnection;

    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private BluetoothActivity btActivity = new BluetoothActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        createNewSessionLog();
        createNewFavourites();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_pair_glasses) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, btActivity).commit();
        } else if (id == R.id.nav_new_session) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new presession()).commit();
        } else if (id == R.id.nav_rename_session) {

        } else if (id == R.id.nav_delete_session) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DeleteLog()).commit();
        } else if (id == R.id.nav_reset_learnt_words) {
            fragmentManager.beginTransaction().replace(R.id.content_frame, new DefTab()).commit();
        } else if (id == R.id.nav_reset_session_data) {
            clearSessions();

        } else if (id == R.id.nav_logs){
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Logs_tab()).commit();
        } else if (id == R.id.nav_favourites){
            fragmentManager.beginTransaction().replace(R.id.content_frame, new Favourites_tab()).commit();
        } else if (id == R.id.nav_help){
            fragmentManager.beginTransaction().replace(R.id.content_frame, new presession()).commit();
        } else if (id == R.id.nav_closeapp){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void requestRecordAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String requiredPermission = Manifest.permission.RECORD_AUDIO;

            // If the user previously denied this permission then show a message explaining why
            // this permission is needed
            if (checkCallingOrSelfPermission(requiredPermission) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{requiredPermission}, 101);
            }
        }
    }

    private void clearSessions(){
        FragmentManager fragmentManager = getFragmentManager();
        File dir = getFilesDir();
        File file = new File(dir, "sessions.xml");
        file.delete();
        createNewSessionLog();
        fragmentManager.beginTransaction().replace(R.id.content_frame, new Logs_tab()).commit();
    }

    private void createNewSessionLog(){
        File path = getFilesDir();

        File file = new File(path, "sessions.xml");

        if(!file.exists()){
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            try{
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);
                serializer.startTag("","Sessions");

                serializer.endTag("","Sessions");
                serializer.endDocument();
                String result = writer.toString();

                FileOutputStream fos = openFileOutput("sessions.xml", Context.MODE_PRIVATE);
                fos.write(result.getBytes());
                fos.close();
                Toast.makeText(this.getApplicationContext(), result,Toast.LENGTH_LONG).show();

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");
                dir.mkdirs();
                File file2 = new File(dir, "sessions.xml");

                FileOutputStream f = new FileOutputStream(file2);
                f.write(result.getBytes());
                f.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    private void createNewFavourites() {
        File path = getFilesDir();

        File file = new File(path, "favourites.xml");

        if(!file.exists()){
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            try{
                serializer.setOutput(writer);
                serializer.startDocument("UTF-8", true);
                serializer.startTag("","Favourites");

                serializer.endTag("","Favourites");
                serializer.endDocument();
                String result = writer.toString();

                FileOutputStream fos = openFileOutput("favourites.xml", Context.MODE_PRIVATE);
                fos.write(result.getBytes());
                fos.close();
                //Toast.makeText(this.getApplicationContext(), result,Toast.LENGTH_LONG).show();

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");
                dir.mkdirs();
                File file2 = new File(dir, "favourites.xml");

                FileOutputStream f = new FileOutputStream(file2);
                f.write(result.getBytes());
                f.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }


}
