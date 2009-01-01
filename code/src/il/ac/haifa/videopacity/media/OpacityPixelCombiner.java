package il.ac.haifa.videopacity.media;

import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.WritableRectIter;

/**
 *	Pixel Combiner that combines the pixel by giving equal amount of
 *  Opacity value to each original pixel
 *  In case there was some change then the opacity is only divided
 *  between the pixels that had change in them 
 */
public class OpacityPixelCombiner implements PixelCombiner {

	/**
	 * combine pixel
	 * 
	 * @param originals - original RectIter that points to the relevant pixel
 	 * @param changeBitmaps - change bitmap RectIter that points to the relevant pixel
	 * @param out - the WritableRectIter that point to the correct pixel and can be written
	 */
	@Override
	public void combine(RectIter[] originals, RectIter[] changeBitmaps,	WritableRectIter out) {
		//count the amount of changes in the change pixels
		int changeCounter = 0;
		for (int i = 0; i < changeBitmaps.length; i++) {
			if(changeBitmaps[i].getSample() > 0){
				changeCounter++;
			}
		}
		if(changeCounter == 0){
			//Distribute opacity equally between all images
			//because no movement occurred at this pixel
			float alphaPrecentage = 1f / originals.length;
			float pixelValue = 0;
			for (int i = 0; i < originals.length; i++) {
				pixelValue += originals[i].getSample() * alphaPrecentage;
			}
			out.setSample(pixelValue);
		}else{
			//distribute opacity equally between the pixel that a change in
			//them was detected
			float alphaPrecentage = 1f / changeCounter;
			float pixelValue = 0;
			for (int i = 0; i < originals.length; i++) {
				if(changeBitmaps[i].getSample() > 0){
					pixelValue += originals[i].getSample() * alphaPrecentage;
				}
				out.setSample(pixelValue);
			}
		}
	}

}
