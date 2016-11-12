/*
 * (C) Copyright 2015-2016 by fr3ts0n <erwin.scheuch-heilig@gmx.at>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 *
 */

package com.fr3ts0n.stagefever;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsActivity
        extends PreferenceActivity
{
    /**
     * app preferences
     */
    static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefsFragment())
                .commit();
    }


    public static class PrefsFragment
            extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings);
            // add handler for selection update
            prefs.registerOnSharedPreferenceChangeListener(this);
        }

        void setValueAsSummary(Preference pref)
        {
            if (pref instanceof ListPreference)
            {
                ListPreference currPref = (ListPreference) pref;
                currPref.setSummary(currPref.getValue());
            }
            else
            if (pref instanceof EditTextPreference)
            {
                EditTextPreference currPref = (EditTextPreference) pref;
                currPref.setSummary(currPref.getText());
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            Preference pref = findPreference(key);
            setValueAsSummary(pref);
        }
    }
}
