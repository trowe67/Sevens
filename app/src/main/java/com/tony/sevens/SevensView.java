package com.tony.sevens;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import au.tonyrowe.sevens.R;
/*
// Play Sound
AudioManager audioManager = (AudioManager) mContext.getSystemService(mContext.AUDIO_SERVICE);
float actualVolume = (float) audioManager
		.getStreamVolume(AudioManager.STREAM_MUSIC);
float maxVolume = (float) audioManager
		.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
float volume = actualVolume / maxVolume;			    		
((Sevens)mContext).soundPool.play(((Sevens)mContext).soundID, volume, volume, 1, 0, 1f);*/

public class SevensView extends View{
	  private static final int MODE_NORMAL      = 1;
	  private static final int MODE_MOVE_CARD   = 2;
	  private static final int MODE_CARD_SELECT = 3;
	  private static final int MODE_TEXT        = 4;
	  private static final int MODE_ANIMATE     = 5;
	  private static final int MODE_WIN         = 6;
	  private static final int MODE_WIN_STOP    = 7;
	  private static final int MODE_OPPONENT_MOVE    = 8;
	  private static final int MODE_OPPONENT_STOP    = 9;
	  private static final int MODE_OPPONENT_MOVES    = 10;
	  private static final int MODE_MY_MOVE    = 11;

	  private static final String SAVE_FILENAME = "sevens_save.bin";
	  // This is incremented only when the save system changes.
	  private static final String SAVE_VERSION = "sevens_save";
	  private CharSequence mHelpText;

	  public CardAnchor[] mCardAnchor;
	  private DrawMaster mDrawMaster;
	  private Rules mRules;
	  private TextView mTextView;
	  private AnimateCard mAnimateCard;

	  private MoveCard mMoveCard;
	  private SelectCard mSelectCard;
	  private int mViewMode;
	  private boolean mTextViewDown;

	  private PointF mLastPoint;
	  private PointF mDownPoint;
	  private RefreshHandler mRefreshHandler;
	  private Thread mRefreshThread;
	  //private Stack<Move> mMoveHistory;
	  private Replay mReplay;
	  private OpponentMoves mOpponentMoves;
	  private MyMove mMyMove;
	  private Context mContext;
	  private Speed mSpeed;

	  private int mElapsed = 0;
	  private long mStartTime;
	  private boolean mTimePaused;

	  public boolean mGameStarted;
	  public boolean mbDealDone;
	  public boolean mbPlayable;
	  public int miCurrentPlayer;
	  private boolean mPaused;
	  public boolean mbDrawYourTurn;
 
	  
	  public SevensView(Context context, AttributeSet attrs) {
		    super(context, attrs);
		    setFocusable(true);
		    setFocusableInTouchMode(true);

		    mDrawMaster = new DrawMaster(context);
		    
		    mMoveCard = new MoveCard();
		    mSelectCard = new SelectCard();
		    mViewMode = MODE_NORMAL;
		    mLastPoint = new PointF();
		    mDownPoint = new PointF();
		    mRefreshHandler = new RefreshHandler(this);
		    mRefreshThread = new Thread(mRefreshHandler);
		    mAnimateCard = new AnimateCard(this);
		    mSpeed = new Speed();
		    mReplay = new Replay(this, mAnimateCard);
		    mOpponentMoves = new OpponentMoves(this, mAnimateCard);
		    mMyMove = new MyMove(this, mAnimateCard);

		    mHelpText = context.getResources().getText(R.string.help_text);
		    mContext = context;
		    mTextViewDown = false;
		    mRefreshThread.start();
		    mbPlayable = false;
		    mGameStarted = false;
		    mbDealDone = false;
		    mbDrawYourTurn = false;
		    miCurrentPlayer = 0;
		} 
	  
	  public void InitGame(int gameType, boolean bResetScore, Player[] players) {

		    // We really really want focus :)
		    setFocusable(true);
		    setFocusableInTouchMode(true);
		    requestFocus();

		    SharedPreferences.Editor editor = GetSettings().edit();

		    ChangeViewMode(MODE_NORMAL);
		    mTextView.setVisibility(View.INVISIBLE);
		    //mMoveHistory.clear();
		    mRules = Rules.CreateRules(gameType, null, this, mAnimateCard, bResetScore, players);

		    Card.SetSize(gameType);
		    mDrawMaster.DrawCards(GetSettings().getBoolean("DisplayBigCards", false));
		    mDrawMaster.mbShowPlayable = (GetSettings().getBoolean("DisplayPlayableCards", true));
		    mCardAnchor = mRules.GetAnchorArray();

		    if (mDrawMaster.GetWidth() > 1) {
		      mRules.Resize(mDrawMaster.GetWidth(), mDrawMaster.GetHeight());
		      Refresh();
		    }

		    editor.putInt("LastType", gameType);
		    editor.commit();
		    mStartTime = SystemClock.uptimeMillis();
		    mElapsed = 0;
		    mTimePaused = false;
		    mPaused = false;
		    mGameStarted = false;
		    mbDealDone = true;
		    

		    PlayGame();
		  }	  
	 
	  public void PlayGame(){
		  // Each opponent plays
		  //miCurrentPlayer = 0;
		  if (!mbDealDone)
			  return;
		  if (GetSettings().getBoolean("PlayedBefore", false))
			  mOpponentMoves.StartMoves(null, miCurrentPlayer);
		  
		    
	  }
	  	public void Refresh() {
		    mRefreshHandler.SingleRefresh();
		}
	  	
