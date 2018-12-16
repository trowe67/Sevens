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

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import au.tonyrowe.sevens.R;


public class DrawMaster {

  private Context mContext;
  public boolean mbShowPlayable;
  
  private static final int ButtonWidth   = 70;
  private static final int ButtonHeight   = 70;
  // Background
  private int mScreenWidth;
  private int mScreenHeight;
  private Paint mBGPaint;

  // Card stuff
  private final Paint mSuitPaint = new Paint();
  private Bitmap[] mCardBitmap;
  private Bitmap mCardHidden;

  private Paint mEmptyAnchorPaint;
  private Paint mDoneEmptyAnchorPaint;
  private Paint mShadePaint;
  private Paint mLightShadePaint;
  private Paint mNonPlayablePaint;

  
  private Paint mTimePaint;
  private Paint mKnockPaint;
  private int mLastSeconds;
  private String mTimeString;
  

  private Bitmap mBoardBitmap;
  private Canvas mBoardCanvas;
  

  public DrawMaster(Context context) {

    mContext = context;
    mbShowPlayable = true;
    // Default to this for simplicity
    mScreenWidth = 480;
    mScreenHeight = 295;
    mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
    mScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels;    

    // Background
    mBGPaint = new Paint();
    mBGPaint.setARGB(255, 0, 128, 0);

    mShadePaint = new Paint();
    mShadePaint.setARGB(200, 0, 0, 0);

    mLightShadePaint = new Paint();
    mLightShadePaint.setARGB(100, 0, 0, 0);

    // Card related stuff
    mEmptyAnchorPaint = new Paint();
    mEmptyAnchorPaint.setARGB(255, 0, 64, 0);
    mDoneEmptyAnchorPaint = new Paint();
    mDoneEmptyAnchorPaint.setARGB(128, 255, 0, 0);
    mNonPlayablePaint = new Paint();
    mNonPlayablePaint.setARGB(145, 0, 0, 0);   

    mTimePaint = new Paint();
    mTimePaint.setTextSize(18);
    mTimePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
    mTimePaint.setTextAlign(Paint.Align.RIGHT);
    mTimePaint.setAntiAlias(true);
    
    mKnockPaint = new Paint();
    mKnockPaint.setTextSize(14);
    mKnockPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD));
    mKnockPaint.setTextAlign(Paint.Align.LEFT);
    mKnockPaint.setAntiAlias(true);    
    mLastSeconds = -1;

    mCardBitmap = new Bitmap[52];
    DrawCards(false);
    mBoardBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
    mBoardCanvas = new Canvas(mBoardBitmap);
    
  }

  public int GetWidth() { return mScreenWidth; }
  public int GetHeight() { return mScreenHeight; }
  public Canvas GetBoardCanvas() { return mBoardCanvas; }
  public void SetShowPlayable(boolean bShowPlayable){mbShowPlayable = bShowPlayable;}

  public void DrawCard(Canvas canvas, Card card) {
    float x = card.GetX();
    float y = card.GetY();
    int idx = card.GetSuit()*13+(card.GetValue()-1);
    int iUp = 8;
    float fCardSF = 1.0f;
    if(mScreenWidth > 2 * 295.0f)
    	fCardSF = 2.0f;
    if (card.mbPlayable && mbShowPlayable)
    	canvas.drawBitmap(mCardBitmap[idx], x, y-(iUp*fCardSF), mSuitPaint);
    else
    	canvas.drawBitmap(mCardBitmap[idx], x, y, mSuitPaint);
  }

  public void DrawHiddenCard(Canvas canvas, Card card) {
    float x = card.GetX();
    float y = card.GetY();
    canvas.drawBitmap(mCardHidden, x, y, mSuitPaint);
  }

  public void DrawEmptyAnchor(Canvas canvas, float x, float y, boolean done) {
    RectF pos = new RectF(x, y, x + Card.WIDTH, y + Card.HEIGHT);
    if (!done) {
      canvas.drawRoundRect(pos, 4, 4, mEmptyAnchorPaint);
    } else {
      canvas.drawRoundRect(pos, 4, 4, mDoneEmptyAnchorPaint);
    }
  }

  public void DrawBackground(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mBGPaint);
  }

  public void DrawShade(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mShadePaint);
  }

  public void DrawLightShade(Canvas canvas) {
    canvas.drawRect(0, 0, mScreenWidth, mScreenHeight, mLightShadePaint);
  }

  public void DrawLastBoard(Canvas canvas) {
    canvas.drawBitmap(mBoardBitmap, 0, 0, mSuitPaint);
  }

  public void SetScreenSize(int width, int height) {
    mScreenWidth = width;
    mScreenHeight = height;
    float fCardSF = 1.0f;
    if(mScreenWidth > (2 * 295.0f))
    	fCardSF = 2.0f;
 
    mKnockPaint.setTextSize(14*fCardSF);
    mBoardBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
    mBoardCanvas = new Canvas(mBoardBitmap);
  }

  public void DrawCards(boolean bigCards) {
    if (bigCards) {
      DrawBigCards(mContext.getResources());
    } else {
      DrawCards(mContext.getResources());
    }
  }

  private void DrawBigCards(Resources r) {

    Paint cardFrontPaint = new Paint();
    Paint cardBorderPaint = new Paint();
    Bitmap[] bigSuit = new Bitmap[4];
    Bitmap[] suit = new Bitmap[4];
    Bitmap[] blackFont = new Bitmap[13];
    Bitmap[] redFont = new Bitmap[13];
    Canvas canvas;
    float fCardSF = GetWidth()/480.0f;
    
    fCardSF = 125.0f/50.0f;

    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE) 
    	fCardSF = 75.0f/50.0f;
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL) 
    	fCardSF = 1.0f;
    	 
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL) 
    	fCardSF = 1.0f;
    	   
    int width = (int)(fCardSF * Card.WIDTH);
    int height = (int)(fCardSF * Card.HEIGHT); 

    
    Drawable drawable = r.getDrawable(R.drawable.cardbackbluelarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.cardbackbluelarge);   
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.cardbackblue);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.cardbackblue); 

    mCardHidden = Bitmap.createBitmap(width, height,
                                      Bitmap.Config.ARGB_4444);
    mCardHidden.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    canvas = new Canvas(mCardHidden);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);

    drawable = r.getDrawable(R.drawable.suitslarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.suitslarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.suits);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.suits);    

    int suitwidth = (int)(10*fCardSF);
    int suitheight = (int)(10*fCardSF);
    
    for (int i = 0; i < 4; i++) {
      suit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      suit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(suit[i]);
      drawable.setBounds(-i*suitwidth, 0, -i*suitwidth+4*suitwidth, suitheight);
      drawable.draw(canvas);
    }

    drawable = r.getDrawable(R.drawable.bigsuitslarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.bigsuitslarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.bigsuits);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.bigsuits);     
 
    suitwidth = (int)(25*fCardSF);
    suitheight = (int)(25*fCardSF);    
    for (int i = 0; i < 4; i++) {
      bigSuit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      bigSuit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(bigSuit[i]);
      drawable.setBounds(-i*suitwidth, 0, -i*suitwidth+4*suitwidth, suitheight);
      drawable.draw(canvas);
    }

    int fontwidth = (int)(18*fCardSF);
    int fontheight = (int)(23*fCardSF); 
    drawable = r.getDrawable(R.drawable.bigblackfontlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.bigblackfontlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.bigblackfont);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)      
    	drawable = r.getDrawable(R.drawable.bigblackfont);
    for (int i = 0; i < 13; i++) {
      blackFont[i] = Bitmap.createBitmap(fontwidth, fontheight, Bitmap.Config.ARGB_4444);
      blackFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(blackFont[i]);
      drawable.setBounds(-i*fontwidth, 0, -i*fontwidth+13*fontwidth, fontheight);
      drawable.draw(canvas);
    }

    drawable = r.getDrawable(R.drawable.bigredfontlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.bigredfontlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.bigredfont);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)      
    	drawable = r.getDrawable(R.drawable.bigredfont);    
    for (int i = 0; i < 13; i++) {
      redFont[i] = Bitmap.createBitmap(fontwidth, fontheight, Bitmap.Config.ARGB_4444);
      redFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(redFont[i]);
      drawable.setBounds(-i*fontwidth, 0, -i*fontwidth+13*fontwidth, fontheight);
      drawable.draw(canvas);
    }

    cardBorderPaint.setARGB(255, 0, 0, 0);
    cardFrontPaint.setARGB(255, 255, 255, 255);
    RectF pos = new RectF();
    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_4444);
        mCardBitmap[suitIdx*13+valueIdx].setDensity(DisplayMetrics.DENSITY_MEDIUM);
        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
        pos.set(0, 0, width, height);
        canvas.drawRoundRect(pos, 4, 4, cardBorderPaint);
        pos.set(1, 1, width-1, height-1);
        canvas.drawRoundRect(pos, 4, 4, cardFrontPaint);
        if ((suitIdx & 1) == 1) {
          canvas.drawBitmap(redFont[valueIdx], 3, 4, mSuitPaint);
        } else {
          canvas.drawBitmap(blackFont[valueIdx], 3, 4, mSuitPaint);
        }

        canvas.drawBitmap(suit[suitIdx], width-14*fCardSF, 4*fCardSF, mSuitPaint);
        canvas.drawBitmap(bigSuit[suitIdx], width/2-12*fCardSF, height/2-8*fCardSF, mSuitPaint);
      }
    }
  }

  private void DrawCards(Resources r) {

    Paint cardFrontPaint = new Paint();
    Paint cardBorderPaint = new Paint();
    Bitmap[] suit = new Bitmap[4];
    Bitmap[] revSuit = new Bitmap[4];
    Bitmap[] smallSuit = new Bitmap[4];
    Bitmap[] revSmallSuit = new Bitmap[4];
    Bitmap[] blackFont = new Bitmap[13];
    Bitmap[] revBlackFont = new Bitmap[13];
    Bitmap[] redFont = new Bitmap[13];
    Bitmap[] revRedFont = new Bitmap[13];
    Bitmap diaJack;
    Bitmap diaRevJack;
    Bitmap diaQueen;
    Bitmap diaRevQueen;
    Bitmap diaKing;
    Bitmap diaRevKing;
    Bitmap heaJack;
    Bitmap heaRevJack;
    Bitmap heaQueen;
    Bitmap heaRevQueen;
    Bitmap heaKing;
    Bitmap heaRevKing;    
    Bitmap cluJack;
    Bitmap cluRevJack;
    Bitmap cluQueen;
    Bitmap cluRevQueen;
    Bitmap cluKing;
    Bitmap cluRevKing;
    Bitmap spaJack;
    Bitmap spaRevJack;
    Bitmap spaQueen;
    Bitmap spaRevQueen;
    Bitmap spaKing;
    Bitmap spaRevKing;
    Canvas canvas;
    
   float fCardSF = GetWidth()/480.0f;
    
    fCardSF = 125.0f/50.0f;

    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE) 
    	fCardSF = 75.0f/50.0f;
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL) 
    	fCardSF = 1.0f;
    	 
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL) 
    	fCardSF = 1.0f;
    	   
    int width = (int)(fCardSF * Card.WIDTH);
    int height = (int)(fCardSF * Card.HEIGHT); 
    int fontWidth;
    int fontHeight;
    
    float[] faceBox = { 9*fCardSF,8*fCardSF,width-10*fCardSF,8*fCardSF,
            width-10*fCardSF,8*fCardSF,width-10*fCardSF,height-8*fCardSF,
            width-10*fCardSF,height-8*fCardSF,9*fCardSF,height-8*fCardSF,
            9*fCardSF,height-8*fCardSF,9*fCardSF,8*fCardSF
          };
    
    Drawable drawable = r.getDrawable(R.drawable.cardbackbluelarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.cardbackbluelarge);   
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.cardbackblue);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.cardbackblue);      
    

    mCardHidden = Bitmap.createBitmap(width, height,
                                      Bitmap.Config.ARGB_4444);
    //DisplayMetrics metrics = new DisplayMetrics();
    //getWindowManager().getDefaultDisplay().getMetrics(metrics);
    mCardHidden.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    canvas = new Canvas(mCardHidden);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);
    

    drawable = r.getDrawable(R.drawable.suitslarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.suitslarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.suits);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.suits);   
    
    int suitwidth = (int)(10*fCardSF);
    int suitheight = (int)(10*fCardSF);
    for (int i = 0; i < 4; i++) {
      suit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      revSuit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      suit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      revSuit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(suit[i]);
      drawable.setBounds(-i*suitwidth, 0, -i*suitwidth+4*suitwidth, suitheight);
      drawable.draw(canvas);
      canvas = new Canvas(revSuit[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*suitwidth-suitwidth, -suitheight, -i*suitwidth+3*suitwidth, 0);
      drawable.draw(canvas);
    }

    drawable = r.getDrawable(R.drawable.smallsuitslarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.smallsuitslarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.smallsuits);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.smallsuits);  
    suitwidth = suitwidth/2;
    suitheight = suitheight/2;
    for (int i = 0; i < 4; i++) {
      smallSuit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      revSmallSuit[i] = Bitmap.createBitmap(suitwidth, suitheight, Bitmap.Config.ARGB_4444);
      smallSuit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      revSmallSuit[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(smallSuit[i]);
      drawable.setBounds(-i*suitwidth, 0, -i*suitwidth+4*suitwidth, suitheight);
      drawable.draw(canvas);
      canvas = new Canvas(revSmallSuit[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*suitwidth-suitwidth, -suitheight, -i*suitwidth+3*suitwidth, 0);
      drawable.draw(canvas);
    }
    
    drawable = r.getDrawable(R.drawable.medblackfontlarge); 
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.medblackfontlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.medblackfont);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.medblackfont);    
    fontWidth = (int)(7*fCardSF);
    fontHeight = (int)(9*fCardSF);
    for (int i = 0; i < 13; i++) {
      blackFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      revBlackFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      blackFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      revBlackFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      canvas = new Canvas(blackFont[i]);
      drawable.setBounds(-i*fontWidth, 0, -i*fontWidth+13*fontWidth, fontHeight);
      drawable.draw(canvas);
      canvas = new Canvas(revBlackFont[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*fontWidth-fontWidth, -fontHeight, -i*fontWidth+(12*fontWidth), 0);
      drawable.draw(canvas);
    }

    drawable = r.getDrawable(R.drawable.medredfontlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.medredfontlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.medredfont);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.medredfont);     
    for (int i = 0; i < 13; i++) {
      redFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      revRedFont[i] = Bitmap.createBitmap(fontWidth, fontHeight, Bitmap.Config.ARGB_4444);
      redFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);
      revRedFont[i].setDensity(DisplayMetrics.DENSITY_MEDIUM);      
      canvas = new Canvas(redFont[i]);
      drawable.setBounds(-i*fontWidth, 0, -i*fontWidth+13*fontWidth, fontHeight);
      drawable.draw(canvas);
      canvas = new Canvas(revRedFont[i]);
      canvas.rotate(180);
      drawable.setBounds(-i*fontWidth-fontWidth, -fontHeight, -i*fontWidth+(12*fontWidth), 0);
      drawable.draw(canvas);
    }

    int faceWidth = (int)((Card.WIDTH - 20)*fCardSF);
    int faceHeight = (int)((Card.HEIGHT/2 - 9)*fCardSF);
    
    drawable = r.getDrawable(R.drawable.diamondjacklarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.diamondjacklarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.diamondjack);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.diamondjack);    
    diaJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    diaRevJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(diaJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(diaRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.heartjacklarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.heartjacklarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.heartjack);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.heartjack);    
    heaJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    heaRevJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(heaJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(heaRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);    

    drawable = r.getDrawable(R.drawable.diamondqueenlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.diamondqueenlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.diamondqueen);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.diamondqueen);    
    diaQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    diaRevQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(diaQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(diaRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.heartqueenlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.heartqueenlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.heartqueen);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.heartqueen);    
    heaQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    heaRevQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(heaQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(heaRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas); 

    drawable = r.getDrawable(R.drawable.diamondkinglarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.diamondkinglarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.diamondking);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.diamondking);    
    diaKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    diaKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    diaRevKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(diaKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(diaRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.heartkinglarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.heartkinglarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.heartking);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.heartking);    
    heaKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    heaKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    heaRevKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(heaKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(heaRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
 /// black pictures
    
    drawable = r.getDrawable(R.drawable.clubjacklarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.clubjacklarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.clubjack);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.clubjack);    
    cluJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    cluRevJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(cluJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(cluRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.spadejacklarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.spadejacklarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.spadejack);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.spadejack);    
    spaJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaRevJack = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    spaRevJack.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(spaJack);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(spaRevJack);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);    
    
    drawable = r.getDrawable(R.drawable.clubqueenlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.clubqueenlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.clubqueen);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.clubqueen);    
    cluQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    cluRevQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(cluQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(cluRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.spadequeenlarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.spadequeenlarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.spadequeen);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.spadequeen);    
    spaQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaRevQueen = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    spaRevQueen.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(spaQueen);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(spaRevQueen);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);    

    drawable = r.getDrawable(R.drawable.clubkinglarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.clubkinglarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.clubking);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.clubking);    
    cluKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    cluKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    cluRevKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(cluKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(cluRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);
    
    drawable = r.getDrawable(R.drawable.spadekinglarge);
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_LARGE)
    	drawable = r.getDrawable(R.drawable.spadekinglarge);
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_NORMAL)
    	drawable = r.getDrawable(R.drawable.spadeking);  
    
    if ((r.getConfiguration().screenLayout & 
    	    Configuration.SCREENLAYOUT_SIZE_MASK) == 
    	        Configuration.SCREENLAYOUT_SIZE_SMALL)
    	drawable = r.getDrawable(R.drawable.spadeking);    
    spaKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaRevKing = Bitmap.createBitmap(faceWidth, faceHeight, Bitmap.Config.ARGB_4444);
    spaKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);
    spaRevKing.setDensity(DisplayMetrics.DENSITY_MEDIUM);    
    canvas = new Canvas(spaKing);
    drawable.setBounds(0, 0, faceWidth, faceHeight);
    drawable.draw(canvas);
    canvas = new Canvas(spaRevKing);
    canvas.rotate(180);
    drawable.setBounds(-faceWidth, -faceHeight, 0, 0);
    drawable.draw(canvas);    


    cardBorderPaint.setARGB(255, 0, 0, 0);
    cardFrontPaint.setARGB(255, 255, 255, 255);
    RectF pos = new RectF();
    for (int suitIdx = 0; suitIdx < 4; suitIdx++) {
      for (int valueIdx = 0; valueIdx < 13; valueIdx++) {
        mCardBitmap[suitIdx*13+valueIdx] = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_4444);
        mCardBitmap[suitIdx*13+valueIdx].setDensity(DisplayMetrics.DENSITY_MEDIUM);
        canvas = new Canvas(mCardBitmap[suitIdx*13+valueIdx]);
        pos.set(0, 0, width, height);
        canvas.drawRoundRect(pos, 4, 4, cardBorderPaint);
        pos.set(1, 1, width-1, height-1);
        canvas.drawRoundRect(pos, 4, 4, cardFrontPaint);

        if ((suitIdx & 1) == 1) {
            canvas.drawBitmap(redFont[valueIdx], null, new RectF(2*fCardSF, 4*fCardSF, 2*fCardSF+fontWidth, 4*fCardSF+fontHeight), mSuitPaint);
            canvas.drawBitmap(revRedFont[valueIdx], null, new RectF(width-fontWidth-2*fCardSF, height-fontHeight-4*fCardSF, width-fontWidth-2*fCardSF+fontWidth,height-fontHeight-4*fCardSF+fontHeight),
                              mSuitPaint);
          } else {
            canvas.drawBitmap(blackFont[valueIdx], null, new RectF(2*fCardSF, 4*fCardSF, 2*fCardSF+fontWidth, 4*fCardSF+fontHeight), mSuitPaint);
            canvas.drawBitmap(revBlackFont[valueIdx], null, new RectF(width-fontWidth-2*fCardSF, height-fontHeight-4*fCardSF, width-fontWidth-2*fCardSF+fontWidth,height-fontHeight-4*fCardSF+fontHeight),
                              mSuitPaint);
          }
          if (fontWidth > 6) {
            canvas.drawBitmap(smallSuit[suitIdx], null, new RectF(3*fCardSF, 5*fCardSF+fontHeight, 8*fCardSF, 10*fCardSF+fontHeight), mSuitPaint);
            canvas.drawBitmap(revSmallSuit[suitIdx], null, new RectF(width-7*fCardSF, height-11*fCardSF-fontHeight,width-7*fCardSF + 5*fCardSF, height-11*fCardSF-fontHeight + 5*fCardSF),
                              mSuitPaint);
          } else {
            canvas.drawBitmap(smallSuit[suitIdx], null, new RectF(2*fCardSF, 5*fCardSF+fontHeight, 2*fCardSF + 5*fCardSF, 5*fCardSF+fontHeight + 5*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSmallSuit[suitIdx], null, new RectF(width-6*fCardSF, height-11*fCardSF-fontHeight, width-6*fCardSF + 5*fCardSF, height-11*fCardSF-fontHeight + 5*fCardSF),
                              mSuitPaint);
          }

          if (valueIdx >= 10) {
            canvas.drawBitmap(suit[suitIdx], null, new RectF(10*fCardSF, 9*fCardSF, 20*fCardSF, 19*fCardSF),mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(width-21*fCardSF, height-20*fCardSF,width-21*fCardSF+10*fCardSF,height-20*fCardSF+10*fCardSF),
                              mSuitPaint);
          }

          int[] suitX = {(int)(10*fCardSF),width/2-(int)(5*fCardSF),width-(int)(20*fCardSF)};
          int[] suitY = {(int)(7*fCardSF),(int)(2*height/5-5*fCardSF),(int)(3*height/5-5*fCardSF),(int)(height-18*fCardSF)};
          int suitMidY = height/2 - (int)(6*fCardSF);
        switch (valueIdx+1) {
        case 1:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitMidY, suitX[1] + 10*fCardSF, suitMidY + 10*fCardSF),  mSuitPaint);
            break;
          case 2:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitY[0], suitX[1] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[1], suitY[3], suitX[1] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 3:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitY[0], suitX[1] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitMidY, suitX[1] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[1], suitY[3], suitX[1] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 4:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitY[0], suitX[0] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitY[0], suitX[2] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[0], suitY[3], suitX[0] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[2], suitY[3], suitX[2] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 5:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitY[0], suitX[0] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitY[0], suitX[2] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitMidY, suitX[1] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[0], suitY[3], suitX[0] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[2], suitY[3], suitX[2] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 6:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitY[0], suitX[0] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitY[0], suitX[2] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitMidY, suitX[0] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitMidY, suitX[2] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[0], suitY[3], suitX[0] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[2], suitY[3], suitX[2] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 7:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitY[0], suitX[0] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitY[0], suitX[2] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitMidY, suitX[0] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitMidY, suitX[2] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], (suitMidY+suitY[0])/2, suitX[1] + 10*fCardSF, (suitMidY+suitY[0])/2 + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[0], suitY[3], suitX[0] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[2], suitY[3], suitX[2] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            break;
          case 8:
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitY[0], suitX[0] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitY[0], suitX[2] + 10*fCardSF, suitY[0] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[0], suitMidY, suitX[0] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[2], suitMidY, suitX[2] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], (suitMidY+suitY[0])/2, suitX[1] + 10*fCardSF, (suitMidY+suitY[0])/2 + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[0], suitY[3], suitX[0] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[2], suitY[3], suitX[2] + 10*fCardSF, suitY[3] + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[1], (suitY[3]+suitMidY)/2, suitX[1] + 10*fCardSF, (suitY[3]+suitMidY)/2 + 10*fCardSF), mSuitPaint);
            break;
          case 9:
            for (int i = 0; i < 4; i++) {
              canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[(i%2)*2], suitY[i/2], suitX[(i%2)*2] + 10*fCardSF, suitY[i/2] + 10*fCardSF), mSuitPaint);
              canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[(i%2)*2], suitY[i/2+2], suitX[(i%2)*2] + 10*fCardSF, suitY[i/2+2] + 10*fCardSF), mSuitPaint);
            }
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], suitMidY, suitX[1] + 10*fCardSF, suitMidY + 10*fCardSF), mSuitPaint);
            break;
          case 10:
            for (int i = 0; i < 4; i++) {
              canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[(i%2)*2], suitY[i/2], suitX[(i%2)*2] + 10*fCardSF, suitY[i/2] + 10*fCardSF), mSuitPaint);
              canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[(i%2)*2], suitY[i/2+2], suitX[(i%2)*2] + 10*fCardSF, suitY[i/2+2] + 10*fCardSF), mSuitPaint);
            }
            canvas.drawBitmap(suit[suitIdx], null, new RectF(suitX[1], (suitY[1]+suitY[0])/2, suitX[1] + 10*fCardSF, (suitY[1]+suitY[0])/2 + 10*fCardSF), mSuitPaint);
            canvas.drawBitmap(revSuit[suitIdx], null, new RectF(suitX[1], (suitY[3]+suitY[2])/2, suitX[1] + 10*fCardSF, (suitY[3]+suitY[2])/2 + 10*fCardSF), mSuitPaint);
            break;

          case Card.JACK:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
              if (suitIdx == 1){
            	  canvas.drawBitmap(diaJack, 10*fCardSF, 9*fCardSF, mSuitPaint);
            	  canvas.drawBitmap(diaRevJack, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
              }
              else {
            	  canvas.drawBitmap(heaJack, 10*fCardSF, 9*fCardSF, mSuitPaint);
            	  canvas.drawBitmap(heaRevJack, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
              }
            } else {
                if (suitIdx == 0){
              	  canvas.drawBitmap(cluJack, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(cluRevJack, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
                else {
              	  canvas.drawBitmap(spaJack, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(spaRevJack, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
            }
            break;
          case Card.QUEEN:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
                if (suitIdx == 1){
              	  canvas.drawBitmap(diaQueen, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(diaRevQueen, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
                else {
              	  canvas.drawBitmap(heaQueen, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(heaRevQueen, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
            } else {
                if (suitIdx == 0){
                	  canvas.drawBitmap(cluQueen, 10*fCardSF, 9*fCardSF, mSuitPaint);
                	  canvas.drawBitmap(cluRevQueen, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                  }
                  else {
                	  canvas.drawBitmap(spaQueen, 10*fCardSF, 9*fCardSF, mSuitPaint);
                	  canvas.drawBitmap(spaRevQueen, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                  }
            }
            break;
          case Card.KING:
            canvas.drawLines(faceBox, cardBorderPaint);
            if ((suitIdx & 1) == 1) {
                if (suitIdx == 1){
                	  canvas.drawBitmap(diaKing, 10*fCardSF, 9*fCardSF, mSuitPaint);
                	  canvas.drawBitmap(diaRevKing, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                  }
                  else {
                	  canvas.drawBitmap(heaKing, 10*fCardSF, 9*fCardSF, mSuitPaint);
                	  canvas.drawBitmap(heaRevKing, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                  }
            } else {
                if (suitIdx == 0){
              	  canvas.drawBitmap(cluKing, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(cluRevKing, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
                else {
              	  canvas.drawBitmap(spaKing, 10*fCardSF, 9*fCardSF, mSuitPaint);
              	  canvas.drawBitmap(spaRevKing, 10*fCardSF, height-faceHeight-9*fCardSF, mSuitPaint);
                }
            }
            break;
        }
      }
    }
  }
  public void DrawKnock(Canvas canvas, int x, int y) {

	    float fCardSF = 1.0f;
	    if(mScreenWidth > (2 * 295.0f))
	    	fCardSF = 2.0f;
	 
	    mKnockPaint.setTextSize(14*fCardSF);
	    mKnockPaint.setARGB(255, 255, 255, 255);
	    canvas.drawText("Knock!", x, y, mKnockPaint);
	    //mTimePaint.setARGB(255, 0, 0, 0);
	    //canvas.drawText("Knock", x-1, y-1, mTimePaint);
}
  
  public void DrawYourTurn(Canvas canvas, int x, int y) {
	  if (mbShowPlayable)
		  return;
	    mKnockPaint.setARGB(255, 255, 0, 255);
	    canvas.drawText("Your Turn...", x, y, mKnockPaint);
	    //mTimePaint.setARGB(255, 0, 0, 0);
	    //canvas.drawText("Knock", x-1, y-1, mTimePaint);
}  
  
  public void DrawScore(int Score, Canvas canvas, int x, int y) {

	    float fCardSF = 1.0f;
	    if(mScreenWidth > (2 * 295.0f))
	    	fCardSF = 2.0f;
	 
	    mKnockPaint.setTextSize(14*fCardSF);	  
	    mKnockPaint.setARGB(255, 255, 255, 255);
	    canvas.drawText("" + Score, x, y, mKnockPaint);
	    //mTimePaint.setARGB(255, 0, 0, 0);
	    //canvas.drawText("Knock", x-1, y-1, mTimePaint);
}  
  
  public void DrawTime(Canvas canvas, int millis) {
    int seconds = (millis / 1000) % 60;
    int minutes = millis / 60000;
    if (seconds != mLastSeconds) {
      mLastSeconds = seconds;
      // String.format is insanely slow (~15ms)
      if (seconds < 10) {
        mTimeString = minutes + ":0" + seconds;
      } else {
        mTimeString = minutes + ":" + seconds;
      }
    }
    mTimePaint.setARGB(255, 20, 20, 20);
    canvas.drawText(mTimeString, mScreenWidth-9, mScreenHeight-9, mTimePaint);
    mTimePaint.setARGB(255, 0, 0, 0);
    canvas.drawText(mTimeString, mScreenWidth-10, mScreenHeight-10, mTimePaint);
  }

  public void DrawRulesString(Canvas canvas, String score) {
    mTimePaint.setARGB(255, 20, 20, 20);
    canvas.drawText(score, mScreenWidth-9, mScreenHeight-29, mTimePaint);
    if (score.charAt(0) == '-') {
      mTimePaint.setARGB(255, 255, 0, 0);
    } else {
      mTimePaint.setARGB(255, 0, 0, 0);
    }
    canvas.drawText(score, mScreenWidth-10, mScreenHeight-30, mTimePaint);

  }
}
