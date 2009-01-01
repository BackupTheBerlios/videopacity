package il.ac.haifa.videopacity.media;

import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

/**
 *	Change Detector that creates a change bitmap by subtracting
 *  the current frame from a some some frame from the same ImageProducer
 *  that can be defined	 
 */
public class ChangeDetectorByNthFrame extends ChangeDetector {
	
	//The Nth frame that will be subtracted from all frames to create
	//a change bitmap
	private PlanarImage nthImage = null;
	//counter of frames
	private int counter;
	//the number of the frame that will be saved for subtraction
	private int n;
	
	/**
	 * Ctor
	 * 
	 * will create a change detector that uses the first frame
	 * as the the image to subtract from all other frames
	 * 
	 * @param bimp - image producer to detect it's change
	 * @param changeThreshold - threshold value of what will be considered a change
	 */
	public ChangeDetectorByNthFrame(ImageProducer bimp,int changeThreshold) {
		this(bimp,changeThreshold,0);
	}
	
	/**
	 * Ctor
	 * 
	 * @param bimp - image producer to detect it's change
	 * @param changeThreshold - threshold value of what will be considered a change
	 * @param frameNum - the number of the frame that will be subtracted from all others
	 */
	public ChangeDetectorByNthFrame(ImageProducer bimp,int changeThreshold, int frameNum) {
		super(bimp,changeThreshold);
		this.counter = 0;
		this.n = frameNum;
	}
	
	/**
	 * function that returns the background to use
	 * in the subtraction of the produced image  
	 * 
	 * @param img - the Image that we need the background for
	 * @return - the estimated background image
	 */
	@Override
	protected PlanarImage getBackgroundImage(RenderedOp op) {
		if (this.counter <= this.n) {
			this.counter ++;
			nthImage = op.createInstance();
		}
		return this.nthImage;
	}

}
