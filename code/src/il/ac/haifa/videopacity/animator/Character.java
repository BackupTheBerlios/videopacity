package il.ac.haifa.videopacity.animator;

import java.awt.Graphics;

/**
 *	Interface for a character that can be moved 
 */
public interface Character {

	/**
	 *	Character possible states 
	 */
	public enum State{NW,NE,SW,SE,STRANGE};
	
	/**
	 * draw the character onto a Graphics object in location x,y
	 * 
	 * @param x - x coordinate to draw the character at
	 * @param y - y coordinate to draw the character at
	 * @param g - Graphics Object to draw the character on
	 */
	public void drawNextFrame(int x, int y,Graphics g);
	
	/**
	 * get the current state of the character
	 * 
	 * @return - the characters state
	 */
	public State getState();
	
	/**
	 * change current state of the characters
	 * 
	 * @param state - the state to change the character into 
	 */
	public void changeState(State state);
}
