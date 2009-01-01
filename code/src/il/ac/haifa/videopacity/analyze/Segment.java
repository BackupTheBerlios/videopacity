package il.ac.haifa.videopacity.analyze;

/**
 *	Segment Data Type 
 */
class Segment implements Comparable<Segment> {

	//possible location for intersection with the segment
	public enum Location{BEFORE,INSIDE,AFTER}
	//start and end point of the segment
	private int start,finish;

	/**
	 * Ctor
	 * 
	 * @param start - start point of the segment
	 * @param finish - end point of the segment
	 * @param delay - resonable delay that will still be considered insode the segment
	 */
	public Segment(int start,int finish,int delay){
		this.start=start;
		this.finish=finish+delay;
	}
	
	/**
	 * check location of a point relative to the segment
	 * 
	 * @param point - the point to check
	 * @return - the location relative to the segment of the given point
	 */
	public Location checkLocation(int point){
		if(this.start<point&&this.finish>=point){
			return Location.INSIDE;
		}
		if(this.start>=point){
			return Location.BEFORE;
		}
		return Location.AFTER;
	}
	
	/**
	 * compare method from Compareable interface to provide
	 * the ability of sorting alist of segments
	 */
	@Override
	public int compareTo(Segment otherSegment) {
		return this.start-otherSegment.start;
	}
	
}
