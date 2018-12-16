
package com.tony.sevens;

import android.graphics.Canvas;
import android.util.Log;
import java.lang.Math;
import java.util.Random;

class CardAnchor {

  public static final int MAX_CARDS = 52;
  public static final int SEQ_SINK = 0;
  public static final int SUIT_SEQ_STACK = 0;
  public static final int WEST = 0;
  public static final int NORTH = 1;
  public static final int EAST = 2;
  public static final int SOUTH = 3;
  public static final int HEARTS7 = 4;
  public static final int DIAMONDS7 = 5;
  public static final int CLUBS7 = 6;
  public static final int SPADES7 = 7;
  public static final int HEARTS8 = 8;
  public static final int DIAMONDS8 = 9;
  public static final int CLUBS8 = 10;
  public static final int SPADES8 = 11;
  public static final int HEARTS6 = 12;
  public static final int DIAMONDS6 = 13;
  public static final int CLUBS6 = 14;
  public static final int SPADES6 = 15;
  
  public static final int HARD = 0;
  public static final int EASY = 1;

  private int mNumber;
  protected Rules mRules;
  protected float mX;
  protected float mY;
  protected Card[] mCard;
  protected int mCardCount;
  protected int mHiddenCount;
  protected float mLeftEdge;
  protected float mRightEdge;
  protected float mBottom;
  protected static float mfWidthSF;
  protected static float mfHeightSF;
  protected boolean mDone;
  
  public boolean mbKnock;

  //Variables for GenericAnchor
  protected int mSTARTSEQ;
  protected int mBUILDSEQ;
  protected int mMOVESEQ;
  protected int mBUILDSUIT;
  protected int mMOVESUIT;
  protected boolean mBUILDWRAP;
  protected boolean mMOVEWRAP;
  protected int mDROPOFF;
  protected int mPICKUP;
  protected int mDISPLAY; 
  protected int mHACK;
  
  // ==========================================================================
  // Create a CardAnchor
  // -------------------
  public static CardAnchor CreateAnchor(int type, int number, Rules rules, float fWidthSF, float fHeightSF) {
    CardAnchor ret = null;
    mfWidthSF = fWidthSF;
    mfHeightSF = fHeightSF;
    switch (type) {
      case NORTH:
        ret = new NorthAnchor(mfWidthSF, mfHeightSF);
        break;
      case EAST:
        ret = new EastWestAnchor(mfWidthSF, mfHeightSF);
        break;
      case SOUTH:
        ret = new SouthAnchor(mfWidthSF, mfHeightSF);
        break;
      case WEST:
        ret = new EastWestAnchor(mfWidthSF, mfHeightSF);
        break;
      case SPADES7:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case CLUBS7:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;         
      case DIAMONDS7:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case HEARTS7:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;  
      case SPADES8:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case CLUBS8:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;         
      case DIAMONDS8:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case HEARTS8:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;  
      case SPADES6:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case CLUBS6:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;         
      case DIAMONDS6:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;
      case HEARTS6:
          ret = new DealTo(mfWidthSF, mfHeightSF);
          break;           
    }
    ret.SetRules(rules);
    ret.SetNumber(number);
    return ret;
  }

  public CardAnchor(float fWSF, float fHSF) {
    mX = 1;
    mY = 1;
    mCard = new Card[MAX_CARDS];
    mCardCount = 0;
    mHiddenCount = 0;
    mLeftEdge = -1;
    mRightEdge = -1;
    mBottom = -1;
    mNumber = -1;
    mDone = false;
    mbKnock =false;
    mfWidthSF = fWSF;
    mfHeightSF = fHSF;
  }

  // ==========================================================================
  // Getters and Setters
  // -------------------
  public Card[] GetCards() { return mCard; }
  public int GetCount() { return mCardCount; }
  public int GetHiddenCount() { return mHiddenCount; }
  public float GetLeftEdge() { return mLeftEdge; }
  public int GetNumber() { return mNumber; }
  public float GetRightEdge() { return mRightEdge; }
  public int GetVisibleCount() { return mCardCount - mHiddenCount; }
  public int GetMovableCount() { return mCardCount > 0 ? 1 : 0; }
  public float GetX() { return mX; }
  public float GetNewY() { return mY; }
  public boolean IsDone() { return mDone; }

  public void SetBottom(float edge) { mBottom = edge; }
  public void SetHiddenCount(int count) { mHiddenCount = count; }
  public void SetLeftEdge(float edge) { mLeftEdge = edge; }
  public void SetMaxHeight(int maxHeight) { }
  public void SetNumber(int number) { mNumber = number; }
  public void SetRightEdge(float edge) { mRightEdge = edge; }
  public void SetRules(Rules rules) { mRules = rules; }
  public void SetShowing(int showing) {  }
  protected void SetCardPosition(int idx) { mCard[idx].SetPosition(mX, mY); }
  public void SetDone(boolean done) { mDone = done; }

  //Methods for GenericAnchor
  public void SetStartSeq(int seq){ mSTARTSEQ = seq; }
  public void SetSeq(int seq){ mBUILDSEQ = seq; mMOVESEQ = seq; }
  public void SetBuildSeq(int buildseq){ mBUILDSEQ = buildseq;  }
  public void SetMoveSeq(int moveseq){ mMOVESEQ = moveseq;  }
  
  public void SetWrap(boolean wrap){ mBUILDWRAP = wrap; mMOVEWRAP = wrap; }
  public void SetMoveWrap(boolean movewrap){ mMOVEWRAP = movewrap;  }
  public void SetBuildWrap(boolean buildwrap){ mBUILDWRAP = buildwrap;  }
  
  public void SetSuit(int suit){ mBUILDSUIT = suit; mMOVESUIT = suit; }
  public void SetBuildSuit(int buildsuit){ mBUILDSUIT = buildsuit;  }
  public void SetMoveSuit(int movesuit){ mMOVESUIT = movesuit;  }
  
  public void SetBehavior(int beh){ mDROPOFF = beh; mPICKUP = beh; }
  public void SetDropoff(int dropoff){ mDROPOFF = dropoff;  }
  public void SetPickup(int pickup){ mPICKUP = pickup;  }
  
  public void SetDisplay(int display){ mDISPLAY = display;  }
  
  public void SetHack(int hack){ mHACK = hack; }
  //End Methods for Generic Anchor  
  
  public void SetPosition(float x, float y) {
    mX = x;
    mY = y;
    for (int i = 0; i < mCardCount; i++) {
      SetCardPosition(i);
    }
  }

  // ==========================================================================
  // Functions to add cards
  // ----------------------
  public void AddCard(Card card) {
    mCard[mCardCount++] = card;
    SetCardPosition(mCardCount - 1);
  }

  public void AddMoveCard(MoveCard moveCard) {
    int count = moveCard.GetCount();
    Card[] cards = moveCard.DumpCards();

    for (int i = 0; i < count; i++) {
      AddCard(cards[i]);
    }
  }

