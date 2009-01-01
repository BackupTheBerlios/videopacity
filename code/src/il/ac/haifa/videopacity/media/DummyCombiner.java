package il.ac.haifa.videopacity.media;

import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.WritableRectIter;

public class DummyCombiner implements PixelCombiner {

	float[] p = {1.0f,1.0f,1.0f};
	
	@Override
	public void combine(RectIter[] originals, RectIter[] changeBitmaps,
			WritableRectIter out) {
		
		out.setPixel(p);

	}

}
