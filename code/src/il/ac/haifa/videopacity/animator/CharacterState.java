package il.ac.haifa.videopacity.animator;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

/**
 *	A State of a character, i.e. one animation 
 */
public class CharacterState{

	//values to adjust (0,0) point to the center of the character
	private static final int CENTER_OFFSET_X = 12;
	private static final int CENTER_OFFSET_Y = 23;
	
	//buffers for the animation frames 
	private BufferedImage[] frames;
	//number of frames in the animation
	private int numberOfFrames;
	//the current frame of the character
	private int currentFrame;
	//x offset given to each character
	private int offsetX;
	//y offset given to each character
	private int offsetY;
	//is the character animation of a moving animation
	private boolean isDynamic;
	
	/**
	 * Ctor
	 * 
	 * @param fileUrl - url for the file which contains a character animation
	 * @param offsetX - offset given to each character
	 * @param offsetY - offset given to each character
	 * @param isDynamic - is the character animation of a moving animation
	 */
	public CharacterState(URL fileUrl,int offsetX, int offsetY,boolean isDynamic){
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.isDynamic = isDynamic;
		//read all the frames from the animated gif
		ImageReader reader = ImageIO.getImageReadersBySuffix("gif").next();
		try{
			reader.setInput(ImageIO.createImageInputStream(fileUrl.openStream()));
			this.numberOfFrames = reader.getNumImages(true);
			this.frames = new BufferedImage[this.numberOfFrames];
            for(int i = 0; i < this.numberOfFrames; ++i){
                this.frames[i] = reader.read(i);
            }
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.currentFrame = 0;
	}

	/**
	 * check if there is a next frame
	 * 
	 * @return - 'false' if, no more frames left, 'true' otherwise 
	 */
	public boolean hasNextFrame() {
		return this.currentFrame < this.numberOfFrames;
	}

	/**
	 * reset the animation
	 */
	public void reset() {
		this.currentFrame = 0;
	}

	/**
	 * draw the next frame of the animation
	 * 
	 * @param x - the x location to draw the character on 
	 * @param y - the y location to draw the character on
	 * @param g - Graphics object onto the 
	 */
	public void drawNextFrame(int x, int y, Graphics g) {
		//draw the next frame and balance the movements of the characters
		//if this is a walking animation.
		//balancing is needed since inside that animation the character is
		//moving and it needs to be disabled in order to move the character
		//on a path
		if(this.isDynamic){
			if(this.offsetX > 0 && this.offsetY > 0){
				g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X -(this.numberOfFrames - this.currentFrame - 1)*offsetX,
						                                   y - CENTER_OFFSET_Y - (this.numberOfFrames - this.currentFrame - 1)*offsetY,null);
			}else if(this.offsetX < 0 && this.offsetY < 0){
				g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X + this.currentFrame*offsetX,
						                                   y - CENTER_OFFSET_Y + this.currentFrame*offsetY,null);
			}else if(this.offsetX > 0 && this.offsetY < 0){
				g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X - (this.numberOfFrames - this.currentFrame - 1)*offsetX,
	                                                       y - CENTER_OFFSET_Y + this.currentFrame*offsetY,null);
			}else if(this.offsetX < 0 && this.offsetY > 0){
				g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X + this.currentFrame*offsetX,
	                                                       y - CENTER_OFFSET_Y - (this.numberOfFrames - this.currentFrame - 1)*offsetY,null);
			}else{               
				g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X,y - CENTER_OFFSET_Y,null);
			}
		}else{
			g.drawImage(this.frames[this.currentFrame],x - CENTER_OFFSET_X + this.offsetX,y - CENTER_OFFSET_Y + this.offsetY ,null);
		}
		this.currentFrame++;
	}

}