  public boolean DropSingleCard(Card card) { return false; }
  public boolean CanDropCard(MoveCard moveCard, int close) { return false; }

  // ==========================================================================
  // Functions to take cards
  // -----------------------
  public Card[] GetCardStack() { return null; }

  public Card GrabCard(float x, float y) {
    Card ret = null;
    if (mCardCount > 0 && IsOverCard(x, y)) {
      ret = PopCard();
    }
    return ret;
  }

  public Card PopCard() {
    Card ret = mCard[--mCardCount];
    mCard[mCardCount] = null;
    return ret;
  }
  
  public void RemoveCard(Card card) {
	int iCount = 0;
  	for (int i = 0; i < mCardCount; i++){
		if (card == mCard[i]){
			mCard[i] = null;
			iCount = i;
			break;
		}
  	}
  	for (int i = iCount; i < mCardCount-1; i++)
			mCard[i] = mCard[i+1];
  	
  	mCardCount--;
  	
  } 

  // ==========================================================================
  // Functions to interact with cards
  // --------------------------------
  public boolean TapCard(float x, float y) { return false; }
  
  public Card SelectCard(float x, float y) { return null; }

  public boolean UnhideTopCard() {
    if (mCardCount  > 0 && mHiddenCount > 0 && mHiddenCount == mCardCount) {
      mHiddenCount--;
      return true;
    }
    return false;
  }
  public boolean ExpandStack(float x, float y) { return false; }
  public boolean CanMoveStack(float x, float y) { return false; }


  // ==========================================================================
  // Functions to check locations
  // ----------------------------
  private boolean IsOver(float x, float y, boolean deck, int close) {
    float clx = mCardCount == 0 ? mX : mCard[mCardCount - 1].GetX();
    float leftX = mLeftEdge == -1 ? clx : mLeftEdge;
    float rightX = mRightEdge == -1 ? clx + Card.WIDTH : mRightEdge;
    float topY = (mCardCount == 0 || deck) ? mY : mCard[mCardCount-1].GetY();
    float botY = mCardCount > 0 ? mCard[mCardCount - 1].GetY() : mY;
    botY += Card.HEIGHT;

    leftX -= close*Card.WIDTH/2;
    rightX += close*Card.WIDTH/2;
    topY -= close*Card.HEIGHT/2;
    botY += close*Card.HEIGHT/2;
    if (mBottom != -1 && botY + 10 >= mBottom)
      botY = mBottom;

    if (x >= leftX && x <= rightX && y >= topY && y <= botY) {
      return true;
    }
    return false;
  }
  

  protected boolean IsOverCard(float x, float y) {
    return IsOver(x, y, false, 0);
  }
  protected boolean IsOverCard(float x, float y, int close) {
    return IsOver(x, y, false, close);
  }

  protected boolean IsOverDeck(float x, float y) {
    return IsOver(x, y, true, 0);
  }
  
  

