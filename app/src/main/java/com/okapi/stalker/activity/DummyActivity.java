package com.okapi.stalker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.okapi.stalker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 * Created by burak on 5/16/2017.
 */

public class DummyActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new HTMLTask(this).execute();
    }

    private class HTMLTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog;
        public HTMLTask(Activity activity){
            dialog = new ProgressDialog(activity);
        }
        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intent = new Intent(getBaseContext(), BusScheduleActivity.class);
            intent.putExtra("html", aVoid);
            startActivity(intent);
            finish();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.executing));
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            Document doc;
            try {
                doc  = Jsoup.connect("https://www.etu.edu.tr/tr/ulasim")
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            doc = new Cleaner(Whitelist.relaxed()).clean(doc);
            return doc.toString();
        }
    }
}
