package com.monisha.movieclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.monisha.moviecentralservice.movieCentralService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean mBound = false, mPlaying = false;
    private static movieCentralService mService;
    private String TAG = "MainActivity";
    private Bundle movieBundle = null;
    private Button bind, unbind, showmovieLib;
    private Spinner spinner;
    private TextView myAwesomeTextView;
    private ListView listView;
    private TextView npc, status;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mService = movieCentralService.Stub.asInterface(service);
            mBound = true;
            displayUpdate();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            mService = null;
            mBound = false;
            displayUpdate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bind = findViewById(R.id.bind);
        unbind = findViewById(R.id.unbind);
        showmovieLib = findViewById(R.id.show_movie);
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        npc = findViewById(R.id.nowPlaying);
        status = findViewById(R.id.status);
        myAwesomeTextView = (TextView)findViewById(R.id.myAwesomeTextView);
        listView = (ListView) findViewById(R.id.listview);
        // Update UI based on mBound status
        displayUpdate();

        // Setup Spinner to list movies in the UI
        ArrayAdapter<CharSequence> spinnerArrayAdapter =
                ArrayAdapter.createFromResource(this,
                        R.array.movieTitles,
                        R.layout.spinner_item);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(this);


        //setup list view

        String[] players = new String[] {"Movie 1", "Movie 2", "Movie 3", "Movie 4","Movie 5"};
        List<String> Players_list = new ArrayList<String>(Arrays.asList(players));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Players_list);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mBound){
                    try {

                        myAwesomeTextView.setText(bundle2string(mService.getmovieInfo(position),position));
                    }
                    catch (Exception e)
                    {
                        System.out.println("catch");
                    }
                }

                else
                    myAwesomeTextView.setText("not bound yet");
            }
        });



    }


    // Method to Start movie Player
    private void startmoviePlayer(String URL) {

            setContentView(R.layout.youtubevideoview);
            VideoView videoView = findViewById(R.id.YoutubeVideoView);
            videoView.setVideoPath(URL+"");
            //https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
            videoView.start();

    }


    // Method to Unbind from service
    private void unbindService() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        displayUpdate();
    }

    // Logic to bind to the service
    private void bindService() {
        if(!mBound) {
            Intent serviceIntent = new Intent(movieCentralService.class.getName());
            ServiceInfo serviceInfo = getPackageManager().resolveService(serviceIntent, 0).serviceInfo;
            serviceIntent.setComponent(new ComponentName(serviceInfo.packageName, serviceInfo.name));
            bindService(serviceIntent, this.mServiceConnection, Context.BIND_AUTO_CREATE);
            displayUpdate();
        }
    }

    // Button Handler method
    public void bindButtonOnClick(View view) {
        System.out.println("Binding Service");
        bindService();
    }

    // Button Unbind method
    public void unbindButtonOnClick(View view) {
        System.out.println("Unbinding Service");
        myAwesomeTextView.setText("Unbinded");
        unbindService();
    }


    public void showmovieOnClick(View view) throws RemoteException {
        if(movieBundle == null) {
            movieBundle = mService.getmovieList();
        }
        Intent intent = new Intent(this, movieLibrary.class);
        intent.putExtras(movieBundle);
        startActivity(intent);
    }

    // Getter method for service object
    public static movieCentralService getService() {
        return mService;
    }

    // When this method is called, it checks the state of the app and updates the UI components.
    // If the app is not bound to services, only bind button is enabled.
    public void displayUpdate() {
        bind.setEnabled(!mBound);
        unbind.setEnabled(mBound);
        showmovieLib.setEnabled(mBound);
        int bindBG, unbindBG;

        if(mBound) {
            status.setText("Binded");
            bindBG = R.color.disabled;
            unbindBG = R.color.enabled;
            spinner.setVisibility(View.VISIBLE);

        } else {
            status.setText("Not Binded");
            bindBG = R.color.enabled;
            unbindBG = R.color.disabled;
            spinner.setVisibility(View.INVISIBLE);
        }
        bind.setBackgroundResource(bindBG);
        bind.setBackgroundResource(unbindBG);


    }


    // Logic to handle Spinner Selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        stopmoviePlayer();
        if (mBound) {
            if (position == 0) {
                mPlaying = false;
            } else {
                Bundle bundle;
                try {
                    bundle = mService.getmovieInfo(position - 1);
                    if(bundle != null) {

                        mPlaying = true;
                        //Clients can access this information on Console

                        startmoviePlayer(getVideoUrl(position - 1));
                    } else {
                        Toast.makeText(this, "try again", Toast.LENGTH_LONG).show();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        displayUpdate();
    }


    //This converts bundle and returns it in a string format
    public static String bundle2string(Bundle bundle,int position) {
        String ss="";
        try{
            ss=mService.getmovieUrl(position);
        }
        catch(Exception e)
        {
            ss="";
        }
        if (bundle == null) {
            return null;
        }
        String string = "";
        for (String key : bundle.keySet()) {
            if(key.startsWith("bit"))
                continue;
            else
            string += " " + key + " : " + bundle.get(key) + ";"+"\n";
        }
        string += ss+ "Movie URL" + " ";
        return string;
    }


    public String getVideoUrl(int position)
    {
        String m="a";
        if(position==-1)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";

        if(position==0)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
        else if(position==1)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WhatCarCanYouGetForAGrand.mp4";
        else if(position==2)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4";
        else if(position==3)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        else if(position==4)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4";

        else if(position==5)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
        return m;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void playmovie(String URL) {
        startmoviePlayer(URL);
    }



}