  // ==========================================================================
  // Functions to Draw
  // ----------------------------
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
    } else {
      drawMaster.DrawCard(canvas, mCard[mCardCount-1]);
    }
  }
  
  // ==========================================================================
  // Function to arrange a hand into suits
  // ----------------------------
  public void ArrangeSuits() {
    if (mCardCount > 0) {
    	Card card;
    	Card orderedCards[];
    	orderedCards = new Card[mCardCount];
    	int iValue;
    	int iOrder[];
    	int iCount = 0;
    	iOrder = new int [mCardCount];
    	int iMinValue = Card.HEARTS * 13 + Card.KING;
    	iOrder[0] = iMinValue;
    	for (int i = 0; i < mCardCount; i++){
    		card = mCard[i];
    		iValue = card.GetSuit()*13 + card.GetValue();
    		iCount = 0;
    		// See where the card value comes in the pecking order
    		while (iValue > iOrder[iCount]){
    			iCount++;
    		}
    		// It goes here, so move everything else along one
    		for (int j = mCardCount-1; j > iCount; j--)
    			iOrder[j] = iOrder[j-1];
    		iOrder[iCount] = iValue;
    	}
    	
    	for (int i = 0; i < mCardCount; i++){
    		for (int j = 0; j < mCardCount; j++){
        		card = mCard[j];
        		iValue = card.GetSuit()*13 + card.GetValue();   			
    			if (iValue == iOrder[i]){
    				orderedCards[i] = mCard[j];
    				break;
    			}
    		}
    	}
    	for (int i = 0; i < mCardCount; i++){
    		mCard[i] = orderedCards[i];
    	}    	
    }
  } 
  
  // ==========================================================================
  // Function to get a suitable card to play
  // ----------------------------
  public Card PlayCard() {
	  Card ret = null;
	  Card card;
	  if (mCardCount > 0) {
		  // To work out what card to play, see what cards can be played
		  	Card[] playableCards;
		  	playableCards = mRules.GetPlayableCards();
		  	Card[] selectedCards;
		  	selectedCards = new Card[8];
		  	int iNumSelectableCards = 0;
		  	for (int i = 0; i < 8; i++)
		  		selectedCards[i] = null;
	    	for (int i = 0; i < mCardCount; i++){
	    		card = mCard[i];
	    		switch (card.GetSuit()){
	    		case Card.HEARTS:
	    			
	    			// Card is a heart, can it be played?
	    			// Is it a seven, it can be played...
	    			if (card.GetValue() == 7){
	    				selectedCards[Rules.HEARTS8] = card;
	    				iNumSelectableCards++;
	    				// and must be played first
	    				playableCards[Rules.HEARTS8] = new Card(8,Card.HEARTS);
	    				playableCards[Rules.HEARTS6] = new Card(6,Card.HEARTS);
	    				playableCards[Rules.CLUBS8] = new Card(7,Card.CLUBS);
	    				playableCards[Rules.DIAMONDS8] = new Card(7,Card.DIAMONDS);
	    				playableCards[Rules.SPADES8] = new Card(7,Card.SPADES);
	 
	    				return card;
	    			}
	    			if (card.GetValue() < 7){
	    				if (card.Equals(playableCards[Rules.HEARTS6])){
	    					selectedCards[Rules.HEARTS6] = card;
	    					iNumSelectableCards++;
	    					//Log.i(""," Playing card " + (card.GetValue())  + " , " +card.GetSuit());
	    					//Log.i(""," Updating playable card " + (card.GetValue()-1)  + " , " +Card.HEARTS);	    					
	    					//playableCards[Rules.HEARTS6] = new Card(card.GetValue()-1,Card.HEARTS);
	    					//return card;
	    				}
	    			}
	    			if (card.GetValue() > 7){
	    				if (card.Equals( playableCards[Rules.HEARTS8])){
	    					selectedCards[Rules.HEARTS8] = card;
	    					iNumSelectableCards++;
	    					//Log.i(""," Playing card " + (card.GetValue())  + " , " +card.GetSuit());
	    					//Log.i(""," Updating playable card " + (card.GetValue()+1)  + " , " +Card.HEARTS);	    					
	    					//playableCards[Rules.HEARTS8] = new Card(card.GetValue()+1,Card.HEARTS);
	    					//return card;
	    				}
	    			}	    			
	    			
	    			break;
	    		case Card.DIAMONDS:
	    			// Is it a seven, it can be played...
	    			if (card.GetValue() == 7){
	    				if (card.Equals(playableCards[Rules.DIAMONDS8])){
	    					selectedCards[Rules.DIAMONDS8] = card;
	    					iNumSelectableCards++;
	    				}
	    			}
	    			
	    			if (card.GetValue() < 7){
	    				if (card.Equals( playableCards[Rules.DIAMONDS6])){
	    					selectedCards[Rules.DIAMONDS6] = card;
	    					iNumSelectableCards++;
	    				}
	    			}
	    			if (card.GetValue() > 7){
	    				if (card.Equals( playableCards[Rules.DIAMONDS8])){
	    					selectedCards[Rules.DIAMONDS8] = card;
	    					iNumSelectableCards++;
	    				}
	    			}		    			
	    			
	    			break;
	    			
	    		case Card.CLUBS:
	    			// Is it a seven, it can be played...
	    			if (card.GetValue() == 7){
	    				if (card.Equals(playableCards[Rules.CLUBS8])){
	    					selectedCards[Rules.CLUBS8] = card;
	    					iNumSelectableCards++;
	    				}	    				
	    			}
	    			
	    			if (card.GetValue() < 7){
	    				if (card.Equals(playableCards[Rules.CLUBS6])){
	    					selectedCards[Rules.CLUBS6] = card;
	    					iNumSelectableCards++;
	    				}
	    			}
	    			if (card.GetValue() > 7){
	    				if (card.Equals(playableCards[Rules.CLUBS8])){
	    					selectedCards[Rules.CLUBS8] = card;
	    					iNumSelectableCards++;
	    				}
	    			}		    			
	    			break;
	    			
	    		case Card.SPADES:
	    			// Is it a seven, it can be played...
	    			if (card.GetValue() == 7){
	    				if (card.Equals(playableCards[Rules.SPADES8])){
	    					selectedCards[Rules.SPADES8] = card;
	    					iNumSelectableCards++;
	    				}	    				
	    			}
	    			
	    			if (card.GetValue() < 7){
	    				if (card.Equals( playableCards[Rules.SPADES6])){
	    					selectedCards[Rules.SPADES6] = card;
	    					iNumSelectableCards++;
	    				}
	    			}
	    			if (card.GetValue() > 7){
	    				if (card.Equals( playableCards[Rules.SPADES8])){
	    					selectedCards[Rules.SPADES8] = card;
	    					iNumSelectableCards++;
	    				}
	    			}	    			
	    			break;

	    				    			
	    		}
	
	    	}	
	    	
	    	// Now go through the selectable cards  and see which is best to be played
	    	if (iNumSelectableCards == 0)
	    		return ret;
	    	
	    	int iPos;
	    	card = null;
	    	if (iNumSelectableCards == 1){
			  	for (int i = 0; i < 8; i++)
			  		if (selectedCards[i] != null){
			  			card = selectedCards[i];
			  			break;
			  		}
			  			    	
	    	}
	    	else {
			    String sDifficulty = mRules.mView.GetSettings().getString("Difficulty", "0");// 0 = hard, 1 = easy
			    int iDifficulty = Integer.parseInt(sDifficulty);
			    if (iDifficulty == EASY){
			    	// Get a random card from the playable ones
					  Random generator = new Random();
					  int randomIndex = generator.nextInt( iNumSelectableCards );
					  int iCount = 0;
					  	for (int i = 0; i < 8; i++){
					  		if (selectedCards[i] != null){
					  			if (iCount == randomIndex){
					  				card = selectedCards[i];
					  				break;
					  			}
					  			else iCount++;
					  		}	
					  	}
			    }
			    else {
			    	iPos = GetBestCard(selectedCards);
			    	card = selectedCards[iPos];
			    }
	    	}
	    	
	    	switch (card.GetSuit()){
    		case Card.HEARTS:	    	
    			if (card.GetValue() == 7){
    				// and must be played first
    				playableCards[Rules.HEARTS8] = new Card(8,Card.HEARTS);
    				playableCards[Rules.HEARTS6] = new Card(6,Card.HEARTS);
    				playableCards[Rules.CLUBS8] = new Card(7,Card.CLUBS);
    				playableCards[Rules.DIAMONDS8] = new Card(7,Card.DIAMONDS);
    				playableCards[Rules.SPADES8] = new Card(7,Card.SPADES);
 
    				return card;
    			}
    			if (card.GetValue() < 7){
    				if (card.Equals(playableCards[Rules.HEARTS6])){
    					playableCards[Rules.HEARTS6] = new Card(card.GetValue()-1,Card.HEARTS);
    					return card;
    				}
    			}
    			if (card.GetValue() > 7){
    				if (card.Equals( playableCards[Rules.HEARTS8])){    					
    					playableCards[Rules.HEARTS8] = new Card(card.GetValue()+1,Card.HEARTS);
    					return card;
    				}
    			}	    			
    			
    			break;  
    		case Card.DIAMONDS:
    			// Is it a seven, it can be played...
    			if (card.GetValue() == 7){
    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
	    				playableCards[Rules.DIAMONDS8] = new Card(8,Card.DIAMONDS);
	    				playableCards[Rules.DIAMONDS6] = new Card(6,Card.DIAMONDS);	    					
    					return card;
    				}
    			}
    			
    			if (card.GetValue() < 7){
    				if (card.Equals( playableCards[Rules.DIAMONDS6])){	    					
    					playableCards[Rules.DIAMONDS6] = new Card(card.GetValue()-1,Card.DIAMONDS);
    					return card;
    				}
    			}
    			if (card.GetValue() > 7){
    				if (card.Equals( playableCards[Rules.DIAMONDS8])){	    					
    					playableCards[Rules.DIAMONDS8] = new Card(card.GetValue()+1,Card.DIAMONDS);
    					return card;
    				}
    			}		    			
    			
    			break; 
    			
    		case Card.CLUBS:
    			// Is it a seven, it can be played...
    			if (card.GetValue() == 7){

    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
	    				playableCards[Rules.CLUBS8] = new Card(8,Card.CLUBS);
	    				playableCards[Rules.CLUBS6] = new Card(6,Card.CLUBS);	    					
    					return card;
    				}	    				
    			}
    			
    			if (card.GetValue() < 7){
    				if (card.Equals(playableCards[Rules.CLUBS6])){
    					playableCards[Rules.CLUBS6] = new Card(card.GetValue()-1,Card.CLUBS);
    					return card;
    				}
    			}
    			if (card.GetValue() > 7){
    				if (card.Equals(playableCards[Rules.CLUBS8])){
    					Log.i(""," Updating playable card " + (card.GetValue()+1)  + " , " +Card.CLUBS);
    					playableCards[Rules.CLUBS8] = new Card(card.GetValue()+1,Card.CLUBS);
    					return card;
    				}
    			}		    			
    			break;  
    		case Card.SPADES:
    			// Is it a seven, it can be played...
    			if (card.GetValue() == 7){
    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
	    				playableCards[Rules.SPADES8] = new Card(8,Card.SPADES);
	    				playableCards[Rules.SPADES6] = new Card(6,Card.SPADES);	    					
    					return card;
    				}	    				
    			}
    			
    			if (card.GetValue() < 7){
    				if (card.Equals( playableCards[Rules.SPADES6])){    					
    					playableCards[Rules.SPADES6] = new Card(card.GetValue()-1,Card.SPADES);
    					return card;
    				}
    			}
    			if (card.GetValue() > 7){
    				if (card.Equals( playableCards[Rules.SPADES8])){	    					
    					playableCards[Rules.SPADES8] = new Card(card.GetValue()+1,Card.SPADES);
    					return card;
    				}
    			}	    			
    			break;
    			
	    	}
	 
	  }
	  return ret;
  }
 
  
  // Function to get the best card. This is the AI of the whole game
  private int GetBestCard(Card[] card) {
	  int iPos = 0;
	  int iWeightTemp = 0;
	  int iWeight[] = new int[8];
	  for (int i = 0; i < 8; i++){
		  iWeight[i] = -1;
	  }
	  // For each selectable card, work out a weight, the highest weight is the one selected.
	  // The weight is worked out by the distance another of the players cards is from playable ones, with no playable cards in between
	  Card myCard, nextCard;
	  for (int i = 0; i < 8; i++){
		  if (card[i] != null){
			  // if card value > 7
			  if (card[i].GetValue() > 7){
				  // do you have cards that are higher than this card
				    myCard = card[i];
				    iWeightTemp = 0;
			    	for (int j = 0; j < mCardCount; j++){
			    		nextCard = mCard[j];
			    		if (nextCard != null && nextCard.GetSuit() == myCard.GetSuit() && nextCard.GetValue() > myCard.GetValue()){
			    			if (nextCard.GetValue() - myCard.GetValue() > iWeightTemp)
			    				iWeightTemp = nextCard.GetValue() - myCard.GetValue();
			    			myCard = nextCard;
			    		}
			    	}
			    	iWeight[i] = iWeightTemp;
			    		
			  }
			  if (card[i].GetValue() < 7){
				  // do you have cards that are lower than this card
				    myCard = card[i];
				    iWeightTemp = 0;
			    	for (int j = mCardCount-1; j >=0; j--){
			    		nextCard = mCard[j];
			    		if (nextCard != null && nextCard.GetSuit() == myCard.GetSuit() && nextCard.GetValue() < myCard.GetValue()){
			    			if (myCard.GetValue() - nextCard.GetValue() > iWeightTemp)
			    				iWeightTemp = myCard.GetValue() - nextCard.GetValue();
			    			myCard = nextCard;
			    		}
			    	}
			    	iWeight[i] = iWeightTemp;
			    		
			  }	
			  if (card[i].GetValue() == 7){
				  // do you have cards that are in this suit
				    myCard = card[i];
				    int iWeightTemp1;
				    iWeightTemp = 0;
				    iWeightTemp1 = 0;
				 // do you have cards that are higher than this card
			    	for (int j = 0; j < mCardCount; j++){
			    		nextCard = mCard[j];
			    		if (nextCard != null && nextCard.GetSuit() == myCard.GetSuit() && nextCard.GetValue() > myCard.GetValue()){
			    			if (nextCard.GetValue() - myCard.GetValue() > iWeightTemp)
			    				iWeightTemp = nextCard.GetValue() - myCard.GetValue();
			    			myCard = nextCard;
			    		}
			    	}
			    	myCard = card[i];
			    	// do you have cards that are lower than this card
			    	for (int j = mCardCount-1; j >=0; j--){
			    		nextCard = mCard[j];
			    		if (nextCard != null && nextCard.GetSuit() == myCard.GetSuit() && nextCard.GetValue() < myCard.GetValue()){
			    			if (myCard.GetValue() - nextCard.GetValue() > iWeightTemp1)
			    				iWeightTemp1 = myCard.GetValue() - nextCard.GetValue();
			    			myCard = nextCard;
			    		}
			    	}    	
			    	iWeight[i] = iWeightTemp + iWeightTemp1;
			    		
			  }			  
		  }
		  	  
	  }
	  boolean bLoopAceKing = true;
	  int iMaxTemp = 0;
	  int iMaxCount = 0;	
	  int[] iMaxs;
	  iMaxs = new int[8];
	  while (bLoopAceKing){
		  for (int i = 0; i < 8; i++){
			  if (iWeight[i] >= iMaxTemp){
				  if (iWeight[i] == iMaxTemp){
					  iMaxs[iMaxCount] = i;
					  iMaxCount++;
				  }
				  else {
					  iMaxTemp = iWeight[i];
					  iMaxs = new int[8];
					  iMaxCount = 0;
					  iMaxs[iMaxCount] = i;
					  iMaxCount++;
				  }			  
			  }	  
		  }
		  boolean bAceKing = false;
		  // If the top weight is zero, meaning your options don't help you, then play the one that is closest to ace/king
		  if (iMaxTemp == 0 && iMaxCount > 1){
			  for (int k = 0; k < iMaxCount; k++){
					  iWeight[iMaxs[k]] = Math.abs(card[iMaxs[k]].GetValue()-7);
					  bAceKing = true;				  
			  }  		  
		  }
		  if (bAceKing){
			  bLoopAceKing = true;
			  iMaxs = new int[8];
			  iMaxTemp = 0;
			  iMaxCount = 0;			  
		  }
		  else 
			  bLoopAceKing = false;
	  }
	  if (iMaxCount == 1)
		  iPos = iMaxs[iMaxCount-1];
	  else {
		  Random generator = new Random();
		  int randomIndex = generator.nextInt( iMaxCount );
		  iPos = iMaxs[randomIndex];
	  }
		  
	  return iPos;
	  
  }
}

