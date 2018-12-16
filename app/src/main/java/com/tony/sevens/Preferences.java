package com.tony.sevens;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.widget.Toast;
import au.tonyrowe.sevens.R;

public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.visualpreferences);
        for(int i=0;i<getPreferenceScreen().getPreferenceCount();i++){ 
            initSummary(getPreferenceScreen().getPreference(i)); 
           }		
		}
	    @Override 
	    protected void onResume() { 
	        super.onResume(); 	 
	        // Set up a listener whenever a key changes             
	        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this); 
	    } 
	 
	    @Override 
	    protected void onPause() { 
	        super.onPause(); 
	 
	        // Unregister the listener whenever a key changes             
	        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);     
	    } 
	 
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) { 
	    	   Preference pref = findPreference(key); 
	    	   
	    	    if (pref instanceof ListPreference) { 
	    	        ListPreference listPref = (ListPreference) pref; 
	    	        pref.setSummary(listPref.getEntry()); 
	    	    } 
	    } 
		
	     private void initSummary(Preference p){ 
	           if (p instanceof PreferenceCategory){ 
	                PreferenceCategory pCat = (PreferenceCategory)p; 
	                for(int i=0;i<pCat.getPreferenceCount();i++){ 
	                    initSummary(pCat.getPreference(i)); 
	                } 
	            }else{ 
	                updatePrefSummary(p); 
	            } 
	 
	        } 
	 
	        private void updatePrefSummary(Preference p){ 
	            if (p instanceof ListPreference) { 
	                ListPreference listPref = (ListPreference) p;  
	                p.setSummary(listPref.getEntry());  
	            } 
	            if (p instanceof EditTextPreference) { 
	                EditTextPreference editTextPref = (EditTextPreference) p;  
	                p.setSummary(editTextPref.getText());  
	            } 
	 
	        } 
		
		
}
