package il.ac.haifa.videopacity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import il.ac.haifa.videopacity.animator.FalloutCharacter;
import il.ac.haifa.videopacity.commands.AnalyzeDataCommand;
import il.ac.haifa.videopacity.commands.CommandLineAction;
import il.ac.haifa.videopacity.commands.MovieGenerationCommand;
import il.ac.haifa.videopacity.commands.OpacityCombinerCommand;
import il.ac.haifa.videopacity.commands.PlayerCommand;
import il.ac.haifa.videopacity.commands.GridCombinerCommand;

/**
 *	Main class starting point of the whole application 
 */
public class Main {

	//TODO JAI and JMF externalize from JRE, and ant file
	//TODO add run(or name it something else) script that will set max heap space used to 512
	//convert createTest script to sh instead of tcsh
	/**
	 * main method
	 * 
	 * @param args - arguments passed from command line
	 */
	public static void main(String[] args) {
		//verify the input 
		if(args.length < 1){
			System.out.println("At least 1 parameter expected, \ntype \"help\" to see a list of available commands");
			System.exit(1);
		}
		//assign the animations directory
		FalloutCharacter.setCharacterDirectory("resources/chars");
		//create a map of available command line actions
		Map<String, CommandLineAction> actions = new HashMap<String, CommandLineAction>();
		actions.put("generate", new MovieGenerationCommand());
		actions.put("combGrid", new GridCombinerCommand());
		actions.put("combOpacity", new OpacityCombinerCommand());
		actions.put("player",new PlayerCommand());
		actions.put("analyze",new AnalyzeDataCommand());
		String command = args[0];
		
		//handle help command
		if(command.equals("help") || command.equals("Help")){
			if(args.length < 2){
				System.out.println("Available commands:");
				Iterator<String> itor = actions.keySet().iterator();
				while(itor.hasNext()){
					System.out.println("  " + itor.next());
				}
				System.out.println("\nType \"help [command name]\" to learn more about the command");
				System.exit(0);
			}
			String subcommand = args[1];
			CommandLineAction action = actions.get(subcommand);
			if(action == null){
				System.out.println("No such Action Exists:"+subcommand + "\n"+
						           "type \"help\" to see a list of all available commands");
			}else{
				System.out.println(action.getHelp());
			}
			System.exit(0);
		}
		//handle other command, if exists
		CommandLineAction action = actions.get(command);
		if(action == null){
			System.out.println("No such Action Exists:"+command + "\n"+
					           "type \"help\" to see a list of all available commands");
			System.exit(1);
		}
		//execute the command with it's arguments
		String[] subArguments = new String[args.length - 1];
		System.arraycopy(args, 1, subArguments, 0, args.length-1);
		action.execute(subArguments);
		
	}

}
