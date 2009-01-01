package il.ac.haifa.videopacity.media;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;

/**
 * Image Producer that combines the output of sevral Image producers
 * to create a grided image
 */
public class GridMappingImageProducer implements ImageProducer {
	
	//the image producers that will be combined by this
	private ImageProducer[] producers;
	//the last produced image
	private RenderedOp image;
	//the size of the final image (after combination)
	private int width,hight;
	//the size of the grid
	private int gridSize;
	
	/**
	 * Ctor
	 * 
	 * @param producers - the Image Producers to combine
	 * @param gridSize - the size of the grid to create
	 */
	public GridMappingImageProducer(ImageProducer[] producers, int gridSize){
		this.producers=producers;
		this.hight=producers[0].getHeight()*gridSize;
		this.width=producers[0].getWidth()*gridSize;
		this.gridSize=gridSize;
		
	}
	
	/**
	 * close ImageProducer
	 */
	@Override
	public void close() {
		for (int i=0;i<this.producers.length;i++){
			this.producers[i].close();
		}
	}

	/**
	 * get frame rate of the produced images
	 * 
	 * @return - frame rate
	 */
	@Override
	public float getFrameRate() {
		return this.producers[0].getFrameRate();
	}

	/**
	 * get height of the images that are produced
	 * 
	 * @return - height of image
	 */
	@Override
	public int getHeight() {
		return this.hight;
	}

	/**
	 * Get the last produced image
	 * 
	 * @return - the image
	 */
	@Override
	public RenderedOp getImage() {
		return this.image;
	}

	/**
	 * Produce the next image 
	 * 
	 * @return - the image
	 */
	@Override
	public RenderedOp getNextImage() {
		//create image buffer to hold the combined data
		BufferedImage imageBuf=new BufferedImage(this.width,this.hight,BufferedImage.TYPE_INT_RGB);
		Graphics g=imageBuf.getGraphics();
		int width,height,theX,theY;
		//copy the next images from the producers in a grid fashin into the buffer
		for(int i=0;i<this.producers.length;i++){
			width=this.producers[i].getWidth();
			height=this.producers[i].getHeight();
			theY=(int)Math.floor(i/this.gridSize);
			theX=i%this.gridSize;
			g.drawImage(this.producers[i].getNextImage().getAsBufferedImage(),theX*width,theY*height,null);
			//draw rectangle frame to divide the images from each other in a more noticeable way   
			g.setColor(Color.BLACK);
			g.drawRect(theX*width,theY*height,width,height);
		}
		g.dispose();
		//return the created buffer
		this.image =  AWTImageDescriptor.create(imageBuf,null);;
		return this.image;
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
		//only if all producers have next frames then this combination
		//will be considered as a one that has next frame
		for(int i=0;i<this.producers.length;i++){
			if(!this.producers[i].hasNext()){
				return false;
			}
		}
		return true;
	}

}
