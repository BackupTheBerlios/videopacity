package il.ac.haifa.videopacity.commands;

import il.ac.haifa.videopacity.animator.SceneAnimation;
import il.ac.haifa.videopacity.media.MovieWriter;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

/**
 * Command that create a random animation movie 
 */
public class MovieGenerationCommand implements CommandLineAction {

	/**
	 * create a random animation movie
	 */
	@Override
	public void execute(String[] args) {
		//check for correct number of arguments
		if(args.length != 8){
			System.out.println("Incorrect Number of Arguments:\n" + getHelp());
			System.exit(1);
		}
		//convert arguments from string
		int width = 0;
		int height = 0;
		int density = 0;
		int eventfillness = 0;
		int movieLength = 0;
		try{
			width = Integer.parseInt(args[0]);
			height = Integer.parseInt(args[1]);
			density = Integer.parseInt(args[3]);
			eventfillness = Integer.parseInt(args[4]);
			movieLength = Integer.parseInt(args[5]) * SceneAnimation.FPS;
		}catch (NumberFormatException e) {
			System.out.println("Illegal number supplied");
			System.exit(1);
		}
		//check legality of width and height
		if(width < 1 || height < 1){
			System.out.println("with and height values must be at least of value: 1");
			System.exit(1);
		}
		//check density value validity
		if(density < 1){
			System.out.println("density must be at least of value: 1");
			System.exit(1);
		}
		//create file objects
		File outputMovieFile = new File(args[6]);
		File logFile = new File(args[7]);
		//create writer for logging the strange movment frames
		PrintWriter pw = null;
		try{
			pw = new PrintWriter(logFile);
		}catch (IOException e) {
			System.out.println("Error log opening file...");
			System.exit(1);
		}
		//check that the supplied background image exists
		File backroundFile = new File(args[2]);
		if(!backroundFile.exists()){
			System.out.println("Supplied Background image does not exists");
			System.exit(1);
		}
		//read the background image from the file
		Image background = null;
		try{
			background = ImageIO.read(backroundFile);
		}catch (IOException e) {
			System.out.println("Error reading background image");
		}
		//create the imageProducer that produces the animation
		SceneAnimation sa = new SceneAnimation(eventfillness,
				                               background,
				                               density,
				                               new Dimension(width,height),
				                               pw,
				                               movieLength);
		
		System.out.println("Generating movie ...");
		
		//wite the produced animation into the output file
		MovieWriter mw = new MovieWriter(sa);
		try{
			mw.writeMovie(outputMovieFile);
		}catch(IOException e){
		    System.out.println("Error Generating Movie");
		    System.exit(1);
		}
		
		//close the image producer
		pw.close();
		System.out.println("Done, created: \n" + outputMovieFile +"\n" + logFile);
	}

	/**
	 * get the help string that explains all the needed arguments for this command
	 */
	@Override
	public String getHelp() {
		return "Genrates an animation of walking people from an isometric view\n\n" +
				"Expected Arguments:\n" +
				"1. Width\n" +
				"2. Heigth\n" +
				"3. Background image file\n" +
				"4. Density (number of characters the enter in each wave*)\n" +
				"5. Eventfullness (number of waves* between each strange action)\n" +
				"6. Movie length in seconds\n" +
				"7. Movie file output name\n" +
				"8. Log file outputnam\n" +
				"\n" +
				" * wave is defined as a "+SceneAnimation.CHARACTER_WAVE_IN_SECONDS+" seconds period\n";
	}

}
