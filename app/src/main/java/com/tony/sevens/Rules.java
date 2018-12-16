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

import android.os.Bundle;
import au.tonyrowe.sevens.R;
import java.util.Comparator;
import java.util.Stack;
import java.util.Arrays;




public abstract class Rules {

  public static final int SEVENS = 1;

  public static final int EVENT_INVALID = -1;
  public static final int EVENT_DEAL = 1;
  public static final int EVENT_STACK_ADD = 2;
  public static final int EVENT_FLING = 3;
  public static final int EVENT_SMART_MOVE = 4;
  public static final int EVENT_DEAL_NEXT = 5;
  public static final int EVENT_OPPONENT_MOVE = 6;
  public static final int EVENT_OPPONENT_MOVES = 7;
  public static final int EVENT_MY_MOVE = 8;
  
  public static final int HEARTS8 = 0;
  public static final int HEARTS6 = 1;
  public static final int DIAMONDS8 = 2;
  public static final int DIAMONDS6 = 3;
  public static final int CLUBS8 = 4;
  public static final int CLUBS6 = 5;
  public static final int SPADES8 = 6;
  public static final int SPADES6 = 7;

  private int mType;
  protected SevensView mView;
  protected Stack<Move> mMoveHistory;
  protected AnimateCard mAnimateCard; 
  protected boolean mIgnoreEvents;
  protected EventPoster mEventPoster;


  // Anchors
  protected CardAnchor[] mCardAnchor;
  protected int mCardAnchorCount;

  protected Deck mDeck;
  protected int mCardCount;
  
  protected Card[] mPlayableCards;
  protected Card mStartCard;


  public int GetType() { return mType; }
  public int GetCardCount() { return mCardCount; }
  public CardAnchor[] GetAnchorArray() { return mCardAnchor; }
  public void SetType(int type) { mType = type; }
  public void SetView(SevensView view) { mView = view; }
  public void SetMoveHistory(Stack<Move> moveHistory) { mMoveHistory = moveHistory; }
  public void SetAnimateCard(AnimateCard animateCard) { mAnimateCard = animateCard; }
  public void SetIgnoreEvents(boolean ignore) { mIgnoreEvents = ignore; }
  public void SetEventPoster(EventPoster ep) { mEventPoster = ep; }
  public boolean GetIgnoreEvents() { return mIgnoreEvents; }
  public int GetRulesExtra() { return 0; }
  public String GetGameTypeString() { return ""; }
  public String GetPrettyGameTypeString() { return ""; }
  public boolean HasScore() { return false; }
  public boolean HasString() { return false; }
  public String GetString() { return ""; }
  public void SetCarryOverScore(int score) {}
  public int GetScore() { return 0; }
  public void AddDealCount() {}
  public Card[] GetPlayableCards() {return mPlayableCards;}

  public int CountFreeSpaces() { return 0; }
  protected void SignalOpponentMove(CardAnchor anchor, Card card) {  mView.OpponentMove(anchor, card);   }
  protected void SignalOpponentMoves(int anchor, Card card) {  mView.OpponentMoves(anchor, card);   }
  protected void SignalMyMove(int anchor, Card card) {  mView.MyMove(anchor, card);   }
  protected void SignalMove() {  mView.DisplayMove();   }

  abstract public void Init(Bundle map, boolean bResetScore, Player[] players);
  public void EventAlert(int event) { if (!mIgnoreEvents) { mEventPoster.PostEvent(event); mView.Refresh(); } }
  public void EventAlert(int event, CardAnchor anchor) { if (!mIgnoreEvents) { mEventPoster.PostEvent(event, anchor);  mView.Refresh();} }
  public void EventAlert(int event, CardAnchor anchor, Card card) { if (!mIgnoreEvents) { mEventPoster.PostEvent(event, anchor, card);  mView.Refresh();} }
  public void EventAlert(int event, int anchor, Card card) { if (!mIgnoreEvents) { mEventPoster.PostEvent(event, anchor, card);  mView.Refresh();} }
  public void ClearEvent() { mEventPoster.ClearEvent(); }
  abstract public void EventProcess(int event, CardAnchor anchor);
  abstract public void EventProcess(int event, CardAnchor anchor, Card card);
  abstract public void EventProcess(int event);
  abstract public void EventProcess(int event, int iAnchor, Card card);
  abstract public void Resize(int width, int height);
  public boolean Fling(MoveCard moveCard) { moveCard.Release(); return false; }
  public void HandleEvents() { 
    while (!mIgnoreEvents && mEventPoster.HasEvent()) {
      mEventPoster.HandleEvent();
    }
  }

