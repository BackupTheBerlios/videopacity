package il.ac.haifa.videopacity.commands;

import il.ac.haifa.videopacity.analyze.InputFileAnalyzer;

/**
 *	Command to analyze the user presses log, compared to
 *  the actual strange movements log file
 */
public class AnalyzeDataCommand implements CommandLineAction {

	/**
	 * execute the comparison of the log files
	 */
	@Override
	public void execute(String[] args) {
		//verify input
		if(args.length!=3){
			System.err.println("incorrect number of Argiments");
			System.out.println(getHelp());
			System.exit(1);
		}
		InputFileAnalyzer.main(args);
	}

	/**
	 * get help string that explains the arguments for this command
	 */
	@Override
	public String getHelp() {
		return 	"analyze test data and compare it to the real data \n\n" +
				"Expected Arguments:\n" +
				"1. correct time of the animations ending file\n" +
				"2. user time presses file\n" +
				"3. user reaction delay\n";
	}

}
