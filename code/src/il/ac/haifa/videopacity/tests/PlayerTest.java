package il.ac.haifa.videopacity.tests;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;

import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.player.PressRecordingMoviePlayer;

/**
 * Test for class PressRecordingMoviePlayer
 */
public class PlayerTest implements ImageProducer{

	public static void main(String[] args) {
		PlayerTest pt = new PlayerTest();
		PressRecordingMoviePlayer prmp = new PressRecordingMoviePlayer(pt,new PrintWriter(System.out));
		pt.setPlayer(prmp);
	}

	private int counter = 0;
	private Set<Integer> framesOfEvents;
	private PressRecordingMoviePlayer player;
	
	private PlayerTest(){
		this.framesOfEvents = new HashSet<Integer>();
		//generate a list of frames
		this.framesOfEvents.add(1);
		this.framesOfEvents.add(12);
		this.framesOfEvents.add(13);
		this.framesOfEvents.add(57);
		this.framesOfEvents.add(68);
		this.framesOfEvents.add(69);
		this.framesOfEvents.add(80);
		this.framesOfEvents.add(100);
		this.framesOfEvents.add(111);
	}
	
	public void setPlayer(PressRecordingMoviePlayer player){
		this.player = player;
	}
	
	@Override
	public void close() {}

	@Override
	public float getFrameRate() {
		return 100;
	}

	@Override
	public int getHeight() {
		return 500;
	}

	@Override
	public RenderedOp getImage() {
		return null;
	}

	@Override
	public RenderedOp getNextImage() {
		counter++;
		if(this.framesOfEvents.contains(new Integer(counter))){
			//send event for the player
			player.keyTyped(new KeyEvent(player,0,0,0,0,' ',0));
		}
		return AWTImageDescriptor.create(
				new BufferedImage(500,500,BufferedImage.TYPE_INT_RGB),null);
	}

	@Override
	public int getWidth() {
		return 500;
	}

	@Override
	public boolean hasNext() {
		return counter < 120;
	}

}