// Straight up default
class DealTo extends CardAnchor {
  private int mShowing;
  public DealTo(float fWSF, float fHSF) {
    super(fWSF, fHSF);
    mShowing = 1;
  }

  @Override
  public void SetShowing(int showing) { mShowing = showing; }

  @Override
  protected void SetCardPosition(int idx) { 
    if (mShowing == 1) {
      mCard[idx].SetPosition(mX, mY);
    } else {
      if (idx < mCardCount - mShowing) {
        mCard[idx].SetPosition(mX, mY);
      } else {
        int offset = mCardCount - mShowing;
        offset = offset < 0 ? 0 : offset;
        mCard[idx].SetPosition(mX + (idx - offset) * mfWidthSF * Card.WIDTH/2, mY);
      }
    }
  }

  @Override
  public void AddCard(Card card) {
    super.AddCard(card);
    SetPosition(mX, mY);
  }

  @Override
  public boolean UnhideTopCard() {
    SetPosition(mX, mY);
    return false;
  }

  @Override
  public Card PopCard() {
    Card ret = super.PopCard();
    SetPosition(mX, mY);
    return ret;
  }

  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
    } else {
      for (int i = mCardCount - mShowing; i < mCardCount; i++) {
        if (i >= 0) {
          drawMaster.DrawCard(canvas, mCard[i]);
        }
      }
    }
  }
}



