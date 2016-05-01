package edu.gordon.cs.betago;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_play);
        setSupportActionBar(myToolbar);
        onButtonClickListener();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onButtonClickListener() {
        final Button buttonSubmit = (Button) findViewById(R.id.button_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BoardView boardView = (BoardView) findViewById(R.id.bview);
                boardView.submit();
            }
        });
        final Button buttonPass = (Button) findViewById(R.id.button_pass);
        buttonPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(Play.this);
                aBuilder.setMessage("Are you sure you want to pass?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final BoardView boardView = (BoardView) findViewById(R.id.bview);
                                boardView.pass();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = aBuilder.create();
                alert.setTitle("Pass");
                alert.show();
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
                buttonSubmit.setEnabled(false);
                buttonPass.setEnabled(false);
                buttonResign.setEnabled(false);
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

    /*public void onButtonClick(View v) {
        if (v.getId() == R.id.button_submit)
        {
            BoardView boardView = (BoardView) findViewById(R.id.bview);
            boardView.setSubmit(true);
        }
    }*/

}
