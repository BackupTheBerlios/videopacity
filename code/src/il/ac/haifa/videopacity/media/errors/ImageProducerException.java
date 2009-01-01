package il.ac.haifa.videopacity.media.errors;

/**
 * General Exception for everything that has to do with ImageProducers
 */
public class ImageProducerException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Ctor
	 */
	public ImageProducerException() {
		super();
	}

	/**
	 * Ctor
	 * 
	 * @param msg - Error message
	 * @param wrappedException - original Exception that caused this one
	 */
	public ImageProducerException(String msg, Throwable wrappedException) {
		super(msg, wrappedException);
	}

	/**
	 * Ctor
	 * 
	 * @param msg - Error message
	 */
	public ImageProducerException(String msg) {
		super(msg);
	}

	/**
	 * Ctor
	 * 
	 * @param wrappedException - original Exception that caused this one
	 */
	public ImageProducerException(Throwable wrappedException) {
		super(wrappedException);
	}

}
