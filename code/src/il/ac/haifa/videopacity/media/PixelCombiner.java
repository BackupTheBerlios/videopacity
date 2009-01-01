package il.ac.haifa.videopacity.media;

import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.WritableRectIter;

/**
 *	Interface that defines how to merge sevral pixels into one
 *  using the change bimaps 
 */
public interface PixelCombiner {
	
	/**
	 * combine pixel
	 * 
	 * @param originals - original RectIter that points to the relevant pixel
 	 * @param changeBitmaps - change bitmap RectIter that points to the relevant pixel
	 * @param out - the WritableRectIter that point to the correct pixel and can be written
	 */
	public void combine(RectIter[] originals,RectIter[] changeBitmaps,WritableRectIter out);
	
}
