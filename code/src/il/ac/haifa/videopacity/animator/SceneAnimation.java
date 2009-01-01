package il.ac.haifa.videopacity.animator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

import javax.media.jai.RenderedOp;
import javax.media.jai.operator.AWTImageDescriptor;

import il.ac.haifa.videopacity.animator.Character.State;
import il.ac.haifa.videopacity.media.ImageProducer;
/**
 * Image producer of animations of walking people who make
 * strange movements
 */
public class SceneAnimation implements ImageProducer,Observer{
	
	//the number of seconds that each wave of new character lasts 
	public final static int CHARACTER_WAVE_IN_SECONDS = 6;
	//the FPS of the fallout animations
	public final static int FPS = 8;
	//list of available fallout animations
	private final static List<String> animationNames;
	//prefix that will be at the beginning of each log file to distinguish between the different logs
	public static final char LOG_PREFIX = '#';
	
	static{
		//initialize the list of available fallout animations
		animationNames = new ArrayList<String>();
		animationNames.add("hanpwr");
		animationNames.add("hapowr");
		animationNames.add("harobe");
		animationNames.add("hfcmbt");
		animationNames.add("hfjmps");
		animationNames.add("hflthr");
		animationNames.add("hfmaxx");
		animationNames.add("hfmetl");
		animationNames.add("hmbmet");
		animationNames.add("hmcmbt");
		animationNames.add("hmlthr");
		animationNames.add("hmmaxx");
		animationNames.add("hmmetl");
	}
	
	
	//the amount of characters that will be generated at each wave
	private int density;
	//the background Image for the animation
	private BufferedImage background;
	//the size of the the image generated
	private Dimension size;
	//printer to print the strange movements frames into
	private PrintWriter strangeMovmentLog;
	//the amount of frames that this ImageProducer will create
	private int numberOfframes;
	//counter of the current frame
	protected int framePassed;
	//list of frames in which strange movment will occur
	private Queue<Integer> strangeFrames;
	//the last generated frame
	private RenderedOp img;
	//list of active characters on the screen
	private List<CharacterAnimator> listOfCharacters;
	//a down counter till next wave will be created
	private int waveCounter;
	//wave waiting queue, will hold characters created but not yet released onto the screen
	protected List<CharacterAnimator>[] waveWaitingQueue;
	//list of possible entry point for characters
	protected Point[] possibleEnryPoints;
	//list of possible direction for the newly created characters
	//indexes are synchronized with possibleEnryPoints
	protected Character.State[] possibleEntryDirection; 
	
