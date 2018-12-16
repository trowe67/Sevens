package com.tony.sevens;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
//import android.util.Log;
import android.widget.Toast;
import android.media.AudioManager;
import au.tonyrowe.sevens.R;

public class OpponentMoves implements Runnable{

	  private SevensView mView;
	  private AnimateCard mAnimateCard;
	  private CardAnchor mCardAnchor;
	  private boolean mIsPlaying;
	  private Card mCard;
	  private Card mPrevCard;
	  private int miAnchor;
	  
	  public OpponentMoves(SevensView view, AnimateCard animateCard) {
		    mView = view;
		    mAnimateCard = animateCard;
		    mIsPlaying = false;
		    mPrevCard = null;
		  }	  
	  public void StartMoves(Card card, int iAnchor) {
		  
		    mCard = card;
		    mView.DrawBoard(); 
		    mIsPlaying = true;
		    mPrevCard = null;
		    miAnchor = iAnchor;
		    mView.mCardAnchor[iAnchor].mbKnock = false;
		    String sSpeed = mView.GetSettings().getString("PlaySpeed", "0");// 0 = fast, 2 = medium, 3 = slow
		    int iSpeed = Integer.parseInt(sSpeed);		    
		   
	        Handler handler = new Handler();  
	        handler.postDelayed(new Runnable() {  
	             public void run() {  
	            	 PlayNext();  
	             }  
	        }, 1000 + iSpeed*1500);  		    
		    //PlayNext();
		  }	  
	  
	  
	  public void PlayNext() {
		    if (mIsPlaying && mCardAnchor != null && mPrevCard != null && mAnimateCard.mbFinished == false){
		    	mCardAnchor.AddCard(mPrevCard);
		    	//Log.i(""," Add card FIX " + (mPrevCard.GetValue())  + " , " +mPrevCard.GetSuit());
		    	mPrevCard = null;
		    }
		    if (!mIsPlaying || miAnchor >= CardAnchor.SOUTH) {
			  	Card[] playableCards;
			  	playableCards = mView.GetRules().GetPlayableCards();
			  	if (playableCards[0] == null)
			  		playableCards[0] = new Card(7,Card.HEARTS);
			  	Card[] MyCards;
			  	MyCards = mView.mCardAnchor[CardAnchor.SOUTH].GetCards();
			  	for (int i = 0; i < 4; i++)
			  		mView.mCardAnchor[i].mbKnock = false;
		  		for (int j = 0; j < MyCards.length; j++){
		  			if (MyCards[j] != null){
		  				MyCards[j].mbPlayable = false;
		  			}
		  		}			  	
			  	
			  	mView.mbPlayable = false;
			  	for (int i = 0; i < playableCards.length; i++){
			  		for (int j = 0; j < MyCards.length; j++){
			  			if (playableCards[i] != null && MyCards[j] != null && playableCards[i].Equals(MyCards[j])){
			  				//mView.mSelectCard.AddCard(MyCards[j]);
			  				MyCards[j].mbPlayable = true;
			  				mView.mbPlayable = true;
			  			}
			  		}
			  		
			  	}		     
		     
		     
		      if (mView.mbPlayable){
		    	  mView.StopAnimating(); 
		    	  mCard = null;
				    if (!mView.mGameStarted){ // Prompt to start game
				    	String text = "You have the start card - seven of hearts!";
				    	Toast.makeText(mView.getContext(), text, Toast.LENGTH_LONG).show();			    	
				    }	
				    mView.mbDrawYourTurn = true;
		    	  return;
		      }
		      else {
		    	  miAnchor=0;
		    	  mView.miCurrentPlayer = miAnchor;	
				  	MyCards = mView.mCardAnchor[CardAnchor.SOUTH].GetCards();		    	  
			  		for (int j = 0; j < MyCards.length; j++){
			  			if (MyCards[j] != null){
			  				MyCards[j].mbPlayable = false;
			  			}
			  		}		    	  
		    	  mCard = null;
		    	  mView.mbDrawYourTurn = true;
		    	  if (mView.mGameStarted){
		    		  String text = "You are knocking!";
				      if ((mView.GetSettings().getBoolean("AutoKnock", true))){				    	  
				    	  Toast.makeText(mView.getContext(), text, Toast.LENGTH_SHORT).show();	
				    		// Play knocking sound perhaps
				    	  if ((mView.GetSettings().getBoolean("PlaySounds", false))){
				    		AudioManager audioManager = (AudioManager) mView.getContext().getSystemService( mView.getContext().AUDIO_SERVICE);
				    		float actualVolume = (float) audioManager
				    				.getStreamVolume(AudioManager.STREAM_MUSIC);
				    		float maxVolume = (float) audioManager
				    				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				    		float volume = actualVolume / maxVolume;			    		
				    		((Sevens)mView.getContext()).soundPool.play(((Sevens)mView.getContext()).soundID, volume, volume, 1, 0, 1f);
				    	  }
				      }
				      else {
				    	  miAnchor=CardAnchor.SOUTH;
				    	  mView.miCurrentPlayer = miAnchor;	
				    	  AlertDialog.Builder builder = new AlertDialog.Builder(mView.getContext());
				    	  //AlertDialog alertDialog = new AlertDialog.Builder(mView.getContext()).create();   
				    	  builder.setTitle("Knock");
				    	  builder.setIcon(R.drawable.hand);
				    	  builder.setPositiveButton("Knock", new DialogInterface.OnClickListener() {
				    		  public void onClick(DialogInterface dialog, int whichButton) {
						    	  miAnchor=0;
						    		// Play knocking sound perhaps
						    	  if ((mView.GetSettings().getBoolean("PlaySounds", false))){
						    		AudioManager audioManager = (AudioManager) mView.getContext().getSystemService( mView.getContext().AUDIO_SERVICE);
						    		float actualVolume = (float) audioManager
						    				.getStreamVolume(AudioManager.STREAM_MUSIC);
						    		float maxVolume = (float) audioManager
						    				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
						    		float volume = actualVolume / maxVolume;			    		
						    		((Sevens)mView.getContext()).soundPool.play(((Sevens)mView.getContext()).soundID, volume, volume, 1, 0, 1f);
						    	  }
						    	  mView.miCurrentPlayer = miAnchor;					    			  
				    			  run();
				    			  return;
				    			  
				    			  } 
				    		  });
				    	  AlertDialog alertDialog = builder.create();
				    	  alertDialog.show();
				    	  return;
				      }		    		  
		    		  	
		    	  }
		    	  run();
		    	  return;
		      }
		    }
		    mView.mbDrawYourTurn = false;
		  	Card[] playableCards;
		  	playableCards = mView.GetRules().GetPlayableCards();
		  	if (playableCards[0] == null)
		  		playableCards[0] = new Card(7,Card.HEARTS);		    
		    mCard = null;
		    //if (miAnchor == 0)
		    mCard = mView.mCardAnchor[miAnchor].PlayCard();
		    
		    if (mCard == null){
		    	if (mView.mGameStarted){
		    		mView.mCardAnchor[miAnchor].mbKnock = true;
		    		// Play knocking sound perhaps
		    		if ((mView.GetSettings().getBoolean("PlaySounds", false))){
			    		AudioManager audioManager = (AudioManager) mView.getContext().getSystemService( mView.getContext().AUDIO_SERVICE);
			    		float actualVolume = (float) audioManager
			    				.getStreamVolume(AudioManager.STREAM_MUSIC);
			    		float maxVolume = (float) audioManager
			    				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			    		float volume = actualVolume / maxVolume;			    		
			    		((Sevens)mView.getContext()).soundPool.play(((Sevens)mView.getContext()).soundID, volume, volume, 1, 0, 1f);
		    		}
		    		
		    	}
		    	miAnchor++;	
		    	mView.miCurrentPlayer = miAnchor;	
		    	//mCard = mView.mCardAnchor[miAnchor].PlayCard();
		    	mView.StopAnimating(); 
		    	
		    	run(); 
		    	return;
		    }
		    else {
		    	mView.mGameStarted = true;
		    	mView.mCardAnchor[miAnchor].mbKnock = false;
		    	
		    	
		    }
		    	
		    mView.mCardAnchor[miAnchor].RemoveCard(mCard);

    		switch (mCard.GetSuit()){
    		case Card.HEARTS:
    			if (mCard.GetValue() == 7)
    				mCardAnchor = mView.mCardAnchor[CardAnchor.HEARTS7];
    			if (mCard.GetValue() > 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.HEARTS8];
    			if (mCard.GetValue() < 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.HEARTS6];    				
    			break;

    		case Card.DIAMONDS:
    			if (mCard.GetValue() == 7)
    				mCardAnchor = mView.mCardAnchor[CardAnchor.DIAMONDS7];
    			if (mCard.GetValue() > 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.DIAMONDS8];
    			if (mCard.GetValue() < 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.DIAMONDS6];    				
    			break;  
    			
    		case Card.CLUBS:
    			if (mCard.GetValue() == 7)
    				mCardAnchor = mView.mCardAnchor[CardAnchor.CLUBS7];
    			if (mCard.GetValue() > 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.CLUBS8];
    			if (mCard.GetValue() < 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.CLUBS6];    				
    			break;    			
 
       		case Card.SPADES:
    			if (mCard.GetValue() == 7)
    				mCardAnchor = mView.mCardAnchor[CardAnchor.SPADES7];
    			if (mCard.GetValue() > 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.SPADES8];
    			if (mCard.GetValue() < 7)	
    				mCardAnchor = mView.mCardAnchor[CardAnchor.SPADES6];    				
    			break;   			
    		}
    		  mPrevCard = mCard;
		      mAnimateCard.MoveCard(mCard, mCardAnchor, this);
		      
		      
	    	  if (mView.mCardAnchor[miAnchor].GetCount() == 0){
			    	String text = "";
			    	switch (miAnchor){
			    	case 0:
			    		text = "Player West wins hand!";
			    		break;
			    	case 1:
			    		text = "Player North wins hand!";
			    		break;	
			    	case 2:
			    		text = "Player East wins hand!";
			    		break;	
			    	case 3:
			    		text = "Player South (You) wins hand!";
			    		break;			    		
			    	}
			    	mView.mGameStarted = false;
			    	Toast.makeText(mView.getContext(), text, Toast.LENGTH_SHORT).show();		    		  
			    	mIsPlaying = false;
			    	mView.mCardAnchor[0].SetDisplay(GenericAnchor.DISPLAY_ALL);
			    	mView.mCardAnchor[1].SetDisplay(GenericAnchor.DISPLAY_ALL);
			    	mView.mCardAnchor[2].SetDisplay(GenericAnchor.DISPLAY_ALL);			    	
    		    	mView.mbDealDone = false;
    		    	mView.DrawBoard(); 
    		        Handler handler = new Handler();  
    		        handler.postDelayed(new Runnable() {  
    		             public void run() {  
    		            	 ((NormalSevens)mView.GetRules()).ComputeScores();
    		             }  
    		        }, 5000); 				    	
			    	//((NormalSevens)mView.GetRules()).ComputeScores();
			    	
	    	  }		      
	    	  mView.RepositionHands();
		      miAnchor++;
		      mView.miCurrentPlayer = miAnchor;	
		      	      
		  }	  
	  public void run() {
		    if (mIsPlaying) {
		    	   // SLEEP 1 SECONDS HERE ... 
		    	
			    String sSpeed = mView.GetSettings().getString("PlaySpeed", "0");// 0 = fast, 2 = medium, 3 = slow
			    int iSpeed = Integer.parseInt(sSpeed);			    	
		        Handler handler = new Handler();  
		        handler.postDelayed(new Runnable() {  
		             public void run() {  
		            	 PlayNext();  
		             }  
		        }, 1000 + iSpeed*1500);  
		      //PlayNext();
		      
		    }
		  }
}