	  	public void PlayCard(Card cardPlay, int iCardAnchor){
	  		miCurrentPlayer = iCardAnchor;	
	  		mCardAnchor[iCardAnchor].RemoveCard(cardPlay);
    		switch (cardPlay.GetSuit()){
    		case Card.HEARTS:
    			if (cardPlay.GetValue() == 7)   				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.HEARTS7]);
    			
    			if (cardPlay.GetValue() > 7)    				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.HEARTS8]);
  		 
    			
    			if (cardPlay.GetValue() < 7)    				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.HEARTS6]);
  
    			break;
    			
    		case Card.DIAMONDS:
    			if (cardPlay.GetValue() == 7)
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.DIAMONDS7]);
 
    			if (cardPlay.GetValue() > 7)
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.DIAMONDS8]);
		 
    			
    			if (cardPlay.GetValue() < 7)   				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.DIAMONDS6]);
 
    			break;		    			

    		case Card.CLUBS:
    			if (cardPlay.GetValue() == 7)    				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.CLUBS7]);

    			if (cardPlay.GetValue() > 7)    				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.CLUBS8]);
 		 
    			
    			if (cardPlay.GetValue() < 7)
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.CLUBS6]);
 
    			break;	
    			
    		case Card.SPADES:
    			if (cardPlay.GetValue() == 7) 				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.SPADES7]);
 
    			if (cardPlay.GetValue() > 7)
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.SPADES8]);
 		 
    			
    			if (cardPlay.GetValue() < 7) 				
    				mMyMove.StartMove(cardPlay, mCardAnchor[CardAnchor.SPADES6]);
 
    			break;		    			
    		
    		}
    		//StopAnimating();
	  	}
	  	
	  	public void RepositionHands(){
	  		int width = mDrawMaster.GetWidth();
	  		int height = mDrawMaster.GetHeight();
	  	    float fWidthSF = width/480.0f;
	  	    float fHeightSF = height/295.0f;
	  	  	    	
	  	    mCardAnchor[CardAnchor.NORTH].SetPosition(width/2 - fWidthSF * Card.WIDTH/2 - (mCardAnchor[CardAnchor.NORTH].mCardCount/2)*NorthAnchor.HIDDEN_SPACING * fWidthSF, 3);	  	  
	  	    mCardAnchor[CardAnchor.SOUTH].SetPosition(width/2 - fWidthSF * Card.WIDTH/2 - (mCardAnchor[CardAnchor.SOUTH].mCardCount/2)*SouthAnchor.HIDDEN_SPACING * fWidthSF, height-fHeightSF * Card.HEIGHT-3);
	  	    mCardAnchor[CardAnchor.EAST].SetPosition(width- fWidthSF * Card.WIDTH-10, height*0.4f - fHeightSF * Card.HEIGHT/2 - (mCardAnchor[CardAnchor.EAST].mCardCount/2)*EastWestAnchor.HIDDEN_SPACING * fHeightSF);
	  	    mCardAnchor[CardAnchor.WEST].SetPosition(10, height*0.4f - fHeightSF * Card.HEIGHT/2- (mCardAnchor[CardAnchor.WEST].mCardCount/2)*EastWestAnchor.HIDDEN_SPACING *fHeightSF );	  	    

	  	}
	    //public SharedPreferences GetSettings() { return ((Sevens)mContext).GetSettings(); }
	    public DrawMaster GetDrawMaster() { return mDrawMaster; }
	    public Rules GetRules() { return mRules; }
	    public void ClearGameStarted() { mGameStarted = false; }
	     	
	  	
	    public void SetTextView(TextView textView) {
	        mTextView = textView;
	    }	
	    
	  	public SharedPreferences GetSettings() { return ((Sevens)mContext).GetSettings(); }
	  	public void StopAnimating() {
		    if (mViewMode == MODE_ANIMATE) {
		      ChangeViewMode(MODE_NORMAL); 
		    } else if (mViewMode == MODE_WIN) {
		      ChangeViewMode(MODE_WIN_STOP); 
		    }
	        else if (mViewMode == MODE_OPPONENT_MOVE) {
		      ChangeViewMode(MODE_OPPONENT_STOP); 
		    }		    
	    }
	    public void DisplayHelp() {
	        mTextView.setTextSize(12);
	        mTextView.setGravity(Gravity.LEFT);
	        DisplayText(mHelpText);
	    }	
	    
	    public void DisplayText(CharSequence text) {
	        ChangeViewMode(MODE_TEXT);
	        mTextView.setVisibility(View.VISIBLE);
	        mTextView.setText(text);
	        Refresh();
	    }
	    
	    public void DisplayMove() {
	        //MarkWin();

	        mRules.SetIgnoreEvents(true);
	        //mAnimateCard.
	      }
	    public void OpponentMove(CardAnchor anchor, Card card) {
	        ChangeViewMode(MODE_OPPONENT_MOVE);
	        mRules.SetIgnoreEvents(true);
	        mReplay.StartReplay(card, anchor);	        
	      }	    

	    public void OpponentMoves(int anchor, Card card) {
	        ChangeViewMode(MODE_OPPONENT_MOVE);
	        mRules.SetIgnoreEvents(true);
	        PlayCard(card, anchor);
	        	        
	      }
	    
	    public void MyMove(int anchor, Card card) {
	        ChangeViewMode(MODE_MY_MOVE);
	        mRules.SetIgnoreEvents(true);
	        PlayCard(card, anchor);
	        	        
	      }	    
	    
	    public void DrawBoard() {
	        Canvas boardCanvas = mDrawMaster.GetBoardCanvas();
	        mDrawMaster.DrawBackground(boardCanvas);	        
	        for (int i = 0; i < mCardAnchor.length; i++) {
	          mCardAnchor[i].Draw(mDrawMaster, boardCanvas);
	        }
	        
	        mSelectCard.Draw(mDrawMaster, boardCanvas);
		          
	        
	    }
	    	    
	    
	    
	    public void StartAnimating() {
	        DrawBoard();
	        if (mViewMode != MODE_WIN && mViewMode != MODE_ANIMATE && mViewMode != MODE_TEXT) {
	          ChangeViewMode(MODE_ANIMATE);
	        }
	      }

   

	    @Override
	    public void onDraw(Canvas canvas) {

	      // Only draw the stagnant stuff if it may have changed
	      if (mViewMode == MODE_NORMAL) {
	        // SanityCheck is for debug use only.
	        //SanityCheck();
	        DrawBoard();
	      }
	  		int width = mDrawMaster.GetWidth();
	  		int height = mDrawMaster.GetHeight();
	  	    float fWidthSF = width/480.0f;
	  	    float fHeightSF = height/295.0f; 
	  	    
	      mDrawMaster.DrawLastBoard(canvas);
	      // Display Knocks 
	        if (mGameStarted){
		  		  	    
			    for (int i = CardAnchor.WEST; i < CardAnchor.SOUTH; i++){
			    	if (mCardAnchor[i].mbKnock){
			    		if (i == CardAnchor.WEST  || i == CardAnchor.EAST){
			    			int iOffset = 0;
			    			if(i == CardAnchor.EAST)
			    				iOffset = 10;
			    			mDrawMaster.DrawKnock(canvas, (int)mCardAnchor[i].GetX() - iOffset, (int)mCardAnchor[i].GetNewY() - 6 + (int)(mDrawMaster.GetHeight() * (fWidthSF>2.0f ?1.1: 1) * Card.HEIGHT/295 ));
			    		}
			    		if (i == CardAnchor.NORTH)
			    			mDrawMaster.DrawKnock(canvas, (int)mCardAnchor[i].GetX()- (int)(55.0*(fWidthSF>1.0f ?fWidthSF: 1)), (int)mCardAnchor[i].GetNewY() + 20 * (fWidthSF>2.0f ?2: 1));		    			
			    		
			    	}
	
			    }

				    
			    if (mbDrawYourTurn)
			    	mDrawMaster.DrawYourTurn(canvas, (int)mDrawMaster.GetWidth()/2 - 50, (int)mCardAnchor[CardAnchor.SOUTH].GetNewY() - (int)(fHeightSF * Card.HEIGHT)/3 /*+ 10 *(fWidthSF>2.0f ?2: 1) * (int)fHeightSF*/);
	        }
	        else {
	        	if (!mbDealDone){
	        		int score = 0;       		
				    for (int i = CardAnchor.WEST; i <= CardAnchor.SOUTH; i++){
				    	
		    	    	score = 0; 
		    	  		Card[] Cards;
		    			Cards = mCardAnchor[i].GetCards();
		    	  		for (int j = 0; j < mCardAnchor[i].GetCount(); j++){
		    	  			if (Cards[j] != null){
		    	  				if (Cards[j].GetValue() > 10)
		    	  					score += 10;
		    	  				else {
		    	  					if (Cards[j].GetValue() == 1)
		    	  						score += 10;
		    	  					else
		    	  						score += Cards[j].GetValue();
		    	  				}
		    	  			}
		    	  		}				    	
				    	if (i == CardAnchor.WEST  || i == CardAnchor.EAST){	
			    			int iOffset = 0;
			    			if(i == CardAnchor.EAST)
			    				iOffset = 5;				    		
				    			mDrawMaster.DrawScore(score, canvas, (int)mCardAnchor[i].GetX() - iOffset, (int)mCardAnchor[i].GetNewY() - 6 + (int)(mDrawMaster.GetHeight() * (fWidthSF>2.0f ?1.1: 1) * Card.HEIGHT/295 ));
				    	}
				    	if (i == CardAnchor.NORTH|| i == CardAnchor.SOUTH){	
			    			int iOffset = 0;
			    			if(i == CardAnchor.NORTH)
			    				iOffset = 20;	
				    			mDrawMaster.DrawScore(score, canvas, (int)mCardAnchor[i].GetX()- (int)(55.0*(fWidthSF>1.0f ?fWidthSF: 1)), (int)mCardAnchor[i].GetNewY()+ iOffset * (fWidthSF>2.0f ?2: 1));
				    	}
				    		
				    }	        		
	        		
	        		
	        	}
	        	
	        	
	        }

	      switch (mViewMode) {
	        case MODE_MOVE_CARD:
	          mMoveCard.Draw(mDrawMaster, canvas);
	          break;
	        case MODE_CARD_SELECT:
	          mSelectCard.Draw(mDrawMaster, canvas);
	          break;
	        case MODE_WIN:
	        case MODE_OPPONENT_MOVES:
	        case MODE_MY_MOVE:	
	          if (mReplay.IsPlaying()) {
	            mAnimateCard.Draw(mDrawMaster, canvas);
	          }
	        case MODE_WIN_STOP:
	        case MODE_OPPONENT_STOP:
	        case MODE_TEXT:
	          mDrawMaster.DrawShade(canvas);
	          break;
	        case MODE_ANIMATE:
	          mAnimateCard.Draw(mDrawMaster, canvas);
	      }

	      mRules.HandleEvents();
	    }
	    
	    public void onPause() {
	        mPaused = true;

	        if (mRefreshThread != null) {
	          mRefreshHandler.SetRunning(false);
	          mRules.ClearEvent();
	          mRules.SetIgnoreEvents(true);
	          mReplay.StopPlaying();
	          try {
	            mRefreshThread.join(1000);
	          } catch (InterruptedException e) {
	          }
	          mRefreshThread = null;
	          if (mAnimateCard.GetAnimate()) {
	            mAnimateCard.Cancel();
	          }
	          if (mViewMode != MODE_WIN && mViewMode != MODE_WIN_STOP) {
	            ChangeViewMode(MODE_NORMAL);
	          }
	          if (mViewMode != MODE_OPPONENT_MOVES && mViewMode != MODE_MY_MOVE && mViewMode != MODE_OPPONENT_STOP) {
		            ChangeViewMode(MODE_NORMAL);
		          }	          

	          if (mRules != null && mRules.GetScore() > GetSettings().getInt(mRules.GetGameTypeString() + "Score", -52)) {
	            SharedPreferences.Editor editor = GetSettings().edit();
	            editor.putInt(mRules.GetGameTypeString() + "Score", mRules.GetScore());
	            editor.commit();
	          }
	        }
	      }	   
	    
	    public void onResume() {
	        mRefreshHandler.SetRunning(true);
	        mRefreshThread = new Thread(mRefreshHandler);
	        mRefreshThread.start();
	        mRules.SetIgnoreEvents(false);
	        mPaused = false;
	      }	 
	    
	    @Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        mDrawMaster.SetScreenSize(w, h);
	        mRules.Resize(w, h);
	        //mSelectCard.SetHeight(h);
	      }	  
	    
	    public void RestartGame() {
	        mRules.SetIgnoreEvents(true);
	        //while (!mMoveHistory.empty()) {
	          //Undo();
	        //}
	        mRules.SetIgnoreEvents(false);
	        Refresh();
	      }	   
	    
	    @Override
		public boolean onKeyDown(int keyCode, KeyEvent msg) {
	        switch (keyCode) {
	        case KeyEvent.KEYCODE_DPAD_CENTER:
	        case KeyEvent.KEYCODE_SEARCH:
	          if (mViewMode == MODE_TEXT) {
	            ChangeViewMode(MODE_NORMAL);
	          } else if (mViewMode == MODE_NORMAL) {
	            mRules.EventAlert(Rules.EVENT_DEAL, mCardAnchor[0]);
	            Refresh();
	          }
	          return true;
	          case KeyEvent.KEYCODE_BACK:
	        	  InitGame(Rules.SEVENS, true, null);
	            return true;
	          }
	        mRules.HandleEvents();
	        return super.onKeyDown(keyCode, msg);
	      }
	    
	    
	    @Override
	    public boolean onTouchEvent(MotionEvent event) {
	      boolean ret = false;

	      // Yes you can get touch events while in the "paused" state.
	      if (mPaused) {
	        return false;
	      }

	      // Text mode only handles clicks
	      if (mViewMode == MODE_TEXT) {
	        if (event.getAction() == MotionEvent.ACTION_UP && mTextViewDown) {
	          boolean bPlayGame = false;
	          //SharedPreferences.Editor editor = mContext.getSharedPreferences("SevensPreferences", 0).edit();
	          SharedPreferences.Editor editor = GetSettings().edit();
	         
	          if (!GetSettings().getBoolean("PlayedBefore", false))
	        	  bPlayGame  = true;
	          editor.putBoolean("PlayedBefore", true); 
	          editor.commit(); 
	          mTextViewDown = false;
	          ChangeViewMode(MODE_NORMAL);
	          if (bPlayGame)
	        	  PlayGame();
	          ChangeViewMode(MODE_NORMAL);
	        } if (event.getAction() == MotionEvent.ACTION_DOWN) {
	          mTextViewDown = true;
	        }
	        return true;
	      }

	      switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	          mSpeed.Reset();
	          ret = onDown(event.getX(), event.getY()); 
	          mDownPoint.set(event.getX(), event.getY());
	          break;
	        case MotionEvent.ACTION_UP:
	        case MotionEvent.ACTION_CANCEL:
	          ret = onRelease(event.getX(), event.getY()); 
	          break;
	        case MotionEvent.ACTION_MOVE:
	          //if (!mHasMoved) {
	          //  CheckMoved(event.getX(), event.getY());
	          //}
	          //ret = onMove(mLastPoint.x - event.getX(), mLastPoint.y - event.getY(),
	          //             event.getX(), event.getY()); put all back
	          break;
	      }
	      mLastPoint.set(event.getX(), event.getY());

	      //if (!mGameStarted /*&& !mMoveHistory.empty()*/) {
	      //  mGameStarted = true;
	        //MarkAttempt(); 
	      //}

	      mRules.HandleEvents();
	      return ret;
	    }	    
	    
	    
	    private void ChangeViewMode(int newMode) {
	        switch (mViewMode) {
	          case MODE_NORMAL:
	            if (newMode != MODE_NORMAL) {
	              DrawBoard(); 
	            }
	            break;
	          case MODE_MOVE_CARD:
	            mMoveCard.Release();
	            DrawBoard(); 
	            break;
	          case MODE_CARD_SELECT:
	            mSelectCard.Release();
	            DrawBoard(); 
	            break;
	          case MODE_TEXT:
	            mTextView.setVisibility(View.INVISIBLE);
	            break;
	          case MODE_ANIMATE:
	            mRefreshHandler.SetRefresh(RefreshHandler.SINGLE_REFRESH);
	            break;
	          case MODE_WIN:
	          case MODE_WIN_STOP:
	          case MODE_OPPONENT_STOP:
	          case MODE_MY_MOVE:
	          case MODE_OPPONENT_MOVES:
	            if (newMode != MODE_WIN_STOP) {
	              mTextView.setVisibility(View.INVISIBLE);
	            }
	            DrawBoard();
	            mReplay.StopPlaying();
	            break;
	        }
	        mViewMode = newMode;
	        switch (newMode) {
	          case MODE_WIN:
	            //SetTimePassing(false); put back
	          case MODE_MOVE_CARD:
	          case MODE_CARD_SELECT:
	          case MODE_ANIMATE:
	            mRefreshHandler.SetRefresh(RefreshHandler.LOCK_REFRESH);
	            break;

	          case MODE_NORMAL:
	          case MODE_TEXT:
	          case MODE_WIN_STOP:
	          case MODE_OPPONENT_STOP:
	            mRefreshHandler.SetRefresh(RefreshHandler.SINGLE_REFRESH);
	            break;
	        }
	      }
	    public void UpdateTime() {
	        if (!mTimePaused) {
	          int elapsed = (int)(SystemClock.uptimeMillis() - mStartTime);
	          if (elapsed / 1000 > mElapsed / 1000) {
	            Refresh();
	          }
	          mElapsed = elapsed;
	        }
	      }    
	    public void RefreshOptions() {
	        mRules.RefreshOptions();
	      }	
	    
	    public boolean onDown(float x, float y) {
	    	
	    	if (!mbDrawYourTurn)
	    		return false;
	    	
	    	mSelectCard.Clear();
	    	
	    	DrawBoard();
	    	
	        switch (mViewMode) {
	          case MODE_NORMAL:
	        	  
	            Card card = null;
	            //for (int i = 0; i < mCardAnchor.length; i++) {
	              card = mCardAnchor[CardAnchor.SOUTH].SelectCard(x, y);
	              if (card != null) {
	            	  
	                if (y < card.GetY() + Card.HEIGHT/4) {
	                  //boolean lastIgnore = mRules.GetIgnoreEvents();
	                  //mRules.SetIgnoreEvents(true);
	                  //mCardAnchor[i].AddCard(card);
	                 //mRules.SetIgnoreEvents(lastIgnore);
	                  //if (mCardAnchor[i].ExpandStack(x, y)) {
	                  //  mMoveCard.InitFromAnchor(mCardAnchor[i], x-Card.WIDTH/2, y-Card.HEIGHT/2);
	                  //ChangeViewMode(MODE_MOVE_CARD);
	                  //  break;
	                  //}
	                  //mCardAnchor[CardAnchor.SOUTH].RemoveCard(card);
	                }
	               // mMoveCard.SetAnchor(mCardAnchor[CardAnchor.SOUTH]);
	               // mMoveCard.AddCard(card);
		    		
				  	Card[] playableCards;
				  	playableCards = mRules.GetPlayableCards();	
				  	boolean bPlayable = false;
				  	
		    		switch (card.GetSuit()){
		    		case Card.HEARTS:
		    			
		    			// Card is a heart, can it be played?
		    			// Is it a seven, it can be played...
		    			if (card.GetValue() == 7){
		    				// and must be played first
		    				playableCards[Rules.HEARTS8] = new Card(8,Card.HEARTS);
		    				playableCards[Rules.HEARTS6] = new Card(6,Card.HEARTS);
		    				playableCards[Rules.CLUBS8] = new Card(7,Card.CLUBS);
		    				playableCards[Rules.DIAMONDS8] = new Card(7,Card.DIAMONDS);
		    				playableCards[Rules.SPADES8] = new Card(7,Card.SPADES);
		    				bPlayable = true;
		 
		    			}
		    			if (card.GetValue() < 7){
		    				if (card.Equals(playableCards[Rules.HEARTS6])){
		    					playableCards[Rules.HEARTS6] = new Card(card.GetValue()-1,Card.HEARTS);
		    					bPlayable = true;
		    				}
		    			}
		    			if (card.GetValue() > 7){
		    				if (card.Equals(playableCards[Rules.HEARTS8])){
		    					playableCards[Rules.HEARTS8] = new Card(card.GetValue()+1,Card.HEARTS);
		    					bPlayable = true;
		    				}
		    			}	    			
		    			
		    			break;
		    		case Card.DIAMONDS:
		    			// Is it a seven, it can be played...
		    			if (card.GetValue() == 7){
		    				
		    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
			    				playableCards[Rules.DIAMONDS8] = new Card(8,Card.DIAMONDS);
			    				playableCards[Rules.DIAMONDS6] = new Card(6,Card.DIAMONDS);	   
			    				bPlayable = true;
		    				}
		    			}
		    			
		    			if (card.GetValue() < 7){
		    				if (card.Equals(playableCards[Rules.DIAMONDS6])){
		    					playableCards[Rules.DIAMONDS6] = new Card(card.GetValue()-1,Card.DIAMONDS);
		    					bPlayable = true;
		    				}
		    			}
		    			if (card.GetValue() > 7){
		    				if (card.Equals(playableCards[Rules.DIAMONDS8])){
		    					playableCards[Rules.DIAMONDS8] = new Card(card.GetValue()+1,Card.DIAMONDS);
		    					bPlayable = true;
		    				}
		    			}		    			
		    			
		    			break;
		    			
		    		case Card.CLUBS:
		    			// Is it a seven, it can be played...
		    			if (card.GetValue() == 7){
		    				
		    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
			    				playableCards[Rules.CLUBS8] = new Card(8,Card.CLUBS);
			    				playableCards[Rules.CLUBS6] = new Card(6,Card.CLUBS);	  
			    				bPlayable = true;
		    				}	    				
		    			}
		    			
		    			if (card.GetValue() < 7){
		    				if (card.Equals(playableCards[Rules.CLUBS6])){
		    					playableCards[Rules.CLUBS6] = new Card(card.GetValue()-1,Card.CLUBS);
		    					bPlayable = true;
		    				}
		    			}
		    			if (card.GetValue() > 7){
		    				if (card.Equals(playableCards[Rules.CLUBS8])){
		    					playableCards[Rules.CLUBS8] = new Card(card.GetValue()+1,Card.CLUBS);
		    					bPlayable = true;
		    				}
		    			}		    			
		    			break;
		    			
		    		case Card.SPADES:
		    			// Is it a seven, it can be played...
		    			if (card.GetValue() == 7){
		    				
		    				if (playableCards[Rules.HEARTS8] != null && playableCards[Rules.HEARTS8].GetValue() > 7){
			    				playableCards[Rules.SPADES8] = new Card(8,Card.SPADES);
			    				playableCards[Rules.SPADES6] = new Card(6,Card.SPADES);	
			    				bPlayable = true;
		    				}	    				
		    			}
		    			
		    			if (card.GetValue() < 7){
		    				if (card.Equals(playableCards[Rules.SPADES6])){
		    					playableCards[Rules.SPADES6] = new Card(card.GetValue()-1,Card.SPADES);
		    					bPlayable = true;
		    				}
		    			}
		    			if (card.GetValue() > 7){
		    				if (card.Equals( playableCards[Rules.SPADES8])){
		    					playableCards[Rules.SPADES8] = new Card(card.GetValue()+1,Card.SPADES);
		    					bPlayable = true;
		    				}
		    			}	    			
		    			break;
		    			
		    		}	
		    		
		    		if (bPlayable){
		    			mGameStarted = true;
			    		Card[] MyCards;
			    		MyCards = mCardAnchor[CardAnchor.SOUTH].GetCards();
				  		for (int j = 0; j < MyCards.length; j++){
				  			if (MyCards[j] != null){
				  				MyCards[j].mbPlayable = false;
				  			}
				  		}	    			
				  		mbDrawYourTurn = false;
		    			PlayCard(card,CardAnchor.SOUTH);//mCardAnchor[CardAnchor.SOUTH].RemoveCard(card);
		    			miCurrentPlayer = 0;
		    			//RepositionHands();
		    			//Refresh();
	
		    		}
		    		else {
	    		    	String text = "Not a playable card!";
	    		    	Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();	
	    		    	mbDrawYourTurn = true;
		    		
		    		}
		    		
		    		break;
	              }
	            //}
	            break;
	          case MODE_CARD_SELECT:
	            mSelectCard.Tap(x, y);
	            break;
	        }
	        return true;
	      }	
	    
	    private boolean onRelease(float x, float y) {
	    	/*
	        switch (mViewMode) {
	          case MODE_NORMAL:
	            if (!mHasMoved) {
	              for (int i = 0; i < mCardAnchor.length; i++) {
	                if (mCardAnchor[i].ExpandStack(x, y)) {
	                  mSelectCard.InitFromAnchor(mCardAnchor[i]);
	                  ChangeViewMode(MODE_CARD_SELECT);
	                  return true;
	                } else if (mCardAnchor[i].TapCard(x, y)) {
	                  Refresh();
	                  return true;
	                }
	              }
	            }
	            break;
	          case MODE_MOVE_CARD:
	            for (int close = 0; close < 2; close++) {
	              CardAnchor prevAnchor = mMoveCard.GetAnchor();
	              boolean unhide = (prevAnchor.GetVisibleCount() == 0 &&
	                                prevAnchor.GetCount() > 0);
	              int count = mMoveCard.GetCount();

	              for (int i = 0; i < mCardAnchor.length; i++) {
	                if (mCardAnchor[i] != prevAnchor) {
	                  if (mCardAnchor[i].CanDropCard(mMoveCard, close)) {
	                    //mMoveHistory.push(new Move(prevAnchor.GetNumber(), i, count, false, unhide));
	                    mCardAnchor[i].AddMoveCard(mMoveCard);
	                    if (mViewMode == MODE_MOVE_CARD) {
	                      ChangeViewMode(MODE_NORMAL);
	                    }
	                    return true;
	                  }
	                }
	              }
	            }
	            if (!mMoveCard.HasMoved()) {
	              CardAnchor anchor = mMoveCard.GetAnchor();
	              mMoveCard.Release();
	              if (anchor.ExpandStack(x, y)) {
	                mSelectCard.InitFromAnchor(anchor);
	                ChangeViewMode(MODE_CARD_SELECT);
	              } else {
	                ChangeViewMode(MODE_NORMAL);
	              }
	            } else if (mSpeed.IsFast() && mMoveCard.GetCount() == 1) {
	              if (!mRules.Fling(mMoveCard)) {
	                ChangeViewMode(MODE_NORMAL);
	              }
	            } else {
	              mMoveCard.Release();
	              ChangeViewMode(MODE_NORMAL);
	            }
	            return true;
	          case MODE_CARD_SELECT:
	            if (!mSelectCard.IsOnCard() && !mHasMoved) {
	              mSelectCard.Release();
	              ChangeViewMode(MODE_NORMAL);
	              return true;
	            }
	            break;
	        }
*/
	        return true;
	      }	 
	    
	    public void SaveGame() {
	        // This is supposed to have been called but I've seen instances where it wasn't.
	        if (mRefreshThread != null) {
	          onPause();
	        }

	        if (mRules != null && mViewMode == MODE_NORMAL) {
	          try {

	            FileOutputStream fout = mContext.openFileOutput(SAVE_FILENAME, 0);
	            ObjectOutputStream oout = new ObjectOutputStream(fout);

	            int cardCount = mRules.GetCardCount();
	            int[] value = new int[cardCount];
	            int[] suit = new int[cardCount];
	            int[] anchorCardCount = new int[mCardAnchor.length];
	            int[] anchorHiddenCount = new int[mCardAnchor.length];
	            
	            int[] playableCardValue = new int[8];
	            int[] playableCardSuit =new int[8];


	            Card[] card;

	            cardCount = 0;
	            for (int i = 0; i < mCardAnchor.length; i++) {
	              anchorCardCount[i] = mCardAnchor[i].GetCount();
	              anchorHiddenCount[i] = mCardAnchor[i].GetHiddenCount();
	              card = mCardAnchor[i].GetCards();
	              for (int j = 0; j < anchorCardCount[i]; j++, cardCount++) {
	                value[cardCount] = card[j].GetValue();
	                suit[cardCount] = card[j].GetSuit();
	              }
	            }
	            
			  	Card[] playableCards;
			  	playableCards = mRules.GetPlayableCards();
	              for (int j = 0; j < 8; j++) {
	            	    if (playableCards[j] != null){
	            	    	playableCardValue[j] = playableCards[j].GetValue();
	            	    	playableCardSuit[j] = playableCards[j].GetSuit();
	            	    }
	            	    else
	            	    	playableCardValue[j] = 0;
		              }			  	

	            oout.writeObject(SAVE_VERSION);
	            oout.writeInt(mCardAnchor.length);
	            oout.writeInt(cardCount);
	            oout.writeInt(miCurrentPlayer);
	            oout.writeInt(mRules.GetType());
	            oout.writeObject(anchorCardCount);
	            oout.writeObject(anchorHiddenCount);
	            oout.writeObject(value);
	            oout.writeObject(suit);
	            oout.writeInt(mRules.GetRulesExtra());
	            oout.writeBoolean(mbDealDone);
	            oout.writeBoolean(mGameStarted);
	            oout.writeObject(playableCardValue);
	            oout.writeObject(playableCardSuit);	            
	            oout.writeObject(((NormalSevens)mRules).GetPlayers());

	            oout.close();

	            SharedPreferences.Editor editor = GetSettings().edit();
	            editor.putBoolean("SevensSaveValid", true);
	            editor.commit();

	          } catch (FileNotFoundException e) {
	            Log.e("SevensView.java", "onStop(): File not found");
	          } catch (IOException e) {
	            Log.e("SevensView.java", "onStop(): IOException");
	          }
	        }
	      }	    
	    
    public boolean LoadSave() {
        mDrawMaster.DrawCards(GetSettings().getBoolean("DisplayBigCards", false));
        mDrawMaster.mbShowPlayable = (GetSettings().getBoolean("DisplayPlayableCards", true));
        mTimePaused = true;

        try {
          FileInputStream fin = mContext.openFileInput(SAVE_FILENAME);
          ObjectInputStream oin = new ObjectInputStream(fin);
          
          String version = (String)oin.readObject();
          if (!version.equals(SAVE_VERSION)) {
            Log.e("SevensView.java", "Invalid save version");
            return false;
          }
          Bundle map = new Bundle();
            
          map.putInt("cardAnchorCount", oin.readInt());
          map.putInt("cardCount", oin.readInt());
          map.putInt("currentPlayer", oin.readInt());
          int type = oin.readInt();
          map.putIntArray("anchorCardCount", (int[])oin.readObject());
          map.putIntArray("anchorHiddenCount", (int[])oin.readObject());
          map.putIntArray("value", (int[])oin.readObject());
          map.putIntArray("suit", (int[])oin.readObject());
          map.putInt("rulesExtra", oin.readInt());
          mbDealDone = oin.readBoolean();
          mGameStarted = oin.readBoolean();
          map.putIntArray("playableCardValue", (int[])oin.readObject());
          map.putIntArray("playableCardSuit", (int[])oin.readObject());  
          map.putSerializable("players", (Serializable)oin.readObject());

          oin.close();
          //mGameStarted = true;
          //mbDealDone = true;
          mRules = Rules.CreateRules(type, map, this, /*mMoveHistory,*/ mAnimateCard,false, null);
          Card.SetSize(type);
          mCardAnchor = mRules.GetAnchorArray();
          if (mDrawMaster.GetWidth() > 1) {
            mRules.Resize(mDrawMaster.GetWidth(), mDrawMaster.GetHeight());
            Refresh();
          }

 
		    if (miCurrentPlayer == CardAnchor.SOUTH){
			  	Card[] MyCards;
			  	MyCards = mCardAnchor[CardAnchor.SOUTH].GetCards();
			  	for (int i = 0; i < 4; i++)
			  		mCardAnchor[i].mbKnock = false;
		  		for (int j = 0; j < MyCards.length; j++){
		  			if (MyCards[j] != null){
		  				MyCards[j].mbPlayable = false;
		  			}
		  		}			  	
			  	Card[] playableCards;
			  	playableCards = GetRules().GetPlayableCards();
			  	if (playableCards[0] == null)
			  		playableCards[0] = new Card(7,Card.HEARTS);			  	
			  	mbPlayable = false;
			  	for (int i = 0; i < playableCards.length; i++){
			  		for (int j = 0; j < MyCards.length; j++){
			  			if (playableCards[i] != null && MyCards[j] != null && playableCards[i].Equals(MyCards[j])){
			  				//mView.mSelectCard.AddCard(MyCards[j]);
			  				MyCards[j].mbPlayable = true;
			  				mbPlayable = true;
			  			}
			  		}
			  		
			  	}		     
		     
			  mbDrawYourTurn = true;
		      if (!mbPlayable){
		    	  miCurrentPlayer = 0;	
				  	MyCards = mCardAnchor[CardAnchor.SOUTH].GetCards();		    	  
			  		for (int j = 0; j < MyCards.length; j++){
			  			if (MyCards[j] != null){
			  				MyCards[j].mbPlayable = false;
			  			}
			  		}		    	  
			      String text = "You are knocking!";
			      if ((GetSettings().getBoolean("AutoKnock", true)))
			    	  Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
			      else {

			      }
		      }		    	
		    	
		    } 
		    else
		    	PlayGame();
          
          mTimePaused = false;
          return true;
          
        } catch (FileNotFoundException e) {
          Log.e("SevensView.java", "LoadSave(): File not found");
        } catch (StreamCorruptedException e) {
          Log.e("SevensView.java", "LoadSave(): Stream Corrupted");
        } catch (IOException e) {
          Log.e("SevensView.java", "LoadSave(): IOException");
        } catch (ClassNotFoundException e) {
          Log.e("SevensView.java", "LoadSave(): Class not found exception");
        }
        mTimePaused = false;
        mPaused = false;
        return false;
      } 
	    
}
class RefreshHandler implements Runnable {
	  public static final int NO_REFRESH = 1;
	  public static final int SINGLE_REFRESH = 2;
	  public static final int LOCK_REFRESH = 3;

