package edu.gordon.cs.betago;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/**
 * Created by weiqiuyou on 4/20/16.
 */
public class Play extends AppCompatActivity {

    //final Button button = (Button) findViewById(R.id.button_submit);
    //final BoardView boardView = (BoardView) findViewById(R.id.bview);

    public static MediaPlayer music;
    public static boolean musicOn;
    public static String musicChoice;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        context = Play.this;
        // toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_play);
        setSupportActionBar(myToolbar);
        onButtonClickListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // music
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        musicOn = getPrefs.getBoolean("prefMusic", true);
        musicChoice = getPrefs.getString("prefMusicList", "0");
        switch (musicChoice) {
            case "0":
                music = MediaPlayer.create(Play.this, R.raw.high_mountains_and_running_water);
                break;
            case "1":
                music = MediaPlayer.create(Play.this, R.raw.dreamcatcher);
                break;
            default:
                music = MediaPlayer.create(Play.this, R.raw.high_mountains_and_running_water);
        }
        music.setLooping(true);
        if (musicOn) {

            music.start();
        }
        else
            music.stop();
    }

    @Override
    public void onResume(){
        super.onResume();
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (getPrefs.getBoolean("prefMusic", true)) {
            if (!musicChoice.equals(getPrefs.getString("prefMusicList", "0")) || !musicOn) {
                musicChoice = getPrefs.getString("prefMusicList", "0");
                switch (musicChoice) {
                    case "0":
                        music = MediaPlayer.create(Play.this, R.raw.high_mountains_and_running_water);
                        break;
                    case "1":
                        music = MediaPlayer.create(Play.this, R.raw.dreamcatcher);
                        break;
                    default:
                        music = MediaPlayer.create(Play.this, R.raw.high_mountains_and_running_water);
                }
                music.setLooping(true);
                music.start();
            }
        }
        else
            music.stop();
        musicOn = getPrefs.getBoolean("prefMusic", true);




    }

    public void enableSubmit() {
        Button buttonSubmit = (Button) findViewById(R.id.button_submit);
        buttonSubmit.setEnabled(true);
    }

    public void onButtonClickListener() {
        final Button buttonSubmit = (Button) findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BoardView boardView = (BoardView) findViewById(R.id.bview);
                if (boardView.submit())
                    buttonSubmit.setEnabled(false);
            }
        });
        final Button buttonPass = (Button) findViewById(R.id.button_pass);
        buttonPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(Play.this);
                aBuilder.setMessage("Are you sure you want to pass?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final BoardView boardView = (BoardView) findViewById(R.id.bview);
                                boardView.pass();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = aBuilder.create();
                alert.setTitle("Pass");
                alert.show();
                //final BoardView boardView = (BoardView) findViewById(R.id.bview);
                //int result = boardView.getResult();
                //if (result != 404)
                //BoardView boardView = (BoardView) findViewById(R.id.bview);
                //boardView.pass();
            }
        });
        final Button buttonResign = (Button) findViewById(R.id.button_resign);
        buttonResign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(Play.this);
                aBuilder.setMessage("Are you sure you want to resign?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final BoardView boardView = (BoardView) findViewById(R.id.bview);
                                boardView.resign();
                                buttonSubmit.setEnabled(false);
                                buttonPass.setEnabled(false);
                                buttonResign.setEnabled(false);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = aBuilder.create();
                alert.setTitle("Resign");
                alert.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                Intent i = new Intent(Play.this, Settings.class);
                startActivity(i);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public static void setMusicOn() {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        musicChoice = getPrefs.getString("prefMusicList", "0");
        switch (musicChoice) {
            case "0":
                music = MediaPlayer.create(context, R.raw.high_mountains_and_running_water);
                break;
            case "1":
                music = MediaPlayer.create(context, R.raw.dreamcatcher);
                break;
            default:
                music = MediaPlayer.create(context, R.raw.high_mountains_and_running_water);
        }
        music.setLooping(true);
        music.start();
    }

    public static void setMusicChoice(String newMusicChoice) {
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        musicOn = getPrefs.getBoolean("prefMusic", true);
        if (musicOn) {
            music.stop();
            musicChoice = newMusicChoice;
            System.out.println(musicChoice);
            switch (musicChoice) {
                case "0":
                    music = MediaPlayer.create(context, R.raw.high_mountains_and_running_water);
                    break;
                case "1":
                    music = MediaPlayer.create(context, R.raw.dreamcatcher);
                    break;
                default:
                    music = MediaPlayer.create(context, R.raw.high_mountains_and_running_water);
            }
            Play.music.setLooping(true);
            Play.music.start();
        }
    }

    /*public void onButtonClick(View v) {
        if (v.getId() == R.id.button_submit)
        {
            BoardView boardView = (BoardView) findViewById(R.id.bview);
            boardView.setSubmit(true);
        }
    }*/

}
