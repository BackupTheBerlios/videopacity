package il.ac.haifa.videopacity.tests;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;

import il.ac.haifa.videopacity.animator.Character;
import il.ac.haifa.videopacity.animator.CharacterAnimator;
import il.ac.haifa.videopacity.animator.FalloutCharacter;
import il.ac.haifa.videopacity.animator.Path;
import il.ac.haifa.videopacity.animator.Character.State;
import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.media.MovieWriter;

public class CharacterAnimationTest implements ImageProducer{

	public static void main(String[] args) {
		//write the produced animation into the output file
		MovieWriter mw = new MovieWriter(new CharacterAnimationTest());
		try{
			mw.writeMovie(new File("CharacterAnimationTestOutput.mov"));
		}catch(IOException e){
		    System.out.println("Error Generating Movie");
		    System.exit(1);
		}
	}
	

	
	private int counter;
	private CharacterAnimator anim;
	
	public CharacterAnimationTest(){
		this.counter=0;
		FalloutCharacter.setCharacterDirectory("resources/chars");
		Character character = null;
		try {
			character = FalloutCharacter.createCharacter("hapowr");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.anim = new CharacterAnimator(character,
				                          new PathStub(State.NW,new Point(400,300),4,3),
				                          new Point(0,0),
				                          new Dimension(640,480));
	}

	@Override
	public void close() {}


	@Override
	public float getFrameRate() {
		return 8;
	}

	@Override
	public int getHeight() {
		return 480;
	}


	@Override
	public RenderedOp getImage() {
		return null;
	}


	@Override
	public RenderedOp getNextImage() {
		counter++;
		BufferedImage buf = new BufferedImage(640,480,BufferedImage.TYPE_INT_RGB);
		Graphics g = buf.getGraphics();
		this.anim.drawNextFrame(g);
		g.dispose();
		return AWTImageDescriptor.create(buf,null);
	}


	@Override
	public int getWidth() {
		return 640;
	}


	@Override
	public boolean hasNext() {
		return (counter < 160);
	}
	
	private class PathStub extends Path{

		private State[] directions;
		private int currentDirection;
		
		public PathStub(State characterState, Point currentPoint,
						int turnPointsNumber, double velocity) {
			super(characterState, currentPoint, turnPointsNumber, velocity);
			this.directions = new State[4];
			this.directions[0] = State.NE;
			this.directions[1] = State.SE;
			this.directions[2] = State.SW;
			this.directions[3] = State.NW;
			this.currentDirection = 0;
		}

		@Override
		protected int getPathLength() {
			return 20;
		}

		@Override
		protected State getRandomDirection() {
			return this.directions[(currentDirection++)% 4];
		}
		
		
	}
}
