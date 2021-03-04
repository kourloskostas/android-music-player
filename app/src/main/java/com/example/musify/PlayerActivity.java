package com.example.musify;

import android.content.ContentUris;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    ImageButton playBtnV;
    static MediaPlayer mediaPlayer;
    ImageButton previousBtnV;
    ImageButton nextBtnV;
    TextView trackNameV;
    TextView artistNameV;
    TextView current_timeV;
    TextView total_timeV;
    SeekBar seekBarV;
    ImageView artWorkV;

    boolean playing;
    int position;
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

        release();
        initV(); /* Initialize Views */
        // Get track EXTRA and set views , titles etc
        Track track = (Track) getIntent().getSerializableExtra("TRACK");
        position = getIntent().getIntExtra("POSITION", 1);

        Toast.makeText(getApplicationContext(), track.getArtworkPath(), Toast.LENGTH_SHORT).show();

        // Play selected track
        playTrack(track);


    }

    // Initialize Views
    private void initV() {

        artWorkV = findViewById(R.id.artWork);
        trackNameV = findViewById(R.id.title);
        artistNameV = findViewById(R.id.artist);
        current_timeV = findViewById(R.id.currenttime);
        total_timeV = findViewById(R.id.totaltime);
        seekBarV = findViewById(R.id.seekBar);
        playBtnV = findViewById(R.id.play);
        previousBtnV = findViewById(R.id.previous);
        nextBtnV = findViewById(R.id.next);
    }

    /* Play selected track*/
    private void playTrack(Track track) {

        /* Release previously assigned mediaplayer */
        release();

        trackNameV.setText(track.getTrackName());
        artistNameV.setText(track.getArtistName());
        //if (artworkpath != null) { /* Default Artwork*/}
        artWorkV.setImageURI(Uri.parse(track.getArtworkPath()));

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



        /* Art work on clck-gesture listener
         *          //TODO                       */
        artWorkV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Clicked on Artwork ", Toast.LENGTH_SHORT).show();
            }
        });
        /* Previous Button on click listener TODO FIX INDEX
         *          //TODO                       */
        previousBtnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position--;
                if (position < 0) {
                    position = MainActivity.trackAdapter.getItemCount() - 1;
                }
                playTrack(MainActivity.trackAdapter.getItem(position));

            }
        });
        /* Next Button on click listener TODO FIX INDEX
         *          //TODO
         *                                */


        nextBtnV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (++position > MainActivity.trackAdapter.getItemCount() - 1) {
                    position = 0;
                }
                playTrack(MainActivity.trackAdapter.getItem(position));
            }
        });

    }

    void release() {
        try {
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        //TODO ???
    }


}
