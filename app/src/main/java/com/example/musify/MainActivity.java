package com.example.musify;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //Declare recyclerview
    RecyclerView tracksRV;
    static TrackAdapter trackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isStoragePermissionGranted();   //Ask for permission runtime

        tracksRV = findViewById(R.id.rvTracks);
        tracksRV.setLayoutManager(new LinearLayoutManager(this));
        tracksRV.addItemDecoration(new DividerItemDecoration(tracksRV.getContext(), DividerItemDecoration.VERTICAL));


        //Get tracks using FileManager
        FileManager fileManager = new FileManager(this);
        // data to populate the RecyclerView with
        ArrayList<Track> tracks = (ArrayList<Track>) fileManager.getTracks();

        trackAdapter = new TrackAdapter(this, tracks);
        trackAdapter.setClickListener(new TrackAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Track track = trackAdapter.getItem(position); // Track selected

                /* On click go to Player Activity and play the track*/
                Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                i.putExtra("TRACK", (Serializable) track);
                i.putExtra("POSITION", position);
                startActivity(i);
            }
        });

        tracksRV.setAdapter(trackAdapter);

    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMINIT", "Permission is granted");
                return true;
            } else {

                Log.v("PERMINIT", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            Log.v("PERMINIT", "Permission is granted");
            return true;
        }
    }


}