  public void RefreshOptions() {
   
  }

  public static Rules CreateRules(int type, Bundle map, SevensView view,
                                  /*Stack<Move> moveHistory*/ AnimateCard animate, boolean bResetScore, Player[] players) {
    Rules ret = null;
    switch (type) {
      case SEVENS:
        ret = new NormalSevens();
        break;
    }

    if (ret != null) {
      ret.SetType(type);
      ret.SetView(view);
      //ret.SetMoveHistory(moveHistory);
      ret.SetAnimateCard(animate);
      ret.SetEventPoster(new EventPoster(ret));
      ret.RefreshOptions();
      ret.Init(map, bResetScore, players);
    }
    return ret;
  }
}

class NormalSevens extends Rules {

  private int mDealsLeft;
  private String mScoreString;
  private int mLastScore;
  private int mCarryOverScore;
  private Player[] mPlayers;

  public Player[] GetPlayers() { return mPlayers;}
  public String GetPlayer1() { return mPlayers[0].m_name; }
  public String GetPlayer2() { return mPlayers[1].m_name; }
  public String GetPlayer3() { return mPlayers[2].m_name; }
  public String GetPlayer4() { return mPlayers[3].m_name; } 
  
  public int GetPlayer1Score() { return mPlayers[0].m_score; }
  public int GetPlayer2Score() { return mPlayers[1].m_score; }
  public int GetPlayer3Score() { return mPlayers[2].m_score; }
  public int GetPlayer4Score() { return mPlayers[3].m_score; } 
  
  public int GetPlayer1Total() { return mPlayers[0].m_total; }
  public int GetPlayer2Total() { return mPlayers[1].m_total; }
  public int GetPlayer3Total() { return mPlayers[2].m_total; }
  public int GetPlayer4Total() { return mPlayers[3].m_total; } 
  
  
  public void ComputeScores(){
      int[] scores;
      scores = new int[4];
      boolean bGameOver = false;
      for (int i = 0; i <= 3; i++) {
    	scores[i] = 0; 
  		Card[] Cards;
		Cards = mView.mCardAnchor[i].GetCards();
  		for (int j = 0; j < mView.mCardAnchor[i].GetCount(); j++){
  			if (Cards[j] != null){
  				if (Cards[j].GetValue() > 10)
  					scores[i] += 10;
  				else {
  					if (Cards[j].GetValue() == 1)
  						scores[i] += 10;
  					else
  						scores[i] += Cards[j].GetValue();
  				}
  			}
  		}	    			
      }
   
	    String sLoseScore = mView.GetSettings().getString("LoseScore", "0");// 0 = indefinite...
	    int iLoseScore = Integer.parseInt(sLoseScore) * 100;
	    
      for (int i = 0; i <= 3; i++) {
    	  if (mPlayers[i] != null){
    		  mPlayers[i].m_total += scores[mPlayers[i].m_anchor];
    		  if ((mPlayers[i].m_total > iLoseScore) && (iLoseScore > 0))
    			  bGameOver = true;
    		  mPlayers[i].m_score = scores[mPlayers[i].m_anchor];
    	  }
      }
 

      
  
      Arrays.sort(mPlayers, new Comparator<Player>() { 
          @Override 
          public int compare(Player player1, Player player2) { 
              if(player1.m_total > player2.m_total) return 1; 
              else if(player1.m_total < player2.m_total) return -1; 
              else return 0;             
             } 
 });
      //Arrays.sort(mPlayers);    
      //Arrays.sort(scores);
      ((Sevens)mView.getContext()).DisplayScore(bGameOver);
  }