// Anchor where cards to deal come from
class DealFrom extends CardAnchor {
	 public DealFrom(float fWSF, float fHSF) {
		    super(fWSF, fHSF);
		    
		  }
  @Override
  public Card GrabCard(float x, float y) { return null; }

  @Override
  public boolean TapCard(float x, float y) {
    if (IsOverCard(x, y)) {
      mRules.EventAlert(Rules.EVENT_DEAL, this);
      return true;
    }
    return false;
  }

  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
    } else {
      drawMaster.DrawHiddenCard(canvas, mCard[mCardCount-1]);
    }
  }
}


// Generic Anchor

class GenericAnchor extends CardAnchor {

  //Sequence start values
  public static final int START_ANY=1; // An empty stack can take any card.
  public static final int START_KING=2; // An empty stack can take only a king.

  //Value Sequences
  public static final int SEQ_ANY=1; //You can build as you like
  public static final int SEQ_SEQ=2;  //Building only allows sequential
  public static final int SEQ_ASC=3;  //Ascending only
  public static final int SEQ_DSC=4;  //Descending only
    
  //Suit Sequences that limits how adding cards to the stack works
  public static final int SUIT_ANY=1;  //Build doesn't care about suite
  public static final int SUIT_RB=2;  //Must alternate Red & Black
  public static final int SUIT_OTHER=3;//As long as different
  public static final int SUIT_COLOR=4;//As long as same color
  public static final int SUIT_SAME=5; //As long as same suit
    
  //Pickup & Dropoff Behavior
  public static final int PACK_NONE=1;  // Interaction in this mode not allowed
  public static final int PACK_ONE=2;  //Can only accept 1 card
  public static final int PACK_MULTI=3;  //Can accept multiple cards
  public static final int PACK_FIXED=4;  //Don't think this will ever be used
  public static final int PACK_LIMIT_BY_FREE=5; //For freecell style movement
    
  //Anchor Display (Hidden vs. Shown faces)
  public static final int DISPLAY_ALL=1;  //All cards are shown
  public static final int DISPLAY_HIDE=2; //All cards are hidden
  public static final int DISPLAY_MIX=3;  //Uses a mixture
  public static final int DISPLAY_ONE=4;  //Displays one only

  //Hack to fix Spider Dealing
  public static final int DEALHACK=1;
    
  protected static final int SMALL_SPACING = 7;
  protected static final int HIDDEN_SPACING = 3;

  protected int mSpacing;
  protected boolean mHideHidden;
  protected int mMaxHeight;
  
  public GenericAnchor(float fWSF, float fHSF){
    super(fWSF, fHSF);
    SetStartSeq(GenericAnchor.SEQ_ANY);
    SetBuildSeq(GenericAnchor.SEQ_ANY);
    SetBuildWrap(false);
    SetBuildSuit(GenericAnchor.SUIT_ANY);
    SetDropoff(GenericAnchor.PACK_NONE);
    SetPickup(GenericAnchor.PACK_NONE);
    SetDisplay(GenericAnchor.DISPLAY_ALL);    
    mSpacing = GetMaxSpacing();
    mHideHidden = false;
    mMaxHeight = Card.HEIGHT;
  }

  @Override
  public void SetMaxHeight(int maxHeight) {
    mMaxHeight = maxHeight;
    CheckSizing();
    SetPosition(mX, mY);
  }

  @Override
  protected void SetCardPosition(int idx) {
    if (idx < mHiddenCount) {
      if (mHideHidden) {
        mCard[idx].SetPosition(mX, mY);
      } else {
        mCard[idx].SetPosition(mX, mY + HIDDEN_SPACING * idx);
      }
    } else {
      int startY = mHideHidden ? HIDDEN_SPACING : mHiddenCount * HIDDEN_SPACING;
      int y = (int)mY + startY + (idx - mHiddenCount) * mSpacing;
      mCard[idx].SetPosition(mX, y);
    }
  }

  @Override
  public void SetHiddenCount(int count) {
    super.SetHiddenCount(count);
    CheckSizing();
    SetPosition(mX, mY);
  }
  
  @Override
  public void AddCard(Card card) {
    super.AddCard(card);
    CheckSizing();
    if (mHACK == GenericAnchor.DEALHACK){
      mRules.EventAlert(Rules.EVENT_STACK_ADD, this);
    }
  }

  @Override
  public Card PopCard() {
    Card ret = super.PopCard();
    CheckSizing();
    return ret;
  }
 
  
  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
      return;
    }
    switch (mDISPLAY){
      case GenericAnchor.DISPLAY_ALL:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_HIDE:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawHiddenCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_MIX:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mHiddenCount) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
      case GenericAnchor.DISPLAY_ONE:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mCardCount-1) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
    }
  }
  
  @Override
  public boolean ExpandStack(float x, float y) {
    if (IsOverDeck(x, y)) {
      return (GetMovableCount() > 0);

    }
    return false;
  }
  
  @Override
  public boolean CanMoveStack(float x, float y) { return ExpandStack(x, y); }

  @Override
  public Card[] GetCardStack() {
    int movableCount = GetMovableCount();
    Card[] ret = new Card[movableCount];
    for (int i = movableCount-1; i >= 0; i--) {
      ret[i] = PopCard();
    }
    return ret;
  }


  
  private void CheckSizing() {
    if (mCardCount < 2 || mCardCount - mHiddenCount < 2) {
      mSpacing = GetMaxSpacing();
      mHideHidden = false;
      return;
    }
    int max = mMaxHeight;
    int hidden = mHiddenCount;
    int showing = mCardCount - hidden;
    int spaceLeft = max - (hidden * HIDDEN_SPACING) - Card.HEIGHT;
    int spacing = spaceLeft / (showing - 1);

    if (spacing < SMALL_SPACING && hidden > 1) {
      mHideHidden = true;
      spaceLeft = max - HIDDEN_SPACING - Card.HEIGHT;
      spacing = spaceLeft / (showing - 1);
    } else {
      mHideHidden = false;
      if (spacing > GetMaxSpacing()) {
        spacing = GetMaxSpacing();
      }
    }
    if (spacing != mSpacing) {
      mSpacing = spacing;
      SetPosition(mX, mY);
    }
  }
  // This can't be a constant as Card.HEIGHT isn't constant.
  protected int GetMaxSpacing() {
    return Card.HEIGHT/3;
  }

  public float GetNewY() {
    if (mCardCount == 0) {
      return mY;
    }
    return mCard[mCardCount-1].GetY() + mSpacing;
  }
}


