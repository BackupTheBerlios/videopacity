package il.ac.haifa.videopacity.animator;

import java.awt.Graphics;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Observable;

/**
 * Implementation of the Character Interface using Fallout animations
 */ 
public class FalloutCharacter extends Observable implements Character{
	
	private static String characterDirectory;
	
	/**
	 * builder method for FalloutCharacter
	 * 
	 * @param name - the name of the animation
	 * @return - a FalloutCharacter Object
	 * @throws MalformedURLException - thrown in case the correct animation files can not be located
	 */
	public static FalloutCharacter createCharacter(String name) throws MalformedURLException{
		//create all the animation files neede for the diffrent character states
		//normal states (4 directions)
		File nw = new File(characterDirectory + File.separator + name + "ab_nw.gif");
		File ne = new File(characterDirectory + File.separator + name + "ab_ne.gif");
		File sw = new File(characterDirectory + File.separator + name + "ab_sw.gif");
		File se = new File(characterDirectory + File.separator + name + "ab_se.gif");
		//stranfe states (4 directions)
		File nws = new File(characterDirectory + File.separator + name + "da_nw.gif");
		File nes = new File(characterDirectory + File.separator + name + "da_ne.gif");
		File sws = new File(characterDirectory + File.separator + name + "da_sw.gif");
		File ses = new File(characterDirectory + File.separator + name + "da_se.gif");
		return new FalloutCharacter(nw.toURI().toURL(),
							     ne.toURI().toURL(),
							     sw.toURI().toURL(),
							     se.toURI().toURL(),
							     nws.toURI().toURL(),
							     nes.toURI().toURL(),
							     sws.toURI().toURL(),
							     ses.toURI().toURL());
	}
	
	/**
	 * Initialize the location of the directory where the animations can be found
	 * 
	 * @param dir - relative path to the animations directory
	 */
	public static void setCharacterDirectory(String dir){
		characterDirectory = dir;
	}
	
	
	//flag that indicates whether the character is in a strange state
	public boolean isInStrangeState;
	
	//current state of the character
	private CharacterState currentState;
	//normal states
	private CharacterState NWCharacter;
	private CharacterState NECharacter;
	private CharacterState SWCharacter;
	private CharacterState SECharacter;
	//strange states
	private CharacterState NWCharacterStrange;
	private CharacterState NECharacterStrange;
	private CharacterState SWCharacterStrange;
	private CharacterState SECharacterStrange;
	
	/**
	 * Ctor
	 * 
	 * @params - urls to all animation needed for this character
	 */
	private FalloutCharacter(URL nw,URL ne,URL sw,URL se,
					  URL nws,URL nes,URL sws,URL ses){
		
		//female animation require different adjustments
		File f = new File(nw.getFile());
		//create states with different offset for male an female characters
		//this is done  because the fallout animation of female and male
		//character differ in speed
		if(f.getName().charAt(1) == 'f'){
			this.NWCharacter = new CharacterState(nw,4,3,true);
			this.NECharacter = new CharacterState(ne,-4,3,true);
			this.SWCharacter = new CharacterState(sw,4,-3,true);
			this.SECharacter = new CharacterState(se,-4,-3,true);
			
			this.NWCharacterStrange = new CharacterState(nws,-3,-19,false);
			this.NECharacterStrange = new CharacterState(nes,-5,-10,false);
			this.SWCharacterStrange = new CharacterState(sws,-15,-1,false);
			this.SECharacterStrange = new CharacterState(ses,-1,1,false);
		}else{
			this.NWCharacter = new CharacterState(nw,5,3,true);
			this.NECharacter = new CharacterState(ne,-4,3,true);
			this.SWCharacter = new CharacterState(sw,5,-3,true);
			this.SECharacter = new CharacterState(se,-5,-3,true);
			
			this.NWCharacterStrange = new CharacterState(nws,-3,-14,false);
			this.NECharacterStrange = new CharacterState(nes,-5,-6,false);
			this.SWCharacterStrange = new CharacterState(sws,-15,-1,false);
			this.SECharacterStrange = new CharacterState(ses,-1,1,false);
		}
		this.isInStrangeState = false;
		
		this.currentState = SECharacter;
	}
	
	/**
	 * draw the character onto a Graphics object in location x,y
	 * the method is synchronized to prevent from changing state
	 * at the same time as drawing
	 * 
	 * 
	 * @param x - x coordinate to draw the character at
	 * @param y - y coordinate to draw the character at
	 * @param g - Graphics Object to draw the character on
	 */
	public synchronized void drawNextFrame(int x, int y, Graphics g){
		//if state has further animation frames draw it
		if(this.currentState.hasNextFrame()){
			this.currentState.drawNextFrame(x,y,g);
			return;
		}
		//if animation ended and not in strange movement state 
		//reset the walking animation
		if(!this.isInStrangeState){
			this.currentState.reset();
			this.currentState.drawNextFrame(x, y, g);
			return;
		}else{
			//strange state is over
			this.isInStrangeState = false;
			//notify that strange state is over
			setChanged();
			notifyObservers();
			//change back to normal state
			if(currentState == NWCharacterStrange){
				this.currentState = NWCharacter;
			}else if(currentState == NECharacterStrange){
				this.currentState = NECharacter;
			}else if(currentState == SWCharacterStrange){
				this.currentState = SWCharacter;
			}else{
				this.currentState = SECharacter;
			}
			//reset the normal state animation to make the walking animation
			//appear floent 
			this.currentState.reset();
			this.currentState.drawNextFrame(x, y, g);
			return;
		}
		
	}
	
	/**
	 * get the current state of the character
	 * 
	 * @return - the characters state
	 */
	@Override
	public State getState(){
		if(currentState == NWCharacter){
			return State.NW;
		}else if(currentState == NECharacter){
			return State.NE;
		}else if(currentState == SECharacter){
			return State.SE;
		}else if(currentState == SWCharacter){
			return State.SW;
		}else if(currentState == NWCharacterStrange || currentState == NECharacterStrange ||
				 currentState == SWCharacterStrange || currentState == SECharacterStrange){
			return State.STRANGE;
		}else{
			throw new RuntimeException("Illegal State of a character");
		}
	}
	
	/**
	 * change current state of the characters
	 * 
	 * synchronized to make sure the state will not be changed
	 * at the same time as the drawing process
	 * 
	 * @param state - the state to change the character into 
	 */
	@Override
	public synchronized void changeState(State state){
		if(!this.isInStrangeState){
			switch(state){
			case NW:
				this.currentState = NWCharacter;
				break;
			case NE:
				this.currentState = NECharacter;
				break;
			case SE:
				this.currentState = SECharacter;
				break;
			case SW:
				this.currentState = SWCharacter;
				break;
			case STRANGE:
				if(currentState == NWCharacter){
					this.currentState = NWCharacterStrange;
				}else if(currentState == NECharacter){
					this.currentState = NECharacterStrange;
				}else if(currentState == SWCharacter){
					this.currentState = SWCharacterStrange;
				}else{
					this.currentState = SECharacterStrange;
				}
				this.currentState.reset();
				this.isInStrangeState = true;
				break;
			}
		}
	}

}
