package com.tony.sevens;

import java.io.Serializable;
public class Player implements Serializable{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String m_name; 
	  public int m_total;
	  public int m_score;
	  public int m_anchor;
	  
	  public Player(String name, int total, int score, int anchor) {
		    m_name = name;
		    m_total = total;
		    m_score = score;
		    m_anchor = anchor;
		  }
	  
 
}