//North Anchor
class NorthAnchor extends CardAnchor {

  //Sequence start values
  public static final int START_ANY=1; // An empty stack can take any card.
  public static final int START_KING=2; // An empty stack can take only a king.

  //Value Sequences
  public static final int SEQ_ANY=1; //You can build as you like
  public static final int SEQ_SEQ=2;  //Building only allows sequential
  public static final int SEQ_ASC=3;  //Ascending only
  public static final int SEQ_DSC=4;  //Descending only
    
  //Suit Sequences that limits how adding cards to the stack works
  public static final int SUIT_ANY=1;  //Build doesn't care about suite
  public static final int SUIT_RB=2;  //Must alternate Red & Black
  public static final int SUIT_OTHER=3;//As long as different
  public static final int SUIT_COLOR=4;//As long as same color
  public static final int SUIT_SAME=5; //As long as same suit
    
  //Pickup & Dropoff Behavior
  public static final int PACK_NONE=1;  // Interaction in this mode not allowed
  public static final int PACK_ONE=2;  //Can only accept 1 card
  public static final int PACK_MULTI=3;  //Can accept multiple cards
  public static final int PACK_FIXED=4;  //Don't think this will ever be used
  public static final int PACK_LIMIT_BY_FREE=5; //For freecell style movement
    
  //Anchor Display (Hidden vs. Shown faces)
  public static final int DISPLAY_ALL=1;  //All cards are shown
  public static final int DISPLAY_HIDE=2; //All cards are hidden
  public static final int DISPLAY_MIX=3;  //Uses a mixture
  public static final int DISPLAY_ONE=4;  //Displays one only

  //Hack to fix Spider Dealing
  public static final int DEALHACK=1;
    
  protected static final int SMALL_SPACING = 7;
  protected static final int HIDDEN_SPACING = 15;//15

  protected int mSpacing;
  protected boolean mHideHidden;
  protected int mMaxHeight;
  
  public NorthAnchor(float fWSF, float fHSF){
    super(fWSF, fHSF);
    //SetStartSeq(GenericAnchor.SEQ_ANY);
    //SetBuildSeq(GenericAnchor.SEQ_ANY);
    //SetBuildWrap(false);
    //SetBuildSuit(GenericAnchor.SUIT_ANY);
    //SetDropoff(GenericAnchor.PACK_NONE);
    //SetPickup(GenericAnchor.PACK_NONE);
    //SetDisplay(GenericAnchor.DISPLAY_ALL);    
    mSpacing = GetMaxSpacing();
    mHideHidden = false;
    mMaxHeight = Card.HEIGHT;
  }

  @Override
  public void SetMaxHeight(int maxHeight) {
    mMaxHeight = maxHeight;
    CheckSizing();
    SetPosition(mX, mY);
  }

  @Override
  protected void SetCardPosition(int idx) { 
    if (idx < mHiddenCount) {
      if (mHideHidden) {
        mCard[idx].SetPosition(mX, mY);
      } else {
        mCard[idx].SetPosition(mX + mfWidthSF * HIDDEN_SPACING * idx, mY);
      }
    } else {
      int startX = mHideHidden ? ((int)mfWidthSF * HIDDEN_SPACING) : ((int)mfWidthSF * mHiddenCount * HIDDEN_SPACING);
      int x = (int)mX + startX + (idx - mHiddenCount) * mSpacing;
      mCard[idx].SetPosition(x, mY);
    }
  }

  @Override
  public void SetHiddenCount(int count) {
    super.SetHiddenCount(count);
    CheckSizing();
    SetPosition(mX, mY);
  }
  
  @Override
  public void AddCard(Card card) {
    super.AddCard(card);
    CheckSizing();
    //if (mHACK == GenericAnchor.DEALHACK){
    //  mRules.EventAlert(Rules.EVENT_STACK_ADD, this);
    //}
  }

  @Override
  public Card PopCard() {
    Card ret = super.PopCard();
    CheckSizing();
    return ret;
  }
  
  
  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
      return;
    }
    switch (mDISPLAY){
      case GenericAnchor.DISPLAY_ALL:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_HIDE:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawHiddenCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_MIX:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mHiddenCount) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
      case GenericAnchor.DISPLAY_ONE:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mCardCount-1) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
    }
  }
  
  @Override
  public boolean ExpandStack(float x, float y) {
    if (IsOverDeck(x, y)) {
      return (GetMovableCount() > 0);
 
    }
    return false;
  }
  
  @Override
  public boolean CanMoveStack(float x, float y) { return ExpandStack(x, y); }

  @Override
  public Card[] GetCardStack() {
    int movableCount = GetMovableCount();
    Card[] ret = new Card[movableCount];
    for (int i = movableCount-1; i >= 0; i--) {
      ret[i] = PopCard();
    }
    return ret;
  }

  private void CheckSizing() {
    if (mCardCount < 2 || mCardCount - mHiddenCount < 2) {
      mSpacing = GetMaxSpacing();
      mHideHidden = false;
      return;
    }
    int max = mMaxHeight;
    int hidden = mHiddenCount;
    int showing = mCardCount - hidden;
    int spaceLeft = max - (hidden * HIDDEN_SPACING) - Card.HEIGHT;
    int spacing = spaceLeft / (showing - 1);

    if (spacing < SMALL_SPACING && hidden > 1) {
      mHideHidden = true;
      spaceLeft = max - HIDDEN_SPACING - Card.HEIGHT;
      spacing = spaceLeft / (showing - 1);
    } else {
      mHideHidden = false;
      if (spacing > GetMaxSpacing()) {
        spacing = GetMaxSpacing();
      }
    }
    if (spacing != mSpacing) {
      mSpacing = spacing;
      SetPosition(mX, mY);
    }
  }
  // This can't be a constant as Card.HEIGHT isn't constant.
  protected int GetMaxSpacing() {
    return Card.HEIGHT/3;
  }

  public float GetNewY() {
    if (mCardCount == 0) {
      return mY;
    }
    return mCard[mCardCount-1].GetY() + mSpacing;
  }
}

//EastWest Anchor
class EastWestAnchor extends CardAnchor {

  //Sequence start values
  public static final int START_ANY=1; // An empty stack can take any card.
  public static final int START_KING=2; // An empty stack can take only a king.

  //Value Sequences
  public static final int SEQ_ANY=1; //You can build as you like
  public static final int SEQ_SEQ=2;  //Building only allows sequential
  public static final int SEQ_ASC=3;  //Ascending only
  public static final int SEQ_DSC=4;  //Descending only
    
