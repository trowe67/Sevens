package com.tony.sevens;


import com.tony.sevens.Stats;
import com.tony.sevens.Options;
import com.tony.sevens.SevensView;
import com.tony.sevens.Rules;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.support.v7.app.ActionBarActivity;
import com.tony.sevens.Preferences;
import au.tonyrowe.sevens.R;

//Base activity class.
public class Sevens extends ActionBarActivity {
	  private static final int MENU_NEW_GAME  = 1;
	  private static final int MENU_OPTIONS   = 2;
	  private static final int MENU_SAVE_QUIT = 3;
	  private static final int MENU_QUIT      = 4;
	  private static final int MENU_STATS     = 5;
	  private static final int MENU_HELP      = 6;	
	  private static final int MENU_ABOUT  	  = 7;
	  
	  // View extracted from main.xml.
	  private View mMainView;
	  private SevensView mSevensView;
	  private SharedPreferences mSettings;
	  public SoundPool soundPool;
	  public int soundID;
	  public int soundGameOver;
	  private boolean mDoSave;	

	  private SharedPreferences.OnSharedPreferenceChangeListener listener;
	  	  
	  // Shared preferences are where the various user settings are stored.
	  public SharedPreferences GetSettings() { return mSettings; } 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDoSave = true;
        // Force landscape and no title for extra room
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   
        // If the user has never accepted the EULA show it again.
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceManager.setDefaultValues(this, R.xml.visualpreferences, false);        
        //mSettings = getSharedPreferences("SevensPreferences", 0);        
        setContentView(R.layout.main);
        mMainView = findViewById(R.id.main_view);
        mSevensView = (SevensView) findViewById(R.id.sevens);
        getSupportActionBar().show();
        getSupportActionBar().setLogo(R.drawable.app_icon);
        mSevensView.SetTextView((TextView) findViewById(R.id.text));
		// Set the hardware buttons to control the music
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		// Load the sound
		soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0); 
		soundID = soundPool.load(this, R.raw.knock, 1);	
		soundGameOver = soundPool.load(this, R.raw.gamesuccess, 1);
		
	       // register preference change listener 
        //mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() { 
       	  public void onSharedPreferenceChanged(SharedPreferences prefs, String key) { 
       	    // Implementation
 
       	  } 
       	}; 
       	mSettings.registerOnSharedPreferenceChangeListener(listener);		
    }
 
    @Override
    public void onStart() {
      super.onStart();
      if (mSettings.getBoolean("SevensSaveValid", false)) {
        SharedPreferences.Editor editor = GetSettings().edit();
        editor.putBoolean("SevensSaveValid", false);
        editor.commit();
        // If save is corrupt, just start a new game.
        if (mSevensView.LoadSave()) {
          HelpSplashScreen();
          return;
        }
      }

      mSevensView.InitGame(mSettings.getInt("LastType", Rules.SEVENS), true, null);
      HelpSplashScreen();
    }

    // Force show the help if this is the first time played. Sadly no one reads
    // it anyways.
    private void HelpSplashScreen() {
      if (!mSettings.getBoolean("PlayedBefore", false)) {
        mSevensView.DisplayHelp();
      }
    }  
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);

      SubMenu subMenu = menu.addSubMenu(0, MENU_NEW_GAME, 0, R.string.menu_newgame);
      menu.add(0, MENU_OPTIONS, 0, R.string.menu_options);
      menu.add(0, MENU_SAVE_QUIT, 0, R.string.menu_save_quit);
      menu.add(0, MENU_QUIT, 0, R.string.menu_quit);
      menu.add(0, MENU_STATS, 0, R.string.menu_stats);
      menu.add(0, MENU_HELP, 0, R.string.menu_help);
      menu.add(0, MENU_ABOUT, 0, R.string.menu_about);
      return true;
    }  
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      Intent PrefIntent;
      switch (item.getItemId()) {

        case MENU_NEW_GAME:
          mSevensView.InitGame(Rules.SEVENS, true, null);
          break;
        case MENU_STATS:
          DisplayScore(false);
          break;
        case MENU_OPTIONS:
          //DisplayOptions();
        	onPause();
        	PrefIntent = new Intent(this, Preferences.class); 
            startActivity(PrefIntent);        	
          break;
        case MENU_HELP:
          mSevensView.DisplayHelp();
          break;
        case MENU_SAVE_QUIT:
          mSevensView.SaveGame();
          mDoSave = false;
          finish();
          break;
        case MENU_QUIT:
          mDoSave = false;
          finish();
          break;
        case MENU_ABOUT:
		  	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		  	builder.setTitle("About Sevens");
		  	String strMessage = "Developed by A R C Rowe Software";
		  	strMessage += "\n";
		  	strMessage += "email: trowe67@gmail.com";
		  	strMessage += "\n";
		  	strMessage += "\n";
		  	strMessage += "Vectorized Playing Cards 1.3- http://code.google.com/p/vectorized-playing-cards";
		  	strMessage += "\n";
		  	strMessage += "Licensed under LGPL 3 - www.gnu.org/copyleft/lesser.html";
		  	builder.setMessage(strMessage);
  			builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // User clicked OK button
		        	 
		           }
		       });
  			AlertDialog alert = builder.create();
  			alert.show();
  			
  			return true;          
      }

      return false;
    }  
    
    @Override
    protected void onPause() {
      super.onPause();
      if (mDoSave) {
          mSevensView.SaveGame();
        }
      mSevensView.onPause();
    }

    @Override
    protected void onStop() {
      super.onStop();
      if (mDoSave) {
        mSevensView.SaveGame();
      }
    }

    @Override
    protected void onResume() {
      super.onResume();
      mSevensView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
      super.onSaveInstanceState(outState);
    }   
    
    public void DisplayOptions() {

        new Options(this, mSevensView.GetDrawMaster());
      }
    public void DisplayScore(boolean bGameOver) {
        
        new Stats(this, mSevensView, bGameOver);
      }   
    // This is called for option changes that require a refresh, but not a new game
    public void RefreshOptions() {
      setContentView(mMainView);
      mSevensView.RefreshOptions();
    } 
    public void CancelOptions() {
        setContentView(mMainView);
        mSevensView.requestFocus();
        
      }

      public void NewOptions() {
        setContentView(mMainView);
        mSevensView.InitGame(mSettings.getInt("LastType", Rules.SEVENS), false, null);
      }    
}