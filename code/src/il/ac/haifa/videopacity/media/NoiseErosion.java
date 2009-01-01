package il.ac.haifa.videopacity.media;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.MedianFilterDescriptor;

/**
 * Image Producer Decorated that performs Erotion on the 
 * Image 
 */
public class NoiseErosion extends ImageProducerDecorator {

	
	//last image produced
	private RenderedOp img;
	
	/**
	 * Ctor
	 * 
	 * @param bimp - the decorated ImageProducer
	 */
	public NoiseErosion(ImageProducer bimp) {
		super(bimp);
	}

	/**
	 * Get the last produced Image 
	 */
	@Override
	public RenderedOp getImage() {
		return this.img;
	}

	/**
	 * Get next image
	 */
	@Override
	public RenderedOp getNextImage() {
		RenderedOp op = super.getNextImage();
		//aplly median filter with a mask of size 5x5
		this.img = MedianFilterDescriptor.create(op, MedianFilterDescriptor.MEDIAN_MASK_SQUARE, 5, null);
		return this.img;
	}
	
	

}
