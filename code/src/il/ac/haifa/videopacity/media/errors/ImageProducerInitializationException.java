package il.ac.haifa.videopacity.media.errors;

/**
 *	Exception for Initialization of an Image Producer
 */
public class ImageProducerInitializationException extends ImageProducerException {

	private static final long serialVersionUID = 1L;

	/**
	 * Ctor
	 */
	public ImageProducerInitializationException() {
		super();
	}

	/**
	 * Ctor
	 * 
	 * @param msg - Error message
	 * @param wrappedException - original Exception that caused this one
	 */
	public ImageProducerInitializationException(String msg, Throwable wrappedException) {
		super(msg, wrappedException);
	}

	/**
	 * Ctor
	 * 
	 * @param msg - Error message
	 */
	public ImageProducerInitializationException(String msg) {
		super(msg);
	}

	/**
	 * Ctor
	 * 
	 * @param wrappedException - original Exception that caused this one
	 */
	public ImageProducerInitializationException(Throwable wrappedException) {
		super(wrappedException);
	}
	
	
}
