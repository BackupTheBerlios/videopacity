package il.ac.haifa.videopacity.media;

import il.ac.haifa.videopacity.media.errors.ImageProducerInitializationException;

import java.awt.Image;
import java.io.File;

import javax.media.Buffer;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.StartEvent;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.format.VideoFormat;
import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;
import javax.media.protocol.DataSource;
import javax.media.util.BufferToImage;

/**
 * Image producer that creates Images that are frames from a movie
 * 
 * Format: QuickTime
 * Codec: JPEG
 * 
 */
public class VideoFile implements ImageProducer {
	
	/**
	 * builder method to create a video file image producer
	 * 
	 * @param file - video file object
	 * @param encoding - encoding string, can be found as contsts on VidoFormat class
	 * @return - ImageProducer
	 * @throws ImageProducerInitializationException - if some error accured during initialization
	 */
	public static VideoFile createMediaFile(File file,String encoding) throws ImageProducerInitializationException{
		MediaLocator locator = new MediaLocator("file://" + file.getAbsolutePath());
		DataSource ds = null;
		try{
			ds = Manager.createDataSource(locator);
		}catch (Exception e) {
			throw new ImageProducerInitializationException("unable to create data source");
		}
		Format mfs[] = new Format[1];
		mfs[0] = new VideoFormat(encoding);
		
		Processor processor = null;
		try {
			ProcessorModel model = new ProcessorModel(ds,mfs,null);
			processor = Manager.createRealizedProcessor(model);
		} catch (Exception e) {
			throw new ImageProducerInitializationException("unable to realize processor",e);
		}
		
        //get frame grabbing control
		FrameGrabbingControl fg = null;
		if(processor != null){
			fg = (FrameGrabbingControl)processor.getControl("javax.media.control.FrameGrabbingControl");
		}else{
			throw new ImageProducerInitializationException("player is null :" + locator);
		}
		if(fg == null){
			throw new ImageProducerInitializationException("frame grabbing control not recieved :" + locator);
		}
        
		
		//get frame positioning control
		FramePositioningControl fp = (FramePositioningControl)processor.getControl("javax.media.control.FramePositioningControl");
		if(fp == null){
			throw new ImageProducerInitializationException("frame posittioning control not recieved :" + locator);
		}
		
		//get frame positioning control
		final Object blocker = new Object();
		
		//define anonymous class that will notify the blocker once the webcam has started 
		ControllerListener startWaiter = new ControllerListener(){

			@Override
			public void controllerUpdate(ControllerEvent evt) {
				if(evt instanceof StartEvent){
					synchronized (blocker) {
						blocker.notify();
					}
				}
			}
			
		};
		
		processor.addControllerListener(startWaiter);
		
		synchronized (blocker) {
			try{
				processor.start();
				blocker.wait();
			}catch (InterruptedException e) {
				throw new ImageProducerInitializationException("Problem occured while waiting for file: " + file,e);
			}
		}
		processor.stop();
		int frames = fp.mapTimeToFrame(processor.getStopTime()) + 1;
		return new VideoFile(fg,fp,frames,processor);
	}
	
	//movie controller that enables frame grabbing
	private FrameGrabbingControl fg;
	//movie controller that enables frame positioning
	private FramePositioningControl fp;
	//converter from generic buffer to image
	private BufferToImage btoi;
	//video format of the movie
	private VideoFormat vf;
	//size of the movie
	private int width;
	private int height;
	//last produced Image
	private RenderedOp img;
	//the current frame in the movie
	private int currentFrame;
	//the amount of frames in the movie
	private int frames;
	//the processing unit the decodes the movie from it's codec
	private Processor ply;
	
	/**
	 * Ctor
	 * should only be accessed by the builder static method
	 * 
	 * @param fg - frame grabbing controller
	 * @param fp - frame positioning controller 
	 * @param frames - number of frames in the movie
	 * @param ply - processor that decodes the movie
	 */
	private VideoFile(FrameGrabbingControl fg,FramePositioningControl fp,int frames, Processor ply){
		this.ply = ply;
		this.frames = frames;
		this.fg = fg;
		this.fp = fp;
		this.currentFrame = 0;
		Buffer buf = this.fg.grabFrame();
		vf = (VideoFormat)buf.getFormat();
		this.btoi = new BufferToImage(vf);
		Image img = this.btoi.createImage(buf);
		this.width = img.getWidth(null);
		this.height = img.getHeight(null);
	}
	
	
	/**
	 * Produce the next image from the video file
	 * 
	 * @return - the image
	 */
	@Override
	public RenderedOp getNextImage() {
		fp.seek(this.currentFrame++);
		//get frame from frame grabbing controller and transform it into RenderedOp
		this.img = AWTImageDescriptor.create(this.btoi.createImage(fg.grabFrame()),null);
		return this.img;
	}

	/**
	 * get the last produced image from the video file
	 * 
	 * @return - the image
	 */
	@Override
	public RenderedOp getImage() {
		return this.img;
	}

	/**
	 * get height of the images that are produced
	 * 
	 * @return - height of image
	 */
	@Override
	public int getHeight() {
		return this.height;
	}

	/**
	 * get width of the images that are produced
	 * 
	 * @return - width of image
	 */
	@Override
	public int getWidth() {
		return this.width;
	}

	/**
	 * Can another Image be produced
	 * 
	 * @return - 'true' if another image can be produced, 'false' otherwise.
	 */
	@Override
	public boolean hasNext() {
		return (currentFrame <= frames);
	}

	/**
	 * get frame rate of the produced images
	 * 
	 * @return - frame rate
	 */
	@Override
	public float getFrameRate() {
		return vf.getFrameRate();
	}


	/**
	 * close ImageProducer
	 */
	@Override
	public void close() {
		//close decoder
		this.ply.close();
	}

}
