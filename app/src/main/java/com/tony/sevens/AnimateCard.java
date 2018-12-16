
package com.tony.sevens;

import android.graphics.Canvas;
//import android.util.Log;

import java.lang.Runnable;
import java.lang.Math;

public class AnimateCard {

  private static final float PPF = 40;

  protected SevensView mView;
  private Card[] mCard;
  private CardAnchor mCardAnchor;
  private int mCount;
  private int mFrames;
  private float mDx;
  private float mDy;
  private boolean mAnimate;
  public boolean mbFinished;
  private Runnable mCallback;

  public AnimateCard(SevensView view) {
    mView = view;
    mAnimate = false;
    mCard = new Card[104];
    mCallback = null;
    mbFinished = false;
  }

  public boolean GetAnimate() { return mAnimate; }

  public void Draw(DrawMaster drawMaster, Canvas canvas) {
	  //Log.i(""," Frames " + mFrames);
    if (mAnimate) {
      for (int j = 0; j < mCount; j++) {
        mCard[j].MovePosition(-mDx, -mDy);
      }
      for (int i = 0; i < mCount; i++) {
        drawMaster.DrawCard(canvas, mCard[i]);
      }
      mFrames--;
      if (mFrames <= 0) {
        mAnimate = false;
        Finish();
      }
    }
  }

  public void MoveCards(Card[] card, CardAnchor anchor, int count, Runnable callback) {
    float x = anchor.GetX();
    float y = anchor.GetNewY();
    mCardAnchor = anchor;
    mCallback = callback;
    mAnimate = true;

    for (int i = 0; i < count; i++) {
      mCard[i] = card[i];
    }
    mCount = count;
    Move(mCard[0], x, y);
  }

  public void MoveCard(Card card, CardAnchor anchor, Runnable callback) {
    float x = anchor.GetX();
    float y = anchor.GetNewY();
    mCardAnchor = anchor;
    mCallback = callback;
    mAnimate = true;

    mCard[0] = card;
    mCount = 1;
    Move(card, x, y);
  }

  private void Move(Card card, float x, float y) {
    float dx = x - card.GetX(); 
    float dy = y - card.GetY(); 
    
    String sSpeed = mView.GetSettings().getString("PlaySpeed", "0");// 0 = fast, 2 = medium, 3 = slow
    int iSpeed = Integer.parseInt(sSpeed);	 
    mFrames = Math.round((float)Math.sqrt(dx * dx + dy * dy) / (PPF - (float)(iSpeed*15.0f)));
    if (mFrames == 0) {
      mFrames = 1;
    }
    mDx = dx / mFrames;
    mDy = dy / mFrames;
    //Log.i(""," Move card " + (card.GetValue())  + " , " +card.GetSuit());
    mbFinished = false;
    mView.StartAnimating(); 
    if (!mAnimate) {
      Finish();
    }
  }

  private void Finish() {
	mbFinished = true;
    for (int i = 0; i < mCount; i++) {
    //	Log.i(""," Add card " + (mCard[i].GetValue())  + " , " +mCard[i].GetSuit());
      mCardAnchor.AddCard(mCard[i]);
      //mView.mCardAnchor[miAnchor].RemoveCard(mCard);
      mCard[i] = null;
    }
    mCardAnchor = null;
    mView.DrawBoard(); 

    if (mCallback != null) {
      mCallback.run();
    }
  }

  public void Cancel() {
	mbFinished = true;
    if (mAnimate) {
      for (int i = 0; i < mCount; i++) {
        mCardAnchor.AddCard(mCard[i]);
        mCard[i] = null;
      }
      mCardAnchor = null;
      mAnimate = false;
    }
  }
}
