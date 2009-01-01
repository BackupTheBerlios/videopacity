package il.ac.haifa.videopacity.commands;

import il.ac.haifa.videopacity.media.GridMappingImageProducer;
import il.ac.haifa.videopacity.media.ImageProducer;


/**
 *	Command to combine Movies in a Grid fashion 
 */
public class GridCombinerCommand extends CombinerCommand{

	/**
	 * get help string for the arguments expected by this command
	 */
	@Override
	public String getHelp() {
		return "combines movies of the same size in a grid \n" +
				" - output movie will be as long as the shortest of the provided movies\n\n" +
				"Expected Arguments:\n" +
				"1. Number of movies\n" +
				"2. List of movie files (the size as specified in number of movies)\n" +
				"3. Movie file output name\n";
	}

	/**
	 * create an ImageProducer that will produce Images
	 * in a grid from the provided videos
	 * 
	 * @param videos - list of videos to combine
	 * @return - the grid combined videos ImageProducer
	 */
	@Override
	protected ImageProducer createCombinedImageProducer(ImageProducer[] videos) {
		//check that the number of specified movies is good for the grid
		if(videos.length != 4 && videos.length != 9){
			System.out.println("Illeagl number specified as number of movies, should be 4 or 9");
			System.exit(1);
		}
		
		//check that all videos are the same size
		for(int i=1;i < videos.length; ++i){
			if(videos[i-1].getWidth() != videos[i].getWidth() ||
			   videos[i-1].getHeight() != videos[i].getHeight()){
				System.out.println("All videos must be the same size");
				System.exit(1);
			}
		}
		//create the grid combined image producer
		return new GridMappingImageProducer(videos,(int)Math.sqrt(videos.length));
	}

}
