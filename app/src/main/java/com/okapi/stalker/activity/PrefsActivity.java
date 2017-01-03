package com.okapi.stalker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.eggheadgames.siren.Siren;
import com.okapi.stalker.R;
import com.okapi.stalker.service.CourseNotificationService;

/**
 * Created by burak on 6/18/2016.
 */
public class PrefsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String TAG = PrefsActivity.class.getSimpleName();
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle(getString(R.string.preferences));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.relativeForPrefs, new MyPreferenceFragment()).commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e(TAG, "SharedPreference changed: " + key);
        if(key.equals("checkbox_notification_allow") || key.equals("edittext_notification_x_minute")){
            startService(new Intent(this, CourseNotificationService.class));
        }

    }
    public static class MyPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener((PrefsActivity)getActivity());
            updateSummaries();
            findPreference("edittext_notification_x_minute").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(Integer.parseInt(newValue.toString()) > 60){
                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.more_than_60_minutes), Toast.LENGTH_LONG).show();
                        return false;
                    }else if(Integer.parseInt(newValue.toString()) < 0) {
                        Toast.makeText(getActivity().getBaseContext(), getString(R.string.less_than_0_minute), Toast.LENGTH_LONG).show();
                        return false;
                    }
                    preference.setSummary(getString(R.string.minutes, newValue.toString()));
                    return true;
                }
            });
            findPreference("list_auto_update_check_cycle").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if(newValue.toString().equals("0")){
                        preference.setSummary(getString(R.string.at_every_opening));
                    }else{
                        preference.setSummary(getString(R.string.days, newValue.toString()));
                    }
                    Siren siren = Siren.getInstance(getActivity().getApplicationContext());
                    siren.checkVersion(getActivity(), Integer.parseInt(newValue.toString()), MainActivity.SIREN_JSON_URL);
                    return true;
                }
            });

        }
        public void updateSummaries(){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
            findPreference("edittext_notification_x_minute").setSummary(getString(R.string.minutes, sharedPreferences.getString("edittext_notification_x_minute", "10")));
            findPreference("list_auto_update_check_cycle").setSummary(getString(R.string.days, sharedPreferences.getString("list_auto_update_check_cycle", "1")));
        }
    }
}