  //Suit Sequences that limits how adding cards to the stack works
  public static final int SUIT_ANY=1;  //Build doesn't care about suite
  public static final int SUIT_RB=2;  //Must alternate Red & Black
  public static final int SUIT_OTHER=3;//As long as different
  public static final int SUIT_COLOR=4;//As long as same color
  public static final int SUIT_SAME=5; //As long as same suit
    
  //Pickup & Dropoff Behavior
  public static final int PACK_NONE=1;  // Interaction in this mode not allowed
  public static final int PACK_ONE=2;  //Can only accept 1 card
  public static final int PACK_MULTI=3;  //Can accept multiple cards
  public static final int PACK_FIXED=4;  //Don't think this will ever be used
  public static final int PACK_LIMIT_BY_FREE=5; //For freecell style movement
    
  //Anchor Display (Hidden vs. Shown faces)
  public static final int DISPLAY_ALL=1;  //All cards are shown
  public static final int DISPLAY_HIDE=2; //All cards are hidden
  public static final int DISPLAY_MIX=3;  //Uses a mixture
  public static final int DISPLAY_ONE=4;  //Displays one only

  //Hack to fix Spider Dealing
  public static final int DEALHACK=1;
    
  protected static final int SMALL_SPACING = 6;
  protected static final int HIDDEN_SPACING = 9;//9

  protected int mSpacing;
  protected boolean mHideHidden;
  protected int mMaxHeight;
  
  public EastWestAnchor(float fWSF, float fHSF){
    super(fWSF, fHSF);
    //SetStartSeq(GenericAnchor.SEQ_ANY);
    //SetBuildSeq(GenericAnchor.SEQ_ANY);
    //SetBuildWrap(false);
    //SetBuildSuit(GenericAnchor.SUIT_ANY);
    //SetDropoff(GenericAnchor.PACK_NONE);
    //SetPickup(GenericAnchor.PACK_NONE);
    //SetDisplay(GenericAnchor.DISPLAY_ALL);    
    mSpacing = GetMaxSpacing();
    mHideHidden = false;
    mMaxHeight = Card.HEIGHT;
  }

  @Override
  public void SetMaxHeight(int maxHeight) {
    mMaxHeight = maxHeight;
    CheckSizing();
    SetPosition(mX, mY);
  }

  @Override
  protected void SetCardPosition(int idx) {
    if (idx < mHiddenCount) {
      if (mHideHidden) {
        mCard[idx].SetPosition(mX, mY);
      } else {
        mCard[idx].SetPosition(mX, mY + mfHeightSF * HIDDEN_SPACING * idx);
      }
    } else {
      int startY = mHideHidden ? (int)mfHeightSF * HIDDEN_SPACING : (int)mfHeightSF * mHiddenCount * HIDDEN_SPACING;
      int y = (int)mY + startY + (idx - mHiddenCount) * mSpacing;
      mCard[idx].SetPosition(mX, y);
    }
  }

  @Override
  public void SetHiddenCount(int count) {
    super.SetHiddenCount(count);
    CheckSizing();
    SetPosition(mX, mY);
  }
  
  @Override
  public void AddCard(Card card) {
    super.AddCard(card);
    CheckSizing();
    if (mHACK == GenericAnchor.DEALHACK){
      mRules.EventAlert(Rules.EVENT_STACK_ADD, this);
    }
  }

  @Override
  public Card PopCard() {
    Card ret = super.PopCard();
    CheckSizing();
    return ret;
  }
 
  
  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
      return;
    }
    switch (mDISPLAY){
      case GenericAnchor.DISPLAY_ALL:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_HIDE:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawHiddenCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_MIX:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mHiddenCount) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
      case GenericAnchor.DISPLAY_ONE:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mCardCount-1) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
    }
  }
  
  @Override
  public boolean ExpandStack(float x, float y) {
    if (IsOverDeck(x, y)) {
      return (GetMovableCount() > 0);
 
    }
    return false;
  }
  
  @Override
  public boolean CanMoveStack(float x, float y) { return ExpandStack(x, y); }

  @Override
  public Card[] GetCardStack() {
    int movableCount = GetMovableCount();
    Card[] ret = new Card[movableCount];
    for (int i = movableCount-1; i >= 0; i--) {
      ret[i] = PopCard();
    }
    return ret;
  }

  private void CheckSizing() {
    if (mCardCount < 2 || mCardCount - mHiddenCount < 2) {
      mSpacing = GetMaxSpacing();
      mHideHidden = false;
      return;
    }
    int max = mMaxHeight;
    int hidden = mHiddenCount;
    int showing = mCardCount - hidden;
    int spaceLeft = max - (hidden * HIDDEN_SPACING) - Card.HEIGHT;
    int spacing = spaceLeft / (showing - 1);

    if (spacing < SMALL_SPACING && hidden > 1) {
      mHideHidden = true;
      spaceLeft = max - HIDDEN_SPACING - Card.HEIGHT;
      spacing = spaceLeft / (showing - 1);
    } else {
      mHideHidden = false;
      if (spacing > GetMaxSpacing()) {
        spacing = GetMaxSpacing();
      }
    }
    if (spacing != mSpacing) {
      mSpacing = spacing;
      SetPosition(mX, mY);
    }
  }
  // This can't be a constant as Card.HEIGHT isn't constant.
  protected int GetMaxSpacing() {
    return Card.HEIGHT/3;
  }

  public float GetNewY() {
    if (mCardCount == 0) {
      return mY;
    }
    return mCard[mCardCount-1].GetY() + mSpacing;
  }
}

//South Anchor
class SouthAnchor extends CardAnchor {

  //Sequence start values
  public static final int START_ANY=1; // An empty stack can take any card.
  public static final int START_KING=2; // An empty stack can take only a king.

  //Value Sequences
  public static final int SEQ_ANY=1; //You can build as you like
  public static final int SEQ_SEQ=2;  //Building only allows sequential
  public static final int SEQ_ASC=3;  //Ascending only
  public static final int SEQ_DSC=4;  //Descending only
    
  //Suit Sequences that limits how adding cards to the stack works
  public static final int SUIT_ANY=1;  //Build doesn't care about suite
  public static final int SUIT_RB=2;  //Must alternate Red & Black
  public static final int SUIT_OTHER=3;//As long as different
  public static final int SUIT_COLOR=4;//As long as same color
  public static final int SUIT_SAME=5; //As long as same suit
    
  //Pickup & Dropoff Behavior
  public static final int PACK_NONE=1;  // Interaction in this mode not allowed
  public static final int PACK_ONE=2;  //Can only accept 1 card
  public static final int PACK_MULTI=3;  //Can accept multiple cards
  public static final int PACK_FIXED=4;  //Don't think this will ever be used
  public static final int PACK_LIMIT_BY_FREE=5; //For freecell style movement
    
