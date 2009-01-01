package il.ac.haifa.videopacity.commands;

import il.ac.haifa.videopacity.media.ChangeDetectorByNthFrame;
import il.ac.haifa.videopacity.media.ImageProducer;
import il.ac.haifa.videopacity.media.NoiseErosion;
import il.ac.haifa.videopacity.media.OpacityPixelCombiner;
import il.ac.haifa.videopacity.media.PixelMergingImageProducer;


/**
 *	Command to combine the videos in an Opacity fashion 
 */
public class OpacityCombinerCommand extends CombinerCommand {

	/**
	 * get help string for the arguments expected by this command
	 */
	@Override
	public String getHelp() {
		return "combines movies of the same size in opacity layers \n" +
				" - output movie will be as long as the shortest of the provided movies\n\n" +
				"Expected Arguments:\n" +
				"1. Number of movies\n" +
				"2. List of movie files (the size as specified in number of movies)\n" +
				"3. Movie file output name\n";
	}

	/**
	 * create combined ImageProducer that will produce
	 * opacity merged Image
	 * 
	 * @param videos - list of videos to combine
	 * @return - the opacity combined videos ImageProducer
	 */
	@Override
	protected ImageProducer createCombinedImageProducer(ImageProducer[] videos) {
		//create change bitmaps for the opacity ImageProducer
		ImageProducer[] changeBitmaps = new ImageProducer[videos.length];
		for(int i=0; i < videos.length ;++i){
			changeBitmaps[i] = new NoiseErosion(new ChangeDetectorByNthFrame(videos[i], 25,1));
		}
		//create the opacity combined ImageProducer
		return new PixelMergingImageProducer(videos,changeBitmaps,new OpacityPixelCombiner());
	}


}
