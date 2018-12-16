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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import au.tonyrowe.sevens.R;


public class Stats {

  public Stats(final Sevens sevens, final SevensView view, final boolean bGameOver) {
  
    sevens.setContentView(R.layout.stats);
    View statsView = (View) sevens.findViewById(R.id.stats_view);
    statsView.setFocusable(true);
    statsView.setFocusableInTouchMode(true);

    Rules rules = view.GetRules();
    //final SharedPreferences settings = sevens.GetSettings();
    final String Player1String = ((NormalSevens)rules).GetPlayer1();
    final String Player2String = ((NormalSevens)rules).GetPlayer2();
    final String Player3String = ((NormalSevens)rules).GetPlayer3();
    final String Player4String = ((NormalSevens)rules).GetPlayer4();
 
    final int Player1Score = ((NormalSevens)rules).GetPlayer1Score();
    final int Player2Score = ((NormalSevens)rules).GetPlayer2Score();
    final int Player3Score = ((NormalSevens)rules).GetPlayer3Score();
    final int Player4Score = ((NormalSevens)rules).GetPlayer4Score();
    
    final int Player1Total = ((NormalSevens)rules).GetPlayer1Total();
    final int Player2Total = ((NormalSevens)rules).GetPlayer2Total();
    final int Player3Total = ((NormalSevens)rules).GetPlayer3Total();
    final int Player4Total = ((NormalSevens)rules).GetPlayer4Total(); 

    TextView tv = (TextView)sevens.findViewById(R.id.text_title);
    tv.setText((view.getContext().getResources()).getText(R.string.menu_stats) + "\n");
    tv = (TextView)sevens.findViewById(R.id.text_player1);
    tv.setText(Player1String);
    tv = (TextView)sevens.findViewById(R.id.text_player1_score);
    tv.setText("" + Player1Score);
    tv = (TextView)sevens.findViewById(R.id.text_player1_total);
    tv.setText("" +Player1Total);
    
    tv = (TextView)sevens.findViewById(R.id.text_player2);
    tv.setText(Player2String);
    tv = (TextView)sevens.findViewById(R.id.text_player2_score);
    tv.setText("" +Player2Score);
    tv = (TextView)sevens.findViewById(R.id.text_player2_total);
    tv.setText("" +Player2Total); 
    
    tv = (TextView)sevens.findViewById(R.id.text_player3);
    tv.setText(Player3String);
    tv = (TextView)sevens.findViewById(R.id.text_player3_score);
    tv.setText("" +Player3Score);
    tv = (TextView)sevens.findViewById(R.id.text_player3_total);
    tv.setText("" +Player3Total); 
    
    tv = (TextView)sevens.findViewById(R.id.text_player4);
    tv.setText(Player4String);
    tv = (TextView)sevens.findViewById(R.id.text_player4_score);
    tv.setText("" + Player4Score);
    tv = (TextView)sevens.findViewById(R.id.text_player4_total);
    tv.setText("" + Player4Total); 
    
    if (bGameOver){
	  	AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
	  	builder.setTitle("Game Over");
	  	String strMessage = Player1String + (Player1String == "You" ? " Win!!!":" Wins!!!");
	  	strMessage += "\n";
	  	strMessage += Player4String + (Player4String == "You" ? " have reached the losing score." : " has reached the losing score.");

	    if (view.GetSettings().getBoolean("PlaySounds", false)){

    		AudioManager audioManager = (AudioManager) view.getContext().getSystemService( view.getContext().AUDIO_SERVICE);
    		float actualVolume = (float) audioManager
    				.getStreamVolume(AudioManager.STREAM_MUSIC);
    		float maxVolume = (float) audioManager
    				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    		float volume = actualVolume / maxVolume;			    		
    		((Sevens)view.getContext()).soundPool.play(((Sevens)view.getContext()).soundGameOver, volume, volume, 1, 0, 1f);	    	    			

	    } 	  	
	  	builder.setMessage(strMessage);
			builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	               // User clicked OK button
	        	 
	           }
	       });
			AlertDialog alert = builder.create();
			alert.show();
    }


    final Button accept = (Button) sevens.findViewById(R.id.button_accept);
    accept.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        sevens.CancelOptions();

        if (!view.mbDealDone)
        	view.InitGame(1, bGameOver, ((NormalSevens)view.GetRules()).GetPlayers());
      }
    });
 
    statsView.setOnKeyListener(new View.OnKeyListener() {
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
    statsView.requestFocus();
  }
}