	@SuppressWarnings("unchecked")//suppress the warning of creating an array of non generic Lists
	public SceneAnimation(int activity, Image background, int desity,
			              Dimension size, PrintWriter strangeMovmentLog,int numberOfFrames) {
		this.listOfCharacters=new LinkedList<CharacterAnimator>();
		this.background = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_RGB);
		//copy background image into local buffer
		Graphics g = this.background.getGraphics();
		g.drawImage(background, 0,0,null);
		g.dispose();
		//create list of random frames for strange moves
		LinkedList<Integer> strangeArray = new LinkedList<Integer>();
		for(int i=0;i<activity; i++){
			strangeArray.add(new Integer((int)Math.floor(Math.random()*(numberOfFrames - 30))));
		}
		Collections.sort(strangeArray);
		this.strangeFrames = strangeArray;
		this.density = desity;
		this.size = size;
		this.strangeMovmentLog = strangeMovmentLog;
		this.numberOfframes=numberOfFrames;
		//convert wave counter from seconds frames
		this.waveCounter=CHARACTER_WAVE_IN_SECONDS*FPS;
		//create wave holding data sets
		this.waveWaitingQueue=new List[CHARACTER_WAVE_IN_SECONDS];
		for(int i=0; i<this.waveWaitingQueue.length;i++){
			this.waveWaitingQueue[i]=new LinkedList<CharacterAnimator>();
		}
		this.framePassed = 0;
		initializeEntryArrays();
		generateWave();
		advanceScene();
	}
	
	/**
	 * Initializes the possible entry and directions for characters
	 */
	private void initializeEntryArrays(){
		this.possibleEnryPoints = new Point[8];
		this.possibleEntryDirection = new Character.State[8];
		//left entry
		this.possibleEnryPoints[0]=new Point(-CharacterAnimator.SCREEN_PADDING_X,(int)Math.round(this.size.height/5));
		this.possibleEntryDirection[0]=	State.SE;
		this.possibleEnryPoints[1]=new Point(-CharacterAnimator.SCREEN_PADDING_X,(int)Math.round(4*(this.size.height/5)));
		this.possibleEntryDirection[1]=	State.NE;
		//bottom entry
		this.possibleEnryPoints[2]=new Point((int)Math.round(this.size.width/5),this.size.height+CharacterAnimator.SCREEN_PADDING_Y);
		this.possibleEntryDirection[2]=	State.NE;
		this.possibleEnryPoints[3]=new Point((int)Math.round(4*(this.size.width/5)),this.size.height+CharacterAnimator.SCREEN_PADDING_Y);
		this.possibleEntryDirection[3]=	State.NW;
		//top entery
		this.possibleEnryPoints[4]=new Point((int)Math.round(this.size.width/5),-CharacterAnimator.SCREEN_PADDING_Y);
		this.possibleEntryDirection[4]=	State.SE;
		this.possibleEnryPoints[5]=new Point((int)Math.round(4*(this.size.width/5)),-CharacterAnimator.SCREEN_PADDING_Y);
		this.possibleEntryDirection[5]=	State.SW;
		//right entery
		this.possibleEnryPoints[6]=new Point(this.size.width+CharacterAnimator.SCREEN_PADDING_X,(int)Math.round(this.size.height/5));
		this.possibleEntryDirection[6]=	State.SW;
		this.possibleEnryPoints[7]=new Point(this.size.width+CharacterAnimator.SCREEN_PADDING_X,(int)Math.round(4*(this.size.height/5)));
		this.possibleEntryDirection[7]=	State.NW;
				
	}
	
	/**
	 * creates a random path from the possible entry points
	 * 
	 * @return - a random path
	 */
	protected Path createRandomPath(){
		int randomIndex=(int)Math.floor(Math.random()*8);
		return new Path(this.possibleEntryDirection[randomIndex],
					    this.possibleEnryPoints[randomIndex],3,3);
	}
	
	/**
	 * generates a new wave of characters and stores it in the waveWaitingQueue
	 */
	protected void generateWave(){
		//create characters as much as the 'density' parameter
		for(int i=0;i<this.density;i++){
			int randomIndex;
			Path path;
			boolean alreadyTaken;
			//choose a path, if some character already has same entry point at the same time, choose again
			do{
				randomIndex=(int)Math.floor(Math.random()*CHARACTER_WAVE_IN_SECONDS);
				path=createRandomPath();
				
				Iterator<CharacterAnimator> itor = this.waveWaitingQueue[randomIndex].iterator();
				alreadyTaken = false;
				
				while(itor.hasNext()){
					if(itor.next().getPath().equals(path)){
						alreadyTaken = true;
						break;
					}
				}
			}while(alreadyTaken);
			
			//create a random character
			FalloutCharacter character=createRandomCharacter();
			//listen to character to log the actual strange movement time
			character.addObserver(this);
			//define the boundary of the characters movement
			Point boundryPoint = new Point(-100,-100);
			Dimension boundrySize = new Dimension(this.size.width + 200, this.size.height+200);
			//create the Character animation from all the gathered data and add it to the waveWaitingQueue
			CharacterAnimator ca = new CharacterAnimator(character,path, boundryPoint,boundrySize);
			this.waveWaitingQueue[randomIndex].add(ca);
		}
	}

	/**
	 * Create a random Fallout Character from the animations list
	 * 
	 * @return - Fallout Character
	 */
	protected FalloutCharacter createRandomCharacter() {
		int randomIndex=(int)Math.floor(Math.random()*animationNames.size());
		FalloutCharacter c = null;
		try {
			c = FalloutCharacter.createCharacter(animationNames.get(randomIndex));
		} catch (MalformedURLException e) {
			throw new RuntimeException("Character Animation Not Found",e);
		}
		return c;
	}

	@Override
	public RenderedOp getNextImage() {
		BufferedImage buffer = new BufferedImage(this.size.width,this.size.height,BufferedImage.TYPE_INT_RGB);
		Graphics gBuf = buffer.getGraphics();
		//draw background
		if(this.background != null){
			gBuf.drawImage(this.background,0,0,null);
		}else{
			gBuf.setColor(Color.WHITE);
			gBuf.fillRect(0, 0, this.size.width, this.size.height);
		}
		//draw characters
		Iterator<CharacterAnimator> itor=listOfCharacters.iterator();
		while(itor.hasNext()){
			CharacterAnimator cr=itor.next();
			if(cr.hasNextFrame()){
				cr.drawNextFrame(gBuf);
			}else{
				itor.remove();
				handleCharacterRemoved();
			}
		}
		gBuf.dispose();
		this.img = AWTImageDescriptor.create(buffer,null);
		advanceScene();
		//check if strange action needs to happen
		if(strangeFrames.size() != 0 && framePassed > strangeFrames.peek().intValue()){
			List<CharacterAnimator> strangeCandidates = new ArrayList<CharacterAnimator>();
		    itor=listOfCharacters.iterator();
			while(itor.hasNext()){
				CharacterAnimator cr=itor.next();
				if(cr.canDoStrange(this.size)){
					strangeCandidates.add(cr);
				}
			}
			if(strangeCandidates.size() != 0){
				strangeFrames.poll();
				int randomCharacterIndex = (int)Math.floor(Math.random()*strangeCandidates.size());
				strangeCandidates.get(randomCharacterIndex).doStrangeAction();
			}
		}
		return this.img;
	}

	protected void handleCharacterRemoved() {}

	/**
	 * advance all characters 1 frame and update character related data 
	 */
	private void advanceScene() {
		this.framePassed++;
		this.waveCounter--;
		//if it's a round second, release the character that were assign to that second in the current wave
		if(this.waveCounter%FPS==0){
			int i=(int)(this.waveCounter/FPS);
			Iterator<CharacterAnimator> itor=this.waveWaitingQueue[i].iterator();
			while(itor.hasNext()){
				listOfCharacters.add(itor.next());
				itor.remove();
			}
		}
		//generate new wave if needed
		if(this.waveCounter==0){
			this.waveCounter=FPS*CHARACTER_WAVE_IN_SECONDS;
			generateWave();
		}
	}

	/**
	 * Get the last produced image
	 * 
	 * @return - the image
	 */
	@Override
	public RenderedOp getImage() {
		return this.img;
	}
	
	/**
	 * get width of the images that are produced
	 * 
	 * @return - width of image
	 */
	@Override
	public int getWidth() {
		return this.size.width;
	}

	/**
	 * Can another Image be produced
	 * 
	 * @return - 'true' if another image can be produced, 'false' otherwise.
	 */
	@Override
	public boolean hasNext() {
		return this.numberOfframes>=this.framePassed;
	}
	
	/**
	 * close ImageProducer
	 */
	@Override
	public void close() {}

	/**
	 * get frame rate of the produced images
	 * 
	 * @return - frame rate
	 */
	@Override
	public float getFrameRate() {
		return FPS;
	}

	/**
	 * get height of the images that are produced
	 * 
	 * @return - height of image
	 */
	@Override
	public int getHeight() {
		return this.size.height;
	}

	
	/**
	 * handle finished strange movement events from character
	 */
	@Override
	public void update(Observable observable, Object o) {
		//log th end of the strange movment
		this.strangeMovmentLog.println(LOG_PREFIX+""+this.framePassed);
	}

}
