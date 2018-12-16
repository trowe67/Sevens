/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/ 
package com.tony.sevens;

import android.content.SharedPreferences;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import au.tonyrowe.sevens.R;


public class Options {

  public Options(final Sevens sevens, final DrawMaster drawMaster) {
 
    sevens.setContentView(R.layout.options);
    View view = (View) sevens.findViewById(R.id.options_view);
    view.setFocusable(true);
    view.setFocusableInTouchMode(true);

    // Display stuff
    final boolean bigCards = sevens.GetSettings().getBoolean("DisplayBigCards", false);
    ((RadioButton)sevens.findViewById(R.id.normal_cards)).setChecked(!bigCards);
    ((RadioButton)sevens.findViewById(R.id.big_cards)).setChecked(bigCards);

    final boolean displayPlayableCards = sevens.GetSettings().getBoolean("DisplayPlayableCards", true);
    ((CheckBox)sevens.findViewById(R.id.display_cards_playable)).setChecked(displayPlayableCards);

    final boolean autoKnock = sevens.GetSettings().getBoolean("AutoKnock", true);
    ((CheckBox)sevens.findViewById(R.id.auto_knock)).setChecked(autoKnock);
    
    final int iPlaySpeed = sevens.GetSettings().getInt("PlaySpeed", 0);
    if (iPlaySpeed == 0)
    	((RadioButton)sevens.findViewById(R.id.play_fast)).setChecked(true);
    if (iPlaySpeed == 1)
    	((RadioButton)sevens.findViewById(R.id.play_medium)).setChecked(true); 
    if (iPlaySpeed == 2)
    	((RadioButton)sevens.findViewById(R.id.play_slow)).setChecked(true); 
    
    final boolean playSounds = sevens.GetSettings().getBoolean("PlaySounds", false);
    ((CheckBox)sevens.findViewById(R.id.play_sounds)).setChecked(playSounds);    
        
    final Button accept = (Button) sevens.findViewById(R.id.button_accept);
    accept.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        boolean commit = false;
        boolean newGame = false;
        SharedPreferences.Editor editor = sevens.GetSettings().edit();

        if (bigCards != ((RadioButton)sevens.findViewById(R.id.big_cards)).isChecked()) {
          editor.putBoolean("DisplayBigCards", !bigCards);
          commit = true;
          drawMaster.DrawCards(!bigCards);
        }
        
        if (displayPlayableCards != ((CheckBox)sevens.findViewById(R.id.display_cards_playable)).isChecked()) {
            editor.putBoolean("DisplayPlayableCards", !displayPlayableCards);
            commit = true;
            drawMaster.SetShowPlayable(!displayPlayableCards);
        }        

        if (autoKnock != ((CheckBox)sevens.findViewById(R.id.auto_knock)).isChecked()) {
            editor.putBoolean("AutoKnock", !autoKnock);
            commit = true;
        } 
        
        if (playSounds != ((CheckBox)sevens.findViewById(R.id.play_sounds)).isChecked()) {
            editor.putBoolean("PlaySounds", !playSounds);
            commit = true;
        }         
        
        if ((iPlaySpeed == 0 && !((RadioButton)sevens.findViewById(R.id.play_fast)).isChecked())||
        		(iPlaySpeed == 1 && !((RadioButton)sevens.findViewById(R.id.play_medium)).isChecked()) ||
        		(iPlaySpeed == 2 && !((RadioButton)sevens.findViewById(R.id.play_slow)).isChecked())){
        	    int iSpeed = 0;
        		if (((RadioButton)sevens.findViewById(R.id.play_fast)).isChecked())
        			iSpeed = 0;
           		if (((RadioButton)sevens.findViewById(R.id.play_medium)).isChecked())
        			iSpeed = 1; 
           		if (((RadioButton)sevens.findViewById(R.id.play_slow)).isChecked())
        			iSpeed = 2;
                editor.putInt("PlaySpeed", iSpeed);
                commit = true;	 		
    	}

        if (commit) {
          editor.commit();
          sevens.RefreshOptions();
        }
        if (newGame) {
          sevens.NewOptions();
        } else {
          sevens.CancelOptions();
        }
      }
    });
    final Button decline = (Button) sevens.findViewById(R.id.button_cancel);
    decline.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        sevens.CancelOptions();
      }
    });

    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {
        switch (keyCode) {
          case KeyEvent.KEYCODE_BACK:
          case KeyEvent.KEYCODE_HOME:
            sevens.CancelOptions();
            return true;
        }
        return false;
      }
    });
    view.requestFocus();
  }
}

