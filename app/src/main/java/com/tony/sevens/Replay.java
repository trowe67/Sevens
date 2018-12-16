
package com.tony.sevens;


public class Replay implements Runnable {
  
  private SevensView mView;
  private AnimateCard mAnimateCard;
  private CardAnchor mCardAnchor;
  private boolean mIsPlaying;
  private Card mCard;
  
 

  public Replay(SevensView view, AnimateCard animateCard) {
    mView = view;
    mAnimateCard = animateCard;
    mIsPlaying = false;
  }

  public boolean IsPlaying() { return mIsPlaying; }
  public void StopPlaying() { mIsPlaying = false; }

  public void StartReplay(Card card, CardAnchor anchor) {
    mCardAnchor = anchor;
    mCard = card;
    mView.DrawBoard(); 
    mIsPlaying = true;
    PlayNext();
  }

  public void PlayNext() {
    if (!mIsPlaying ) {
      mView.StopAnimating(); 
      return;
    }
    
      mAnimateCard.MoveCard(mCard, mCardAnchor, null);
      mIsPlaying = false;
      mView.StopAnimating(); 
  }

  public void run() {
    if (mIsPlaying) {

      PlayNext();
    }
  }
}
