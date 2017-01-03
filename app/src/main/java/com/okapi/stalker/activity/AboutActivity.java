package com.okapi.stalker.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.aboutlibraries.ui.LibsActivity;
import com.okapi.stalker.R;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by burak on 9/30/2016.
 */
public class AboutActivity extends LibsActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Integer db_version = sharedPreferences.getInt("db_version", 1);
        LibsBuilder builder = new LibsBuilder()
                .withAboutSpecial2Description(getString(R.string.about_current_db) + db_version)
                .withActivityTitle(getString(R.string.about))
                .withLibraries("roundedletterview", "material-intro-screen", "searchablespinnerlibrary", "Jsoup")
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withLibraryComparator(new LibraryComparator());

        setIntent(builder.intent(this));
        super.onCreate(savedInstanceState);
    }


    private static class LibraryComparator implements Comparator<Library>, Serializable {

        @Override
        public int compare(Library lhs, Library rhs) {

            return lhs.getLibraryName().compareTo(rhs.getLibraryName());
        }
    }
}