  @Override
  public void Init(Bundle map, boolean bResetScore, Player[] players) {
    mIgnoreEvents = true;

    if (bResetScore){
    	  mPlayers = new Player[4];
    	    Player playerW = new Player((String)(mView.getContext().getResources()).getText(R.string.west), 0,0,CardAnchor.WEST);
    	    Player playerN = new Player((String)(mView.getContext().getResources()).getText(R.string.north), 0,0,CardAnchor.NORTH);
    	    Player playerE = new Player((String)(mView.getContext().getResources()).getText(R.string.east), 0,0,CardAnchor.EAST);
    	    Player playerS = new Player((String)(mView.getContext().getResources()).getText(R.string.south), 0,0,CardAnchor.SOUTH);
    	    
    	    mPlayers[CardAnchor.WEST] = playerW;
    	    mPlayers[CardAnchor.NORTH] = playerN;
    	    mPlayers[CardAnchor.EAST] = playerE;
    	    mPlayers[CardAnchor.SOUTH] = playerS;
    	
    }
    else 
    	mPlayers = players;
  	


 
    // 16 total anchors for regular sevens
    mCardCount = 52;
    mCardAnchorCount = 16;
    mCardAnchor = new CardAnchor[mCardAnchorCount];
    
    mPlayableCards = new Card[8]; // There is 8 possible playable cards at any one time
    mStartCard = new Card(7,Card.HEARTS);
    mPlayableCards[Rules.HEARTS8] = mStartCard;
    
    float fWidthSF = mView.getContext().getResources().getDisplayMetrics().widthPixels/480.0f;
    float fHeightSF = mView.getContext().getResources().getDisplayMetrics().heightPixels/295.0f;

    // Top dealt from anchors
    mCardAnchor[0] = CardAnchor.CreateAnchor(CardAnchor.WEST, 0, this, fWidthSF, fHeightSF);
    mCardAnchor[1] = CardAnchor.CreateAnchor(CardAnchor.NORTH, 1, this, fWidthSF, fHeightSF);
    mCardAnchor[2] = CardAnchor.CreateAnchor(CardAnchor.EAST, 2, this, fWidthSF, fHeightSF);
    mCardAnchor[3] = CardAnchor.CreateAnchor(CardAnchor.SOUTH, 3, this, fWidthSF, fHeightSF);
    mCardAnchor[4] = CardAnchor.CreateAnchor(CardAnchor.SPADES7, 4, this, fWidthSF, fHeightSF);
    mCardAnchor[5] = CardAnchor.CreateAnchor(CardAnchor.CLUBS7, 5, this, fWidthSF, fHeightSF);
    mCardAnchor[6] = CardAnchor.CreateAnchor(CardAnchor.DIAMONDS7, 6, this, fWidthSF, fHeightSF);
    mCardAnchor[7] = CardAnchor.CreateAnchor(CardAnchor.HEARTS7, 7, this, fWidthSF, fHeightSF);
    mCardAnchor[8] = CardAnchor.CreateAnchor(CardAnchor.SPADES8, 8, this, fWidthSF, fHeightSF);
    mCardAnchor[9] = CardAnchor.CreateAnchor(CardAnchor.CLUBS8, 9, this, fWidthSF, fHeightSF);
    mCardAnchor[10] = CardAnchor.CreateAnchor(CardAnchor.DIAMONDS8, 10, this, fWidthSF, fHeightSF);
    mCardAnchor[11] = CardAnchor.CreateAnchor(CardAnchor.HEARTS8, 11, this, fWidthSF, fHeightSF);
    mCardAnchor[12] = CardAnchor.CreateAnchor(CardAnchor.SPADES6, 12, this, fWidthSF, fHeightSF);
    mCardAnchor[13] = CardAnchor.CreateAnchor(CardAnchor.CLUBS6, 13, this, fWidthSF, fHeightSF);
    mCardAnchor[14] = CardAnchor.CreateAnchor(CardAnchor.DIAMONDS6, 14, this, fWidthSF, fHeightSF);
    mCardAnchor[15] = CardAnchor.CreateAnchor(CardAnchor.HEARTS6, 15, this, fWidthSF, fHeightSF);    
    //****
    mCardAnchor[0].SetDisplay(GenericAnchor.DISPLAY_HIDE);
    mCardAnchor[1].SetDisplay(GenericAnchor.DISPLAY_HIDE);
    mCardAnchor[2].SetDisplay(GenericAnchor.DISPLAY_HIDE);
    //****
    //mCardAnchor[0].SetDisplay(GenericAnchor.DISPLAY_ALL);
    //mCardAnchor[1].SetDisplay(GenericAnchor.DISPLAY_ALL);
    //mCardAnchor[2].SetDisplay(GenericAnchor.DISPLAY_ALL);
    //
    mCardAnchor[3].SetDisplay(GenericAnchor.DISPLAY_ALL);
    mCardAnchor[4].SetDisplay(GenericAnchor.DISPLAY_ALL);
    mCardAnchor[5].SetDisplay(GenericAnchor.DISPLAY_ALL);
    mCardAnchor[6].SetDisplay(GenericAnchor.DISPLAY_ALL);
    mCardAnchor[7].SetDisplay(GenericAnchor.DISPLAY_ALL);
    mCardAnchor[8].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[9].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[10].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[11].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[12].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[13].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[14].SetDisplay(GenericAnchor.DISPLAY_ONE);
    mCardAnchor[15].SetDisplay(GenericAnchor.DISPLAY_ONE);    


    if (map != null) {
      // Do some assertions, default to a new game if we find an invalid state
      if (map.getInt("cardAnchorCount") == 16 &&
          map.getInt("cardCount") == 52) {
        int[] cardCount = map.getIntArray("anchorCardCount");
        int[] hiddenCount = map.getIntArray("anchorHiddenCount");
        int[] value = map.getIntArray("value");
        int[] suit = map.getIntArray("suit");
        int[] playableCardValue = map.getIntArray("playableCardValue");
        int[] playableCardSuit = map.getIntArray("playableCardSuit");        
        int cardIdx = 0;
        mPlayers = (Player[])map.getSerializable(("players"));
        mDealsLeft = map.getInt("rulesExtra");
        mView.miCurrentPlayer = map.getInt("currentPlayer");

        for (int i = 0; i < 16; i++) {
          for (int j = 0; j < cardCount[i]; j++, cardIdx++) {
            Card card = new Card(value[cardIdx], suit[cardIdx]);
            mCardAnchor[i].AddCard(card);
          }
          mCardAnchor[i].SetHiddenCount(hiddenCount[i]);
          if (i < CardAnchor.HEARTS6)
        	  mCardAnchor[i].ArrangeSuits();
        }
        
        for (int i = 0; i < 8; i++) {
        	if (playableCardValue[i] > 0){
        		Card card = new Card(playableCardValue[i], playableCardSuit[i]);
        		mPlayableCards[i] = card;
        	}
 
        }       
        for (int i = CardAnchor.WEST; i <= CardAnchor.SOUTH; i++)
        	mCardAnchor[i].ArrangeSuits();


        mIgnoreEvents = false;
        // Return here so an invalid save state will result in a new game
        return;
      }
    }

    mDeck = new Deck(1);
    for (int i = 0; i < 13; i++) {
      for (int j = 0; j <= 3; j++) {
        mCardAnchor[j].AddCard(mDeck.PopCard());
      }     
    }
    for (int i = CardAnchor.WEST; i <= CardAnchor.SOUTH; i++){
    	mCardAnchor[i].SetHiddenCount(13);
    	mCardAnchor[i].ArrangeSuits();

    }

    mIgnoreEvents = false;
    for (int i = HEARTS8; i <= SPADES6; i++)
    	mPlayableCards[i] = null;
    
  }

