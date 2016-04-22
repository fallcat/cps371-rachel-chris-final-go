package edu.gordon.cs.betago;

import android.app.Activity;
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
        /*final Button button = (Button) findViewById(R.id.button_submit);
        final BoardView boardView = (BoardView) findViewById(R.id.bview);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boardView.setSubmit(true);
            }
        });*/
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
