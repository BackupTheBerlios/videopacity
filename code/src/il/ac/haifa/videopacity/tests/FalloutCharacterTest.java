package il.ac.haifa.videopacity.tests;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;

import il.ac.haifa.videopacity.animator.FalloutCharacter;
import il.ac.haifa.videopacity.animator.SceneAnimation;
import il.ac.haifa.videopacity.animator.Character.State;
import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.media.MovieWriter;

public class FalloutCharacterTest implements ImageProducer{

	public static void main(String[] args) {
		//write the produced animation into the output file
		MovieWriter mw = new MovieWriter(new FalloutCharacterTest());
		try{
			mw.writeMovie(new File("FalloutCharacterTestOutput.mov"));
		}catch(IOException e){
		    System.out.println("Error Generating Movie");
		    System.exit(1);
		}
	}

	private int counter;
	private il.ac.haifa.videopacity.animator.Character character;
	
	public FalloutCharacterTest(){
		FalloutCharacter.setCharacterDirectory("resources/chars");
		try {
			this.character = FalloutCharacter.createCharacter("hapowr");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public void close() {}

	@Override
	public float getFrameRate() {
		return SceneAnimation.FPS;
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public RenderedOp getImage() {
		return null;
	}

	@Override
	public RenderedOp getNextImage() {
		counter++;
		BufferedImage buf = new BufferedImage(300,300,BufferedImage.TYPE_INT_RGB);
		Graphics g = buf.getGraphics();
		this.character.drawNextFrame(150, 150, g);
		g.dispose();
		switch (counter) {
		case 8:
			character.changeState(State.NE);
			break;
		case 16:
			character.changeState(State.STRANGE);
			break;
		case 88:
			character.changeState(State.SE);
			break;
		case 96:
			character.changeState(State.STRANGE);
			break;
		case 168:
			character.changeState(State.NW);
			break;
		case 176:
			character.changeState(State.STRANGE);
			break;
		case 248:
			character.changeState(State.SW);
			break;
		case 256:
			character.changeState(State.STRANGE);
			break;
		}
		return AWTImageDescriptor.create(buf,null);
	}

	@Override
	public int getWidth() {
		return 300;
	}

	@Override
	public boolean hasNext() {
		return counter < 350;
	}

}
