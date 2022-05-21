package com.monisha.movieclient;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.RemoteException;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.monisha.moviecentralservice.movieCentralService;

import java.util.ArrayList;

public class movieLibrary extends AppCompatActivity {

    private String TAG = "movieLibrary";
    private ArrayList<String> movieTitles, directorNames;
    private ArrayList<Bitmap> thumbnails;
    private movieCentralService mService;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private movieLibraryAdapter adapter;
    private MediaPlayer moviePlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_library);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            movieTitles = bundle.getStringArrayList(Utils.TITLE_LIST);
            directorNames = bundle.getStringArrayList(Utils.DIRECTOR_LIST);
            thumbnails = bundle.getParcelableArrayList(Utils.BITMAP_LIST);
        }

        mService = MainActivity.getService();
        RVClickListener listener = (view, position) -> {
            try {
                if(moviePlayer != null) moviePlayer.stop();

                String URL = (String) mService.getmovieUrl(position);
                startmoviePlayer(getVideoUrl(position-1));;
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        };

        recyclerView = findViewById(R.id.movieLibraryRV);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new movieLibraryAdapter(movieTitles, directorNames, thumbnails, listener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(moviePlayer != null) {
            moviePlayer.stop();
        }
    }



    public String getVideoUrl(int position)
    {
        String m="a";
        if(position==-1)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";
        if(position==0)
            m="https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4";
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

    private void playmovie(String URL) {
        startmoviePlayer(URL);
    }

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


}