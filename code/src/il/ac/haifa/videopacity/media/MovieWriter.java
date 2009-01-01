package il.ac.haifa.videopacity.media;


import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;
import javax.media.Processor;
import javax.swing.JFrame;

/**
 *	Movie Writer, writes all the prosuced Images of a ImageProducer
 *  to a file int the following format:
 *  
 * 	Format: QuickTime
 * 	Codec: JPEG
 * 
 */
public class MovieWriter implements ControllerListener, DataSinkListener{

	//the image producer that needs to be written into a file
	ImageProducer ip;
	//lock object
	Object blocker = new Object();
	
	public MovieWriter(ImageProducer ip) {
		this.ip = ip;
	}
	
	public void writeMovie(File file) throws IOException{
		//create data source for the processor to write into
		ImageProducerDataSource ipds = new ImageProducerDataSource(this.ip);

		//create processor to work with
		Processor p;
		try {
		    p = Manager.createProcessor(ipds);
		} catch (Exception e) {
		    throw new IOException("Cannot create a processor from the data source.");
		}

		p.addControllerListener(this);
		//put processor into a state so that it can be configured, and wait for
		//it to actually change to that state
		try{
			synchronized (blocker) {
				p.configure();
				blocker.wait();
			}
		}catch (InterruptedException e) {
			throw new IOException("Failed to configure the processor.");
		}
		//set the format output as quicktime
		p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
	
		//check that the  
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			throw new IOException("Format of the input file not supported: " + tcs[0].getFormat());
		}
		tcs[0].setFormat(f[0]);

		//change the processor back to running state, configuration stage finished
		//and wait for it to cahnge into that state
		try{
			synchronized (blocker) {
				p.realize();
				blocker.wait();
			}
		}catch (InterruptedException e) {
			throw new IOException("Failed to realize the processor.");
		}
		//create a sink for the processor to write the movie into 
		DataSink dsink;
		DataSource ds;
		
		
		if ((ds = p.getDataOutput()) == null) {
		    throw new IOException("Processor does not have an output DataSource");
		}
		try {
		    dsink = Manager.createDataSink(ds, new MediaLocator("file://"+file.getAbsolutePath()));
		    dsink.open();
		} catch (Exception e) {
			throw new IOException("Cannot create the DataSink: " + e);
		}
		dsink.addDataSinkListener(this);
		// start the writinf
		try {
		    p.start();
		    dsink.start();
		} catch (IOException e) {
			throw new IOException("IO error during processing");
		}
		// wait for the stream to be over
		try{
			synchronized (blocker) {
				blocker.wait();
			}
		}catch (InterruptedException e) {
			throw new IOException("Failed to writing to data source.");
		}
		// close the sink so that it will be closed and flushed
		try {
		    dsink.close();
		} catch (Exception e) {}
		p.removeControllerListener(this);
	}

	/**
	 * handler for processor events
	 */
	@Override
	public void controllerUpdate(ControllerEvent evt) {
		if (evt instanceof ConfigureCompleteEvent ||
		    evt instanceof RealizeCompleteEvent) {
			//release the thread that's waiting for
			//configuration and realization of the processs
			synchronized (blocker) {
				blocker.notifyAll();
			}
		}else if (evt instanceof EndOfMediaEvent) {
			//if end of media reached close then stop the file writing
		    evt.getSourceController().stop();
		    evt.getSourceController().close();
		}
	}

	/**
	 * handler for data sink events
	 */
	@Override
	public void dataSinkUpdate(DataSinkEvent evt) {
		if (evt instanceof EndOfStreamEvent) {
			//if data sink stopped writing then release the thread that's
			//waiting for it to finish writing
		    synchronized (blocker) {
				blocker.notifyAll();
		    }
		}
	}
	
	/**
	 * Adapter class to convert ImageProducer into a DataSource
	 * that the JMF Framework can work with and read images from from
	 */
	private class ImageProducerDataSource extends PullBufferDataSource {

		private ImageProducerStream streams[];
		
		public ImageProducerDataSource(ImageProducer ip){
			this.streams = new ImageProducerStream[1];
			this.streams[0] = new ImageProducerStream(ip);
		}
		
		@Override
		public PullBufferStream[] getStreams() {
			return this.streams;
		}

		@Override
		public void connect() throws IOException {}

		@Override
		public void disconnect() {}

		@Override
		public String getContentType() {
			return ContentDescriptor.RAW;
		}

		@Override
		public Object getControl(String arg0) {
			return null;
		}

		@Override
		public Object[] getControls() {
			return new Object[0];
		}

		@Override
		public Time getDuration() {
			 return DURATION_UNKNOWN;
		}

		@Override
		public void start() throws IOException {}

		@Override
		public void stop() throws IOException {}
		
		/**
		 * Inner Adapter class to convert ImageProducer into a PullBufferStream
		 * that the JMF Framework can work with and read from
		 */
		public class ImageProducerStream implements PullBufferStream {
			 
			private ImageProducer ip; 
			Format format;
			JFrame win;
			int frameNum;
			
			public ImageProducerStream(ImageProducer ip){
				this.frameNum = 1;
				this.ip = ip;
			    this.format = new VideoFormat(VideoFormat.JPEG,
						new Dimension(ip.getWidth(), ip.getHeight()),
						Format.NOT_SPECIFIED,
						Format.byteArray,
						ip.getFrameRate());
			}
			
			@Override
			public Format getFormat() {
				return this.format;
			}
			
			
			@Override
			public void read(Buffer buf) throws IOException {
				//if no more images stop the reading
				if (endOfStream()) {
					buf.setEOM(true);
					buf.setOffset(0);
					buf.setLength(0);
					return;
			    }
				BufferedImage bufi = ip.getNextImage().getAsBufferedImage();
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				//encode the image to jpeg
				ImageIO.write(bufi, "jpg", bos);
				byte[] byteArr = bos.toByteArray();
				buf.setData(byteArr);
				buf.setOffset(0);
				buf.setLength((int)byteArr.length);
				buf.setFormat(format);
				buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
				System.out.println("writing Frame: " + this.frameNum++);
			}

			@Override
			public boolean willReadBlock() {
				return false;
			}

			@Override
			public boolean endOfStream() {
				return !ip.hasNext();
			}

			@Override
			public ContentDescriptor getContentDescriptor() {
				return new ContentDescriptor(ContentDescriptor.RAW);
			}

			@Override
			public long getContentLength() {
				return 0;
			}

			@Override
			public Object getControl(String arg) {
				return null;
			}

			@Override
			public Object[] getControls() {
				return new Object[0];
			}
			 
		 }
	}
	
}
