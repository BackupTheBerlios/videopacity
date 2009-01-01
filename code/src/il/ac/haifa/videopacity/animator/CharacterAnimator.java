package il.ac.haifa.videopacity.animator;

import il.ac.haifa.videopacity.animator.Character.State;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

/**
 * Animated Character that moves on a path
 */
public class CharacterAnimator {

	//the screen padding for valid location for a strange movement
	public final static int SCREEN_PADDING_X = 20;
	public final static int SCREEN_PADDING_Y = 45;
	
	//the character to move on the path
	private Character character;
	//has this animation has next frame
	private boolean hasNextFrame;
	//the current direction of the character
	private Character.State direction;
	//the current point of the character
	private Point currentPoint;
	//the boundary at which the character stops
	private Point boundsPoint;
	private Dimension boundsSize;
	//the path on which this character moves
	private Path path;
	
	/**
	 * Ctor
	 * 
	 * @param character - the character
	 * @param path - the path on which the character will move
	 * @param boundsPoint - the left right corner of the boundary after which the animation stops
	 * @param boundsSize - the size of the boundary after which the animation stops
	 */
	public CharacterAnimator(Character character,Path path,Point boundsPoint,Dimension boundsSize) {
		this.path = path;
		this.boundsPoint = boundsPoint;
		this.boundsSize = boundsSize;
		this.character=character;
		this.direction=this.path.getDirection();
		this.character.changeState(this.direction);
		this.hasNextFrame = true;
	}
	
	/**
	 * is this animation got more frames
	 * it will be false if the character left the boundary
	 * 
	 * @return - 'true' if the character is still in the boundray 
	 */
	public boolean hasNextFrame(){
		return this.hasNextFrame;
	}
	
	/**
	 * make the character do the strange action animation
	 */
	public void doStrangeAction(){
		character.changeState(State.STRANGE);
	}
	
	/**
	 * draw the next frame of the animation onto 'g'
	 * 
	 * @param g - Graphics Object
	 */
	public void drawNextFrame(Graphics g){
		if(character.getState() != State.STRANGE){
			this.currentPoint=this.path.getNextPoint();
			if(this.direction!=this.path.getDirection()){
				this.direction=this.path.getDirection();
				this.character.changeState(this.direction);
			}
		}
		character.drawNextFrame(this.currentPoint.x,this.currentPoint.y, g);
		this.hasNextFrame = !hasLeftBounds();
	}

	/**
	 * Check if the character has left it's bounds
	 * 
	 * @return
	 */
	private boolean hasLeftBounds() {
		if(currentPoint.x > boundsPoint.x + boundsSize.width ||
		   currentPoint.x < boundsPoint.x	|| 
		   currentPoint.y > boundsPoint.y + boundsSize.height ||
		   currentPoint.y < boundsPoint.y){
			return true;
		}
		return false;
	}

	/**
	 * Get the current point of the on which the character is located 
	 * 
	 * @return - the point
	 */
	public Point getCurrentPoint(){
		return this.path.getPoint();
	}

	/**
	 * Check that the character is in a valid state to perform
	 * a strange action 
	 * 
	 * @param screenSize - the size of the screen 
	 * @return - 'true' if the character can perform a strange action
	 */
	public boolean canDoStrange(Dimension screenSize){
		Point current = getCurrentPoint();
		if(current.x > SCREEN_PADDING_X && 
		   current.x < screenSize.width - SCREEN_PADDING_X &&
	       current.y > SCREEN_PADDING_Y && 
		   current.y < screenSize.height - SCREEN_PADDING_Y &&
		   character.getState() != Character.State.STRANGE){	
			return true;
		}
		return false;
	}

	/**
	 * get the path of this character animation
	 * 
	 * @return - the path
	 */
	public Path getPath(){
		return this.path;
	}
}
