package il.ac.haifa.videopacity.commands;

import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.media.MovieWriter;
import il.ac.haifa.videopacity.media.VideoFile;
import il.ac.haifa.videopacity.media.errors.ImageProducerInitializationException;

import java.io.File;
import java.io.IOException;

import javax.media.format.VideoFormat;

/**
 *	Abstract Combining Movies Command, writes combined movies into a
 *  new movie file 
 */
public abstract class CombinerCommand implements CommandLineAction {

	/**
	 * execute combination command
	 * 
	 * @param args - list of arguments for this command
	 */
	@Override
	final public void execute(String[] args) {
		//validate agrguments
		if(args.length == 0){
			System.out.println("No parameters specified: ");
			System.out.println(getHelp());
			System.exit(1);
		}
		int numOfMovies = 0;
		try{
			numOfMovies = Integer.parseInt(args[0]);
		}catch (NumberFormatException e) {
			System.out.println("Illeagl number specified as number of movies, please refer to help");
			System.exit(1);
		}
		if(numOfMovies < 1){
			System.out.println("number Of Movies cannot be less then 1");
			System.exit(1);
		}
		if(numOfMovies + 2 != args.length){
			System.out.println("Illeagl number of parameters, please refer to help");
			System.exit(1);
		}
		//create output file object
		File outFile = new File(args[args.length - 1]);
		
		//check that all provided movie files exist
		File[] videoFiles = new File[numOfMovies];
		for(int i = 0; i < numOfMovies; ++i){
			videoFiles[i] = new File(args[1 + i]);
			if(!videoFiles[i].exists()){
				System.out.println("No such movie Exists: " + videoFiles[i]);
				System.exit(1);
			}
		}
		
		//create video ImageProducers from the arguments recieved
		VideoFile videos[]=new VideoFile[numOfMovies];
		for(int i = 0; i < numOfMovies; ++i){
			try{
				videos[i] = VideoFile.createMediaFile(videoFiles[i], VideoFormat.JPEG);
			}catch (ImageProducerInitializationException e) {
				System.out.println("Unable to load movie: " + videoFiles[i]);
				System.exit(1);
			}
		}
		MovieWriter mw = new MovieWriter(createCombinedImageProducer(videos));
		System.out.println("Generating movie ...");
		
		//write the combined movie to the output file
		try{
			mw.writeMovie(outFile);
		}catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		 
		//close all movies
		for(int i = 0; i < numOfMovies; ++i){
			videos[i].close();
		}
		
		System.out.println("Done, created: \n" + outFile);
	}

	/**
	 * abstract method to combine videos, sub classes will have to
	 * deside in which way to combine the videos
	 * 
	 * @param videos - list of videos to combine
	 * @return - the combined videos ImageProducer
	 */
	protected abstract ImageProducer createCombinedImageProducer(ImageProducer[] videos);
}