package il.ac.haifa.videopacity.animator;

import il.ac.haifa.videopacity.animator.Character.State;

import java.awt.Point;

/**
 * A Path Iterator that produces points along a path 
 * on which a character will move 
 */
public class Path {
	
	//the speed of the character
	private double velocity;
	//the number of points in which the character will change it's direction
	private int turnPointsNumber;
	//current point on the path
	private Point currentPoint;
	//the current direction in which the character is moved along a this path
	private Character.State direction;
	//number of frames until the turning point
	private int framLeftTillnextPoint;
	
	/**
	 * Ctor 
	 * 
	 * @param characterState - the starting direction
	 * @param currentPoint - the starting point
	 * @param turnPointsNumber - number of points where the path will change it's direction 
	 * @param velocity - the speed at which this path is traveled on
	 */
	public Path(State characterState, Point currentPoint, int turnPointsNumber,
			double velocity) {
		this.direction = characterState;
		this.currentPoint = currentPoint;
		this.turnPointsNumber = turnPointsNumber;
		this.velocity = velocity;
		this.framLeftTillnextPoint=getPathLength();
	}
	
	/**
	 * get random path length until next turn
	 * 
	 * @return - number between 16 and 64
	 */
	protected int getPathLength(){
		//2 to 8 seconds random
		return 16 + (int)Math.round(Math.random() * 48);
	}
	
	/**
	 * get next point on the path
	 * 
	 * @return - next point
	 */
	public Point getNextPoint(){
		advance();
		return this.currentPoint;
	}
	
	/**
	 * advance the path according to all information
	 */
	private void advance(){
		//change path direction if the time has come to do so
		if(this.turnPointsNumber>0){
			if(this.framLeftTillnextPoint==0){
				this.direction=getRandomDirection();
				this.framLeftTillnextPoint=getPathLength();
				this.turnPointsNumber--;
			}
			this.framLeftTillnextPoint--;
		}
		//calculate next point
		currentPoint=calculateNextValue(currentPoint, velocity, direction);
	}
	
	/**
	 * Get random direction
	 * 
	 * @return - a direction
	 */
	protected Character.State getRandomDirection(){
		return Character.State.values()[(int)Math.floor(Math.random()*4)];
	}
	
	/**
	 * Calculate the next point on the path from the given input
	 * 
	 * @param current - current point
	 * @param velocity - the speed of movement
	 * @param direction - the direction of movement
	 * 
	 * @return - next point
	 */
	private Point calculateNextValue(Point current, double velocity,Character.State direction){
		Point newPoint=new Point();
		switch(direction){
		case NW:
			newPoint.x=(int) Math.round(current.x-velocity);
			newPoint.y=(int) Math.round(current.y-2*velocity);
			break;
		case NE:
			newPoint.x=(int) Math.round(current.x+2*velocity);
			newPoint.y=(int) Math.round(current.y-velocity);
			break;
		case SW:
			newPoint.x=(int) Math.round(current.x-2*velocity);
			newPoint.y=(int) Math.round(current.y+velocity);
			break;
		case SE:
			newPoint.x=(int) Math.round(current.x+velocity);
			newPoint.y=(int) Math.round(current.y+2*velocity);
			break;
		case STRANGE:
			//strange is not a valid direction for a path
		default:
			throw new RuntimeException("illegal state");
		}
		return newPoint;
	}

	/**
	 * get current Point on the path
	 * 
	 * @return - a point
	 */
	public Point getPoint() {
		return currentPoint;
	}
	
	/**
	 * get current direction of the path
	 * 
	 * @return - a direction
	 */
	public Character.State getDirection() {
		return direction;
	}
	
	//Two paths are equal if they have same starting point and direction
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentPoint == null) ? 0 : currentPoint.hashCode());
		result = prime * result
				+ ((direction == null) ? 0 : direction.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Path other = (Path) obj;
		if (currentPoint == null) {
			if (other.currentPoint != null)
				return false;
		} else if (!currentPoint.equals(other.currentPoint))
			return false;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		return true;
	}
}
	
	
	
	

