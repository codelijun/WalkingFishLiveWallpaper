package com.example.lijun.walkingfishwallpaperdemo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

public class FishSettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = "FishSettingActivity";

    public static final String TOUCH_ENABLE_KEY = "enable_touch_key";
    public static final String RED_FISH_KEY = "red_fish_number_key";
    public static final String YELLOW_FISH_KEY = "yellow_fish_number_key";
    public static final String BLANK_FISH_KEY = "blank_fish_number_key";

    private SharedPreferences mSharedPreferences;
    private ListPreference mRedFishListPreference;
    private ListPreference mYellowFishListPreference;
    private ListPreference mBlankFishListPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fish_preference);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Preference touchEnablePreference = getPreferenceScreen().findPreference(TOUCH_ENABLE_KEY);
        mRedFishListPreference = (ListPreference) getPreferenceScreen().findPreference(RED_FISH_KEY);
        mYellowFishListPreference = (ListPreference) getPreferenceScreen().findPreference(YELLOW_FISH_KEY);
        mBlankFishListPreference = (ListPreference) getPreferenceScreen().findPreference(BLANK_FISH_KEY);

        mRedFishListPreference.setIcon(R.drawable.red_fish_icon);
        mYellowFishListPreference.setIcon(R.drawable.yellow_fish_icon);
        mBlankFishListPreference.setIcon(R.drawable.blank_fish_icon);

        CharSequence redFishSummary = mRedFishListPreference.getEntry();
        if (!TextUtils.isEmpty(redFishSummary)) {
            mRedFishListPreference.setSummary(redFishSummary);
        }
        CharSequence greenFishSummary = mYellowFishListPreference.getEntry();
        if (!TextUtils.isEmpty(greenFishSummary)) {
            mYellowFishListPreference.setSummary(greenFishSummary);
        }
        CharSequence blueFishSummary = mBlankFishListPreference.getEntry();
        if (!TextUtils.isEmpty(blueFishSummary)) {
            mBlankFishListPreference.setSummary(blueFishSummary);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (DEBUG) {
            Log.d(TAG, " onSharedPreferenceChanged() " + key);
        }
        if (key.equals(TOUCH_ENABLE_KEY)) {

        } else if (key.equals(RED_FISH_KEY)) {
            mRedFishListPreference.setSummary(mRedFishListPreference.getEntry());
        } else if (key.equals(YELLOW_FISH_KEY)) {
            mYellowFishListPreference.setSummary(mYellowFishListPreference.getEntry());
        } else if (key.equals(BLANK_FISH_KEY)) {
            mBlankFishListPreference.setSummary(mBlankFishListPreference.getEntry());
        } else {

        }
    }
}
