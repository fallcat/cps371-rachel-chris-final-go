package edu.gordon.cs.betago;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.widget.Toolbar;
import android.widget.Switch;

/**
 * Created by weiqiuyou on 4/21/16.
 */
public class Settings extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    @SuppressWarnings( "deprecation" )
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_settings);

        //SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //boolean musicOn = getPrefs.getBoolean("prefMusic", true);
        //String musicChoice = getPrefs.getString("prefMusicList", "0");

        SwitchPreference prefMusic = (SwitchPreference) findPreference("prefMusic");
        ListPreference prefMusicList = (ListPreference) findPreference("prefMusicList");

        prefMusic.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (!(boolean)newValue)
                    Play.music.stop();
                else {
                    Play.setMusicOn();
                }
                return true;
            }
        });

        prefMusicList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Play.setMusicChoice((String) newValue);
                return true;
            }
        });
        /*buttonSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                BoardView boardView = (BoardView) findViewById(R.id.bview);
                boardView.submit();
            }
        });*/
    }

    @SuppressWarnings("deprecation")
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) pref;
            pref.setSummary(editTextPref.getText());
        }
    }

}
