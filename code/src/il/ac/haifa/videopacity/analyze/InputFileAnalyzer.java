package il.ac.haifa.videopacity.analyze;

import il.ac.haifa.videopacity.animator.SceneAnimation;
import il.ac.haifa.videopacity.player.PressRecordingMoviePlayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 *	Analyzing program that can compare key presses log
 *  with a strange actions log and output the number
 *  of:
 *  
 *  	1. True Positives - presses when a strange action ocured
 *  	2. Misses - strange message ocured but nothing was pressed
 *  	3. False Positives - a key was pressed but not strange movement
 */
public class InputFileAnalyzer {

	private static final int ANIMATION_LENGTH=12;
	
	public static void main(String[] args) {
		//check that the files actually exist
		File finishPoints;
		File keyPresses;
		finishPoints=new File(args[0]);
		if(!finishPoints.exists()){
			System.err.println("cannot find finish points file");
			System.exit(1);
		}
		keyPresses=new File(args[1]);
		if(!keyPresses.exists()){
			System.err.println("cannot find key Presses file");
			System.exit(1);
		}
		//parse delay and verify it's validity
		int delay = 0;
		try{
			delay = Integer.parseInt(args[2]);
		}catch (NumberFormatException e) {
			System.out.println("delay provided is not a valid number");
			System.exit(1);
		}
		if(delay < 0){
			System.out.println("delay must be a positive number");
			System.exit(1);
		}
		//create readers for the two log files
		List<Segment> segments=new LinkedList<Segment>();
		BufferedReader finishPointsReader=null;
		BufferedReader keyPressesReader=null;
		try {
			finishPointsReader = new BufferedReader(new FileReader(finishPoints));
			keyPressesReader=new BufferedReader(new FileReader(keyPresses));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//create list of segments (acceptable times for strange movement presses)
		// and a list of press times
		String line;
		List <Integer> keyPressesList=new LinkedList<Integer>();
		try{
			while((line=finishPointsReader.readLine())!=null){
				char prefix = line.charAt(0);
				if(prefix != SceneAnimation.LOG_PREFIX){
					if(prefix == PressRecordingMoviePlayer.LOG_PREFIX){
						System.out.println("expected strange animations log file, recieved key presses log file");
					}else{
						System.out.println("strange animations log file format incorrect!");
					}
					System.exit(1);
				}
				int data=Integer.parseInt(line.substring(1)/*line without prefix*/);
				segments.add(new Segment(data-ANIMATION_LENGTH,data,delay));
			}
			while((line=keyPressesReader.readLine())!=null){
				char prefix = line.charAt(0);
				if(prefix != PressRecordingMoviePlayer.LOG_PREFIX){
					if(prefix == SceneAnimation.LOG_PREFIX){
						System.out.println("expected key presses log file, recieved strange animations log file");
					}else{
						System.out.println("key presses log file format incorrect!");
					}
					System.exit(1);
				}
				int data=Integer.parseInt(line.substring(1)/*line without prefix*/);
				keyPressesList.add(new Integer(data));
			}
		}
		catch (IOException e) {
			System.err.println("cannot read from the input files");
		}
		
		//calculate the information needed
		int numberOfHits=verifyPresses(segments, keyPressesList);
		//print the information
		System.out.println("number of true positives:"+numberOfHits);
		System.out.println("number of missed events:"+(segments.size()-numberOfHits));
		System.out.println("number of false positives:"+(keyPressesList.size()-numberOfHits));
		
		
	}

	private static int verifyPresses(List<Segment> listOfSegments, List<Integer> listOfPresses){
		int hits=0;
		//sort the list of segments since it can be an unsorted combination
		//of multiple lists
		Collections.sort(listOfSegments);
		
		if(listOfPresses.isEmpty()||listOfSegments.isEmpty()){
			return hits;
		}
		Iterator<Segment> itorSegment=listOfSegments.iterator();
		Iterator<Integer> itorKeyPresses=listOfPresses.iterator();
		Segment currentSegment=itorSegment.next();
		Integer currentPresse=itorKeyPresses.next();
		try{
			while(true){
				switch(currentSegment.checkLocation(currentPresse)){
				//if a press is inside the segment hit found, remove both
				//segment and keypress
				case INSIDE:
					hits++;
					System.out.println("the point:"+currentPresse);
					currentSegment=itorSegment.next();
					currentPresse=itorKeyPresses.next();
					break;
				//if the press is after the current segment then remove the
				//segment
				case AFTER:
					currentSegment=itorSegment.next();
					break;
				//if the press if before the current segment, remove the press
				case BEFORE:
					currentPresse=itorKeyPresses.next();
					break;
				}
			}
		}catch(NoSuchElementException e){}
		return hits;
	}
	

}