  //Anchor Display (Hidden vs. Shown faces)
  public static final int DISPLAY_ALL=1;  //All cards are shown
  public static final int DISPLAY_HIDE=2; //All cards are hidden
  public static final int DISPLAY_MIX=3;  //Uses a mixture
  public static final int DISPLAY_ONE=4;  //Displays one only

  //Hack to fix Spider Dealing
  public static final int DEALHACK=1;
    
  protected static final int SMALL_SPACING = 7;
  protected static final int HIDDEN_SPACING = 35;

  protected int mSpacing;
  protected boolean mHideHidden;
  protected int mMaxHeight;
  
  public SouthAnchor(float fWSF, float fHSF){
    super(fWSF, fHSF);
    //SetStartSeq(GenericAnchor.SEQ_ANY);
    //SetBuildSeq(GenericAnchor.SEQ_ANY);
    //SetBuildWrap(false);
    //SetBuildSuit(GenericAnchor.SUIT_ANY);
    //SetDropoff(GenericAnchor.PACK_NONE);
    //SetPickup(GenericAnchor.PACK_NONE);
    //SetDisplay(GenericAnchor.DISPLAY_ALL);    
    mSpacing = GetMaxSpacing();
    mHideHidden = false;
    mMaxHeight = Card.HEIGHT;
  }

  @Override
  public void SetMaxHeight(int maxHeight) {
    mMaxHeight = maxHeight;
    CheckSizing();
    SetPosition(mX, mY);
  }

  @Override
  protected void SetCardPosition(int idx) {
	  
    if (idx < mHiddenCount) {
      if (mHideHidden) {
        mCard[idx].SetPosition(mX, mY);
      } else {   	  
        mCard[idx].SetPosition(mX + mfWidthSF * HIDDEN_SPACING * idx, mY);
      }
    } else {
      int startX = mHideHidden ? (int)mfWidthSF * HIDDEN_SPACING : (int)mfWidthSF * mHiddenCount * HIDDEN_SPACING;
      int x = (int)mX + startX + (idx - mHiddenCount) * (int)(mSpacing);
      mCard[idx].SetPosition(x, mY);
    }
  }

  @Override
  public void SetHiddenCount(int count) {
    super.SetHiddenCount(count);
    CheckSizing();
    SetPosition(mX, mY);
  }
  
  @Override
  public void AddCard(Card card) {
    super.AddCard(card);
    CheckSizing();
    if (mHACK == GenericAnchor.DEALHACK){
      mRules.EventAlert(Rules.EVENT_STACK_ADD, this);
    }
  }

  @Override
  public Card PopCard() {
    Card ret = super.PopCard();
    CheckSizing();
    return ret;
  }
 
  
  @Override
  public void Draw(DrawMaster drawMaster, Canvas canvas) {
    if (mCardCount == 0) {
      //drawMaster.DrawEmptyAnchor(canvas, mX, mY, mDone);
      return;
    }
    
    switch (mDISPLAY){
      case GenericAnchor.DISPLAY_ALL:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_HIDE:
        for (int i = 0; i < mCardCount; i++) {
          drawMaster.DrawHiddenCard(canvas, mCard[i]);
        }
        break;
      case GenericAnchor.DISPLAY_MIX:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mHiddenCount) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
      case GenericAnchor.DISPLAY_ONE:
        for (int i = 0; i < mCardCount; i++) {
          if (i < mCardCount-1) {
            drawMaster.DrawHiddenCard(canvas, mCard[i]);
          } else {
            drawMaster.DrawCard(canvas, mCard[i]);
          }
        }
        break;
    }
  }
  
  @Override
  public boolean ExpandStack(float x, float y) {
    if (IsOverDeck(x, y)) {
      return (GetMovableCount() > 0);
      /*
      if (mHiddenCount >= mCardCount) {
        mHiddenCount = mCardCount == 0 ? 0 : mCardCount - 1;
      } else if (mCardCount - mHiddenCount > 1) {
        return true;
      }
      */
    }
    return false;
  }
  
  @Override
  public boolean CanMoveStack(float x, float y) { return ExpandStack(x, y); }

  @Override
  public Card[] GetCardStack() {
    int movableCount = GetMovableCount();
    Card[] ret = new Card[movableCount];
    for (int i = movableCount-1; i >= 0; i--) {
      ret[i] = PopCard();
    }
    return ret;
  }


  private void CheckSizing() {
    if (mCardCount < 2 || mCardCount - mHiddenCount < 2) {
      mSpacing = GetMaxSpacing();
      mHideHidden = false;
      return;
    }
    int max = mMaxHeight;
    int hidden = mHiddenCount;
    int showing = mCardCount - hidden;
    int spaceLeft = max - (hidden * HIDDEN_SPACING) - Card.HEIGHT;
    int spacing = spaceLeft / (showing - 1);

    if (spacing < SMALL_SPACING && hidden > 1) {
      mHideHidden = true;
      spaceLeft = max - HIDDEN_SPACING - Card.HEIGHT;
      spacing = spaceLeft / (showing - 1);
    } else {
      mHideHidden = false;
      if (spacing > GetMaxSpacing()) {
        spacing = GetMaxSpacing();
      }
    }
    if (spacing != mSpacing) {
      mSpacing = spacing;
      SetPosition(mX, mY);
    }
  }
  // This can't be a constant as Card.HEIGHT isn't constant.
  protected int GetMaxSpacing() {
	
    return Card.HEIGHT/3;
  }

  public float GetNewY() {
    if (mCardCount == 0) {
      return mY;
    }
    return mCard[mCardCount-1].GetY() + mSpacing;
  }
  
  public Card SelectCard(float x, float y){
	    Card ret = null;
	    if (mCardCount > 0){
	    	ret = OverWhichCard(x, y);
	    }
	    return ret;	  
	  
  }
  
  private Card OverWhichCard(float x, float y) {

	    float clx = mCardCount == 0 ? mX : mCard[mCardCount - 1].GetX();
	    float leftX = mLeftEdge == -1 ? mX : mLeftEdge;
	    float rightX = mRightEdge == -1 ? clx + mfWidthSF * Card.WIDTH : mRightEdge;
	    float topY = (mCardCount == 0 ) ? mY : mCard[mCardCount-1].GetY();
	    float botY = mCardCount > 0 ? mCard[mCardCount - 1].GetY() : mY;
	    botY += mfHeightSF * Card.HEIGHT;


	    if (mBottom != -1 && botY + 10 >= mBottom)
	      botY = mBottom;

	    if (x >= leftX && x <= rightX && y >= topY && y <= botY) {
	    	if (mCardCount == 1)
	    		return mCard[0];
	    	int iCount = 1;
	    	while (iCount < mCardCount && (x >(leftX + iCount *HIDDEN_SPACING * mfWidthSF)))
	    		iCount++;
	      return mCard[iCount - 1];
	    }
	    return null;
	  } 
}
