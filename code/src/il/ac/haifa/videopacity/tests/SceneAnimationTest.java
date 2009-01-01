package il.ac.haifa.videopacity.tests;

import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import il.ac.haifa.videopacity.animator.Character;
import il.ac.haifa.videopacity.animator.CharacterAnimator;
import il.ac.haifa.videopacity.animator.FalloutCharacter;
import il.ac.haifa.videopacity.animator.Path;
import il.ac.haifa.videopacity.animator.SceneAnimation;
import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.media.MovieWriter;

public class SceneAnimationTest extends SceneAnimation {

	public static void main(String[] args) {
		FalloutCharacter.setCharacterDirectory("resources/chars");
		ImageProducer ip = new SceneAnimationTest(new PrintWriter(System.out),500);
		MovieWriter mw = new MovieWriter(ip);
		try{
			mw.writeMovie(new File("SceneAnimationTestOutput.mov"));
		}catch(IOException e){
		    System.out.println("Error Generating Movie");
		    System.exit(1);
		}
	}
	
	int waveIndex = 0;
	PrintWriter logTest;
	
	public SceneAnimationTest(PrintWriter strangeMovmentLog, int numberOfFrames) {
		super(0, null, 0, new Dimension(640,480), strangeMovmentLog, numberOfFrames);
		this.logTest = null;
		try{
			this.logTest = new PrintWriter(new File("SceneAnimationTest.log"));
		}catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public boolean hasNext() {
		boolean has = super.hasNext();
		if(!has){
			this.logTest.close();
		}
		return has;
	}



	@Override
	protected void handleCharacterRemoved() {
		this.logTest.println("Character removed from active list at frame:" + this.framePassed); 
	}

	@Override
	protected void generateWave() {
		if(this.waveIndex < 6){
			for(int i=0;i < possibleEnryPoints.length; ++i){
				//create a path with no turns
				Path path = new Path(possibleEntryDirection[i],possibleEnryPoints[i],0,3);
				Character character  = createRandomCharacter();
				Point p = new Point();
				p.x = -200;
				p.y = -200;
				CharacterAnimator ca = new CharacterAnimator(character,path,p,new Dimension(1040,880));
				//add to be released at the first second of the wave
				waveWaitingQueue[this.waveIndex].add(ca);
			}
			//make sure only one wave is generated
			this.waveIndex++;
		}
	}
	
	

}
