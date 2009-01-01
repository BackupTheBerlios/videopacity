package il.ac.haifa.videopacity.media;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.jai.RenderedOp;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;
import javax.media.jai.iterator.WritableRectIter;
import javax.media.jai.operator.AWTImageDescriptor;

/**
 *	Image Producer that Merges several Images of the same size
 *  By applying the merge pixel by pixel and taking into account
 *  the changes in these images, using the PixelCombiner
 *  rule of combination  
 */
public class PixelMergingImageProducer implements ImageProducer {
	
	//image producers of the images
	private ImageProducer[] originals;
	//image producers of the change bitmaps
	private ImageProducer[] changeBitmaps;
	//combiner that will decide how to combine each pixel
	private PixelCombiner comb;
	//the last produced image
	private RenderedOp joinedImg;
	
	/**
	 * Ctor 
	 * 
	 * @param originals - array of Image Producers to be merged
	 * @param changeBitmaps - array of change bitmaps Producers
	 * @param comb - the Combination rule
	 */
	public PixelMergingImageProducer(ImageProducer[] originals, ImageProducer[] changeBitmaps, PixelCombiner comb) {
		this.originals = originals;
		this.changeBitmaps = changeBitmaps;
		this.comb = comb;
	}
	/**
	 * Get the last produced image
	 * 
	 * @return - merged image
	 */
	@Override
	public RenderedOp getImage() {
		return this.joinedImg;
	}

	/**
	 * Produce the next image 
	 * 
	 * @return - next merged image
	 */
	@Override
	public RenderedOp getNextImage() {
		//read all images from all producers and change bitmap producers
		RenderedOp[] changeImgs = new RenderedOp[this.changeBitmaps.length];
		RenderedOp[] images = new RenderedOp[this.originals.length];
		BufferedImage out = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		//take images
		for (int i = 0; i < changeImgs.length; i++) {
			changeImgs[i] = this.changeBitmaps[i].getNextImage();
		}
		for (int i = 0; i < images.length; i++) {
			images[i] = this.originals[i].getImage();
		}
		//create iterators
		RectIter[] originalItors = new RectIter[this.originals.length];
		RectIter[] changeItors = new RectIter[this.changeBitmaps.length];
		
		for (int i = 0; i < originalItors.length; i++) {
			originalItors[i] = RectIterFactory.create(images[i], new Rectangle(getWidth(),getHeight()));
		}
		for (int i = 0; i < changeItors.length; i++) {
			changeItors[i] = RectIterFactory.create(changeImgs[i], new Rectangle(getWidth(),getHeight()));
		}
		WritableRectIter outIter = RectIterFactory.createWritable(out, new Rectangle(getWidth(),getHeight()));
		//iterate over all the pixels
		//init bends
		for (int i = 0; i < originalItors.length; i++) {
			originalItors[i].startBands();
		}
		
		for (int i = 0; i < changeItors.length; i++) {
			changeItors[i].startBands();
		}
		outIter.startBands();
		while(!outIter.finishedBands()){
			//init lines
			for (int i = 0; i < originalItors.length; i++) {
				originalItors[i].startLines();
			}
			for (int i = 0; i < changeItors.length; i++) {
				changeItors[i].startLines();
			}
			outIter.startLines();
			while(!outIter.finishedLines()){
				//init pixels
				for (int i = 0; i < originalItors.length; i++) {
					originalItors[i].startPixels();
				}
				for (int i = 0; i < changeItors.length; i++) {
					changeItors[i].startPixels();
				}
				outIter.startPixels();
				while(!outIter.finishedPixels()){
					//use the Pixel Combiner to decide how this pixel will be merged
					this.comb.combine(originalItors, changeItors, outIter);
					for (int i = 0; i < originalItors.length; i++) {
						originalItors[i].nextPixel();
					}
					for (int i = 0; i < changeItors.length; i++) {
						changeItors[i].nextPixel();
					}
					outIter.nextPixel();
				}
				for (int i = 0; i < originalItors.length; i++) {
					originalItors[i].nextLine();
				}
				for (int i = 0; i < changeItors.length; i++) {
					changeItors[i].nextLine();
				}
				outIter.nextLine();
			}
			for (int i = 0; i < originalItors.length; i++) {
				originalItors[i].nextBand();
			}
			outIter.nextBand();
		}
		this.joinedImg =  AWTImageDescriptor.create(out,null);
		return this.joinedImg;
	}

	/**
	 * get frame rate of the produced images
	 * it will be the smallest of all frame rates
	 * of the provided image producers
	 * 
	 * @return - frame rate
	 */
	@Override
	public float getFrameRate() {
		float frameRate = Integer.MAX_VALUE;
		for (int i = 0; i < originals.length; i++) {
			frameRate = Math.min(frameRate, originals[i].getFrameRate());
		}
		return frameRate;
	}
	
	/**
	 * get height of the images that are produced
	 * will be the minimum between all heights
	 * 
	 * @return - height of image
	 */
	@Override
	public int getHeight() {
		int height = Integer.MAX_VALUE;
		for (int i = 0; i < originals.length; i++) {
			height = Math.min(height, originals[i].getHeight());
		}
		return height;
	}
	
	/**
	 * get width of the images that are produced
	 * will be the minimum between all widths
	 * 
	 * @return - width of image
	 */
	@Override
	public int getWidth() {
		int width = Integer.MAX_VALUE;
		for (int i = 0; i < originals.length; i++) {
			width = Math.min(width, originals[i].getWidth());
		}
		return width;
	}

	/**
	 * Can another Image be produced
	 * only if all inner ImageProducers can still produce an image
	 * 
	 * @return - 'true' if another image can be produced, 'false' otherwise.
	 */
	@Override
	public boolean hasNext() {
		boolean hasNext = true;
		for (int i = 0; i < originals.length; i++) {
			hasNext &= originals[i].hasNext();
		}
		return hasNext;
	}

	/**
	 * close ImageProducer
	 */
	@Override
	public void close() {
		for (int i = 0; i < changeBitmaps.length; i++) {
			changeBitmaps[i].close();
		}
		for (int i = 0; i < originals.length; i++) {
			originals[i].close();
		}
	}
}
