package il.ac.haifa.videopacity.media;


import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AddDescriptor;
import javax.media.jai.operator.BandCombineDescriptor;
import javax.media.jai.operator.BinarizeDescriptor;
import javax.media.jai.operator.SubtractDescriptor;

/**
 * Abstract Decorator of ImageProducer that produces change bitmaps
 */
abstract public class ChangeDetector extends ImageProducerDecorator {

	//const for no change value for the bitmap
	public static final float[] NO_CHANGE = {0,0,0};
	//const for when a change has been made for the bitmap
	public static final float[] CHANGE = {255,255,255};
	
	//the change bitmap
	private RenderedOp changeBitmap = null;
	//the threshold of change that will be considered as a real change
	private int changeThreshold;
	
	/**
	 * Ctor
	 * 
	 * @param bimp - The decorated ImageProducer form which to produce changeBitmaps
	 * @param changeThreshold - the threashold in the change that will be marked in the bitmap
	 */
	ChangeDetector(ImageProducer bimp,int changeThreshold) {
		super(bimp);
		this.changeThreshold = changeThreshold;
	}
	
	@Override
	public RenderedOp getNextImage() {
		//get next image from decorated ImageProducer
		RenderedOp next = super.getNextImage();
		
		//do a pixel by pixel XOR operation between the image and the expected background
		RenderedOp sub1 = SubtractDescriptor.create(getBackgroundImage(next),next, null);
		RenderedOp sub2 = SubtractDescriptor.create(next,getBackgroundImage(next), null);
		RenderedOp temp = AddDescriptor.create(sub1, sub2, null);
		
		//combine 3 RGB channels to 1 channel, with equal weights
		double[][] combine = {{1,1,1,0}}; 
		RenderedOp temp2 = BandCombineDescriptor.create(temp, combine, null);
		
		//use the threshold to convert the combined image to a binary image
		this.changeBitmap = BinarizeDescriptor.create(temp2,(double)this.changeThreshold, null);
		return this.changeBitmap;
	} 

	/**
	 * get the last created bitmap
	 * 
	 * @return - last change bitmap
	 */
	@Override
	public RenderedOp getImage() {
		return this.changeBitmap;
	} 
	
	/**
	 * get the threshold value
	 * 
	 * @return - the threshold
	 */
	public int getChangeThreshold() {
		return changeThreshold;
	}

	/**
	 * set the threshold value
	 * 
	 * @param changeThreshold - the new threshold value
	 */
	public void setChangeThreshold(int changeThreshold) {
		this.changeThreshold = changeThreshold;
	}

	/**
	 * Abstract function that returns the background to use
	 * in the subtraction of the produced image  
	 * 
	 * @param img - the Image that we need the background for
	 * @return - the estimated background image
	 */
	abstract protected PlanarImage getBackgroundImage(RenderedOp img);
	
}