  @Override
  public void SetCarryOverScore(int score) {
    mCarryOverScore = score;
  }
  


  @Override
  public void Resize(int width, int height) {
  
    float fWidthSF = width/480.0f;
    float fHeightSF = height/295.0f;
    mCardAnchor[CardAnchor.NORTH].SetPosition(width/2 - fWidthSF * Card.WIDTH/2 - (mCardAnchor[CardAnchor.NORTH].mCardCount/2)*NorthAnchor.HIDDEN_SPACING * fWidthSF, 3);
    mCardAnchor[CardAnchor.SOUTH].SetPosition(width/2 - fWidthSF * Card.WIDTH/2 - (mCardAnchor[CardAnchor.SOUTH].mCardCount/2)*SouthAnchor.HIDDEN_SPACING * fWidthSF, height-fHeightSF * Card.HEIGHT-3);
    mCardAnchor[CardAnchor.EAST].SetPosition(width- fWidthSF * Card.WIDTH-12, height*0.4f - fHeightSF * Card.HEIGHT/2 - (mCardAnchor[CardAnchor.EAST].mCardCount/2)*EastWestAnchor.HIDDEN_SPACING * fHeightSF);
    mCardAnchor[CardAnchor.WEST].SetPosition(10, height*0.4f - fHeightSF * Card.HEIGHT/2- (mCardAnchor[CardAnchor.WEST].mCardCount/2)*EastWestAnchor.HIDDEN_SPACING *fHeightSF );
    mCardAnchor[CardAnchor.SPADES7].SetPosition(width/2- 2*Card.WIDTH * fWidthSF - 12 , height/2 - fHeightSF * Card.HEIGHT/2);
    mCardAnchor[CardAnchor.HEARTS7].SetPosition(width/2- fWidthSF * Card.WIDTH - 4, height/2 - fHeightSF * Card.HEIGHT/2 );
    mCardAnchor[CardAnchor.CLUBS7].SetPosition(width/2 + 4, height/2 - fHeightSF * Card.HEIGHT/2);
    mCardAnchor[CardAnchor.DIAMONDS7].SetPosition(width/2 + fWidthSF * Card.WIDTH + 12, height/2 - fHeightSF * Card.HEIGHT/2);
    mCardAnchor[CardAnchor.SPADES8].SetPosition(width/2- 2*fWidthSF * Card.WIDTH - 12 , height/2 - fHeightSF *Card.HEIGHT);
    mCardAnchor[CardAnchor.HEARTS8].SetPosition(width/2- fWidthSF * Card.WIDTH- 4, height/2 - fHeightSF * Card.HEIGHT );
    mCardAnchor[CardAnchor.CLUBS8].SetPosition(width/2 + 4, height/2 - fHeightSF * Card.HEIGHT);
    mCardAnchor[CardAnchor.DIAMONDS8].SetPosition(width/2 + fWidthSF * Card.WIDTH + 12, height/2 - fHeightSF * Card.HEIGHT);    
    mCardAnchor[CardAnchor.SPADES6].SetPosition(width/2- 2*fWidthSF * Card.WIDTH - 12 , height/2);
    mCardAnchor[CardAnchor.HEARTS6].SetPosition(width/2- fWidthSF *Card.WIDTH- 4, height/2);
    mCardAnchor[CardAnchor.CLUBS6].SetPosition(width/2 + 4, height/2 );
    mCardAnchor[CardAnchor.DIAMONDS6].SetPosition(width/2 + fWidthSF *Card.WIDTH + 12, height/2);    


    // Setup edge cards (Touch sensor loses sensitivity towards the edge).
    mCardAnchor[0].SetLeftEdge(0);
    mCardAnchor[2].SetRightEdge(width);
    mCardAnchor[6].SetLeftEdge(0);
    //mCardAnchor[12].SetRightEdge(width);
    for (int i = 0; i < 8; i++) {
      mCardAnchor[i].SetBottom(height);
    }
  }

