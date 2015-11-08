package com.neykov.podcastportal.view.settings;

import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

import com.neykov.podcastportal.R;
import com.neykov.podcastportal.model.subscriptions.SubscriptionsManager;
import com.neykov.podcastportal.view.base.DependencyResolverProvider;

public class SettingsFragment extends PreferenceFragmentCompat {

    private SubscriptionsManager mSubscriptionsManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        mSubscriptionsManager = ((DependencyResolverProvider)getActivity().getApplicationContext())
                .getDependencyResolver()
                .getModelComponent()
                .getSubscriptionsManager();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        getPreferenceManager().setSharedPreferencesName(getString(R.string.pref_filename));
        addPreferencesFromResource(R.xml.pref_general);

        Preference syncFrequencyPreference = getPreferenceManager().findPreference(getString(R.string.pref_key_sync_frequency));
        setDefaultPreferenceValue(syncFrequencyPreference);
        syncFrequencyPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            String stringValue = (String) newValue;
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);

            // Set the summary to reflect the new value.
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            mSubscriptionsManager.schedulePeriodicSync(Integer.parseInt(stringValue));
            return true;
        });

        Preference syncEnabledPreference = getPreferenceManager().findPreference(getString(R.string.pref_key_sync_enabled));
        syncEnabledPreference.setDefaultValue(mSubscriptionsManager.isAutomaticSyncEnabled());
        syncEnabledPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            mSubscriptionsManager.setSyncAutomatically((Boolean) newValue);
            return true;
        });
    }

    private void setDefaultPreferenceValue(Preference preference) {
        preference.setDefaultValue(PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));

    }
}