	  private static final int FPS = 30;

	  private boolean mRun;
	  private int mRefresh;
	  private SevensView mView;

	  public RefreshHandler(SevensView sevensView) {
	    mView = sevensView;
	    mRun = true;
	    mRefresh = NO_REFRESH;
	  }

	  public void SetRefresh(int refresh) {
	    synchronized (this) {
	      mRefresh = refresh;
	    }
	  }

	  public void SingleRefresh() {
	    synchronized (this) {
	      if (mRefresh == NO_REFRESH) {
	        mRefresh = SINGLE_REFRESH;
	      }
	    }
	  }

	  public void SetRunning(boolean run) {
	    mRun = run;
	  }

	  @Override
	public void run() {
	    while (mRun) {
	      try {
	        Thread.sleep(1000 / FPS);
	      } catch (InterruptedException e) {
	      }
	      mView.UpdateTime(); 
	      if (mRefresh != NO_REFRESH) {
	        mView.postInvalidate();
	        if (mRefresh == SINGLE_REFRESH) {
	          SetRefresh(NO_REFRESH);
	        }
	      }
	    }
	  }
	}

	class Speed {
	  private static final int SPEED_COUNT = 4;
	  private static final float SPEED_THRESHOLD = 10*10;
	  private float[] mSpeed;
	  private int mIdx;

	  public Speed() {
	    mSpeed = new float[SPEED_COUNT];
	    Reset();
	  }
	  public void Reset() {
	    mIdx = 0;
	    for (int i = 0; i < SPEED_COUNT; i++) {
	      mSpeed[i] = 0;
	    }
	  }
	  public void AddSpeed(float dx, float dy) {
	    mSpeed[mIdx] = dx*dx + dy*dy;
	    mIdx = (mIdx + 1) % SPEED_COUNT;
	  }
	  public boolean IsFast() {
	    for (int i = 0; i < SPEED_COUNT; i++) {
	      if (mSpeed[i] > SPEED_THRESHOLD) {
	        return true;
	      }
	    }
	    return false;
	  }
	}