  @Override
  public void EventProcess(int event, CardAnchor anchor) {
    if (mIgnoreEvents) {
      return;
    }
    mView.StopAnimating();

 
  }
  
  public void EventProcess(int event, CardAnchor anchor, Card card) {
	    if (mIgnoreEvents) {
	      return;
	    }
	    if (event == EVENT_OPPONENT_MOVE) {  
	    	
	        SignalOpponentMove(anchor, card);
	    }
	    else
	    	mView.StopAnimating();
	 
	  }

  @Override
  public void EventProcess(int event, int anchor, Card card) {

    if (mIgnoreEvents) {
      return;
    }
    if (event == EVENT_OPPONENT_MOVES) {  
    	
        SignalOpponentMoves(anchor, card);
    }
    else
    	mView.StopAnimating();
    
   if (event == EVENT_MY_MOVE) {  
    	
        SignalMyMove(anchor, card);
    }
    else
    	mView.StopAnimating(); 

  }

  @Override
  public void EventProcess(int event) {
    if (mIgnoreEvents) {
      return;
    }
    mView.StopAnimating();
  }

  @Override
  public boolean Fling(MoveCard moveCard) {
    if (moveCard.GetCount() == 1) {
      CardAnchor anchor = moveCard.GetAnchor();
      Card card = moveCard.DumpCards(false)[0];
      for (int i = 0; i < 4; i++) {
        if (mCardAnchor[i+2].DropSingleCard(card)) {
          EventAlert(EVENT_FLING, anchor, card);
          return true;
        }
      }
      anchor.AddCard(card);
    } else {
      moveCard.Release();
    }
    return false;
  }

 

 

