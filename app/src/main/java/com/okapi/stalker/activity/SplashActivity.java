package com.okapi.stalker.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.Stash;

import java.io.IOException;

public class SplashActivity extends Activity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new SetStash().execute();
    }

    /**
     * Async Task to set stash
     */
    private class SetStash extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            AssetManager am = getAssets();
            try {
                Stash.set(am.open("stash.bin"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Intent intent;
            if (sharedPreferences.contains("id")) {
                intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("key", sharedPreferences.getString("id", ""));
            }else{
                intent = new Intent(getBaseContext(), LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }

    }
}
