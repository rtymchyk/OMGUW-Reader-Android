package com.romantymchyk.omguwreader.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.romantymchyk.omguwreader.R;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    boolean loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        loading = true;
        addPreferencesFromResource(R.xml.pref_about);

        Preference myPref = (Preference) findPreference("pref_about");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                showAboutDialog();
                return true;
            }
        });

        myPref = (Preference) findPreference("pref_help");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                showHelpDialog();
                return true;
            }
        });

        myPref = (Preference) findPreference("pref_licenses");
        myPref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

            public boolean onPreferenceClick(Preference preference) {
                showLicensesDialogOne();
                return true;
            }
        });

        bindPreferenceSummaryToValue(findPreference("pref_post_checker"));
        loading = false;
    }

    public void showAboutDialog() {
        new AlertDialog.Builder(this).setMessage(R.string.about).setTitle("About").setNeutralButton("Done", null).show();
    }

    public void showHelpDialog() {
        new AlertDialog.Builder(this).setMessage(R.string.help).setTitle("Help").setNeutralButton("Done", null).show();
    }

    public void showLicensesDialogOne() {
        new AlertDialog.Builder(this).setMessage(R.string.license_one).setTitle("Cards-UI").setNeutralButton("Done", null).setPositiveButton("Next", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLicensesDialogTwo();
            }
        }).show();
    }

    public void showLicensesDialogTwo() {
        new AlertDialog.Builder(this).setMessage(R.string.license_two).setTitle("Android Action Bar Style Generator").setNeutralButton("Done", null).setPositiveButton("Next", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                showLicensesDialogThree();
            }
        }).show();
    }

    public void showLicensesDialogThree() {
        new AlertDialog.Builder(this).setMessage(R.string.license_three).setTitle("Blogger API Version 3.0").setNeutralButton("Done", null).show();
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getBoolean(preference.getKey(), true));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        return true;
    }

}