  @Override
  public int GetRulesExtra() {
    return mDealsLeft;
  }

  @Override
  public String GetGameTypeString() {
	  return "Sevens";
  }
  @Override
  public String GetPrettyGameTypeString() {
	  return "Sevens";
  }

  @Override
  public boolean HasScore() {
    if (mDealsLeft != -1) {
      return true;
    }
    return false;
  }

  @Override
  public boolean HasString() {
    return HasScore();
  }

  @Override
  public String GetString() {
    if (mDealsLeft != -1) {
      int score = mCarryOverScore - 52;
      for (int i = 0; i < 4; i++) {
        score += 5 * mCardAnchor[i+2].GetCount();
      }
      if (score != mLastScore) {
        if (score < 0) {
          mScoreString = "-$" + (score * -1);
        } else {
          mScoreString = "$" + score;
        }
      }
      return mScoreString;
    }
    return "";
  }

  @Override
  public int GetScore() {
    if (mDealsLeft != -1) {
      int score = mCarryOverScore - 52;
      for (int i = 0; i < 4; i++) {
        score += 5 * mCardAnchor[i+2].GetCount();
      }
      return score;
    }
    return 0;
  }

  @Override
  public void AddDealCount() {
    if (mDealsLeft != -1) {
      mDealsLeft++;
      mCardAnchor[0].SetDone(false);
    }
  }
}



class EventPoster {
  private int mEvent, mAnchorNum;
  private CardAnchor mCardAnchor;
  private Card mCard;
  private Rules mRules;

  public EventPoster(Rules rules) {
    mRules = rules;
    mEvent = -1;
    mCardAnchor = null;
    mCard = null;
    mAnchorNum = -1;
  }

  public void PostEvent(int event) {
    PostEvent(event, null, null);
  }

  public void PostEvent(int event, CardAnchor anchor) {
    PostEvent(event, anchor, null);
  }

  public void PostEvent(int event, CardAnchor anchor, Card card) {
    mEvent = event;
    mCardAnchor = anchor;
    mCard = card;
  }
  
  public void PostEvent(int event, int anchor, Card card) {
	    mEvent = event;
	    mAnchorNum = anchor;
	    mCard = card;
	  } 
  
  public void ClearEvent() {
    mEvent = Rules.EVENT_INVALID;
    mCardAnchor = null;
    mCard = null;
  }

  public boolean HasEvent() {
    return mEvent != Rules.EVENT_INVALID;
  }

  public void HandleEvent() {
	  
    if (HasEvent()) {
      int event = mEvent;
      CardAnchor cardAnchor = mCardAnchor;
      Card card = mCard;
      int ianchor = mAnchorNum;
      ClearEvent();
      if (cardAnchor != null && card != null) {
    	  
        mRules.EventProcess(event, cardAnchor, card);
      } else if (cardAnchor != null) {
        mRules.EventProcess(event, cardAnchor);
      } else {
    	  if (ianchor >=0 && card != null)
    		  mRules.EventProcess(event, ianchor, card);
    	  else
    		  mRules.EventProcess(event);
      }
    }
  }
}


