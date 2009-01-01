package il.ac.haifa.videopacity.media;

import javax.media.jai.RenderedOp;

/**
 * Abstract Decorator for the Image producer, as in Decorator Pattern
 */
public abstract class ImageProducerDecorator implements ImageProducer {

	//decorated Image producer
	private ImageProducer bimp;
	
	/**
	 * Ctor
	 * 
	 * @param bimp - the Image producer to decorate
	 */
	ImageProducerDecorator(ImageProducer bimp){
		this.bimp = bimp;
	}
	
	/**
	 * get Decorated image producer
	 * 
	 * @return - imageProducer
	 */
	protected ImageProducer getImageProducer(){
		return this.bimp;
	}
	
	/*
	 * Delegation of all ImageProducers methods to the decorated
	 * ImageProducer
	 */
	
	@Override
	public RenderedOp getNextImage() {
		return this.bimp.getNextImage();
	}
	
	@Override
	public RenderedOp getImage() {
		return this.bimp.getImage();
	}
	
	@Override
	public int getHeight() {
		return this.bimp.getHeight();
	}

	@Override
	public int getWidth() {
		return this.bimp.getWidth();
	}

	@Override
	public boolean hasNext() {
		return this.bimp.hasNext();
	}

	@Override
	public float getFrameRate(){ 
		return this.bimp.getFrameRate();
	}

	@Override
	public void close() {
		this.bimp.close();
	}

	
}
