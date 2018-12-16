
package com.tony.sevens;


class Card {

  public static final int CLUBS = 0;
  public static final int DIAMONDS = 1;
  public static final int SPADES = 2;
  public static final int HEARTS = 3;

  public static final int ACE = 1;
  public static final int JACK = 11;
  public static final int QUEEN = 12;
  public static final int KING = 13;
  public static final String TEXT[] = {
    "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"
  };

  public static int WIDTH = 50;
  public static int HEIGHT = 70;

  private int mValue;
  private int mSuit;
  private float mX;
  private float mY;
  public boolean mbPlayable;

  public static void SetSize(int type) {
    if (type == Rules.SEVENS) {
      //WIDTH = 51;
      //HEIGHT = 72;     
    } /*else if (type == Rules.FREECELL) {
      WIDTH = 49;
      HEIGHT = 68;
    } else {
      WIDTH = 45;
      HEIGHT = 64;
    }*/
  }

  public Card(int value, int suit) {
    mValue = value;
    mSuit = suit;
    mX = 1;
    mY = 1;
    mbPlayable = false;
  }

  public float GetX() { return mX; }
  public float GetY() { return mY; }
  public int GetValue() { return mValue; }
  public int GetSuit() { return mSuit; }

  public void SetPosition(float x, float y) {
    mX = x;
    mY = y;
  }

  public boolean Equals(Card card){
	  if (card == null)
		  return false;
	  if (mValue == card.GetValue() && mSuit == card.GetSuit())
		  return true;
	  else 
		  return false;
  }
  public void MovePosition(float dx, float dy) {
    mX -= dx;
    mY -= dy;
  }
}


