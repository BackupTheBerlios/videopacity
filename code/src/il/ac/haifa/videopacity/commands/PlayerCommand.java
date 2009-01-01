package il.ac.haifa.videopacity.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.media.format.VideoFormat;

import il.ac.haifa.videopacity.media.VideoFile;
import il.ac.haifa.videopacity.media.errors.ImageProducerInitializationException;
import il.ac.haifa.videopacity.player.PressRecordingMoviePlayer;

/**
 *	Command to start the player of movies that can record user presses 
 */
public class PlayerCommand implements CommandLineAction {

	/**
	 * start the player
	 */
	@Override
	public void execute(String[] args) {
		//validate input
		if(args.length < 2){
			System.out.println("Expected [video file] [log name]");
			System.exit(1);
		}
		String videoFileName = args[0];
		String logName = args[1];
		//check that the movie file supplied exists
		File videoFile = new File(videoFileName);
		if(!videoFile.exists()){
			System.out.println("File Not Found.");
			System.exit(1);
		}
		//open a file writer to log key presses into 
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(logName);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open log for writing");
			System.exit(1);
		}
		//create video file ImageProducer
		VideoFile video = null;
		try{
			video = VideoFile.createMediaFile(videoFile, VideoFormat.JPEG);
		}catch (ImageProducerInitializationException e) {
			System.out.println("Unable to open video file!");
			System.exit(1);
		}
		//create the player gui
		new PressRecordingMoviePlayer(video,pw);
	}

	/**
	 * get the help string that explains this commands expected arguments
	 */
	@Override
	public String getHelp() {
		return "Plays movies and records space presses\n" +
		"Expected Arguments:\n" +
		"1. video file name\n" +
		"2. logFile name\n";
	}

}
