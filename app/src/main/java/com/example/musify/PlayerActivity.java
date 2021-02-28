package com.example.musify;

import android.content.ContentUris;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    ImageButton playBtnV;
    TextView trackNameV;
    TextView artistNameV;
    TextView current_timeV;
    TextView total_timeV;
    SeekBar seekBarV;

    boolean playing;
    MediaPlayer mediaPlayer;
    private Handler mediaHandler = new Handler();
    /*
     *   Updates song time on slider and starttime TextView
     */
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            int currenttime = 111;
            try {
                currenttime = mediaPlayer.getCurrentPosition();
            } catch (Exception e) {
                // good practice
                Thread.currentThread().interrupt();
                return;
                //TODO RELEASE SLIDER RNNABLE
            }
            current_timeV.setText(String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes((long) currenttime),
                    TimeUnit.MILLISECONDS.toSeconds((long) currenttime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) currenttime))));

            seekBarV.setProgress((int) currenttime);
            mediaHandler.postDelayed(this, 100);
        }
    };
    /*
     *   Updates song time base on User slide on track
     */
    private Runnable UpdateSlider = new Runnable() {
        public void run() {
            seekBarV.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (mediaPlayer != null && fromUser) {
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    return;
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    return;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        trackNameV = findViewById(R.id.title);
        artistNameV = findViewById(R.id.artist);
        current_timeV = findViewById(R.id.currenttime);
        total_timeV = findViewById(R.id.totaltime);
        seekBarV = findViewById(R.id.seekBar);
        playBtnV = findViewById(R.id.play);

        // Get track EXTRA
        Track track = (Track) getIntent().getSerializableExtra("TRACK");
        trackNameV.setText(track.getTrackName());
        artistNameV.setText(track.getArtistName());


        Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();


        // Get track Uri
        Uri contentUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, track.getID());

        // Set MediaPlayer attributes
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        //Set MediaPlayer data source
        // Add an onPreparedListener to start the track as soon as it gets prepared
        try {
            mediaPlayer.setDataSource(getApplicationContext(), contentUri);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    int finalTime = mediaPlayer.getDuration();
                    int startTime = mediaPlayer.getCurrentPosition();
                    seekBarV.setMax((int) finalTime);
                    total_timeV.setText(String.format("%d:%d ",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            finalTime)))
                    );

                    current_timeV.setText(String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                            startTime)))
                    );

                    // Set progress on the seek bar
                    seekBarV.setProgress((int) startTime);

                    /* Handlers for updating song time-slider */
                    mediaHandler.postDelayed(UpdateSongTime, 100);
                    mediaHandler.post(UpdateSlider);


                    //Play/Pause button Listener
                    playBtnV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mediaPlayer.isPlaying()) {
                                Toast.makeText(getApplicationContext(), "Paused...", Toast.LENGTH_SHORT).show();
                                mediaPlayer.pause();
                                playBtnV.setImageResource(R.drawable.ic_play);
                                playing = false;
                            } else {
                                Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();
                                mediaPlayer.start();
                                playBtnV.setImageResource(R.drawable.ic_pause);
                                playing = true;
                            }


                        }
                    });
                    mp.start();
                }
            });
        } catch (IOException e) {

            e.printStackTrace();
        }

        /* Prepare the MediaPlayer
         * Automatically starts the track once prepared */
        mediaPlayer.prepareAsync();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
        }
    }


}
