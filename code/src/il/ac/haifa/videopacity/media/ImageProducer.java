package il.ac.haifa.videopacity.media;

import javax.media.jai.RenderedOp;

/**
 * Interface for an Object that is able to produce Images 
 */
public interface ImageProducer {
	
	/**
	 * Produce the next image 
	 * 
	 * @return - the image
	 */
	public RenderedOp getNextImage();
	
	/**
	 * Get the last produced image
	 * 
	 * @return - the image
	 */
	public RenderedOp getImage();
	
	/**
	 * Can another Image be produced
	 * 
	 * @return - 'true' if another image can be produced, 'false' otherwise.
	 */
	public boolean hasNext();
	
	/**
	 * get width of the images that are produced
	 * 
	 * @return - width of image
	 */
	public int getWidth();
	
	/**
	 * get height of the images that are produced
	 * 
	 * @return - height of image
	 */
	public int getHeight();
	
	/**
	 * get frame rate of the produced images
	 * 
	 * @return - frame rate
	 */
	public float getFrameRate(); 
	
	/**
	 * close ImageProducer
	 */
	public void close();
}
