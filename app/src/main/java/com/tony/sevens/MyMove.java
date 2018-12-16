package com.tony.sevens;

import android.os.Handler;
import android.widget.Toast;


public class MyMove implements Runnable{
	  private SevensView mView;
	  private AnimateCard mAnimateCard;
	  private CardAnchor mCardAnchor;
	  private boolean mIsPlaying;
	  private Card mCard;
	  private int miAnchor;
	  
	  public MyMove(SevensView view, AnimateCard animateCard) {
		    mView = view;
		    mAnimateCard = animateCard;
		    mIsPlaying = false;
		    miAnchor = CardAnchor.SOUTH;
		  }	  
	  public void StartMove(Card card, CardAnchor cardAnchor) {
		  
		    mCard = card;
		    mView.DrawBoard(); 
		    mIsPlaying = true;
		    miAnchor = CardAnchor.SOUTH;
		    mView.mCardAnchor[miAnchor].mbKnock = false;
		    mCardAnchor = cardAnchor;
		    Play();
		    //PlayNext();
		  }	 
	  
	  public void Play() {
	     if (!mIsPlaying || miAnchor > CardAnchor.SOUTH) {
	        mIsPlaying = false;
	        mView.StopAnimating();
	        //mCardAnchor.AddCard(mCard);
	        mView.miCurrentPlayer = 0;
	        mView.RepositionHands();
			if (mView.mCardAnchor[CardAnchor.SOUTH].GetCount() == 0){
		    	String text = "You win hand!";
		    	Toast.makeText(mView.getContext(), text, Toast.LENGTH_LONG).show();
		    	mView.mGameStarted = false;
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
		    	
			}	
			else
				mView.PlayGame();
	        return;
	      }	
		 mAnimateCard.MoveCard(mCard, mCardAnchor, this);	
		 miAnchor++;
		  
	  }
	  public void run() {
		    if (mIsPlaying) {
		    	   // SLEEP 1 SECONDS HERE ... 
		        //Handler handler = new Handler();  
		        //handler.postDelayed(new Runnable() {  
		             //public void run() {  
		            	 Play();  
		        //     }  
		        //}, 1000);  
		      
		      
		    }
		  }	  
}
