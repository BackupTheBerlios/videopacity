package il.ac.haifa.videopacity.commands;

/**
 * Interface for an action that can be invoked from the command line 
 */
public interface CommandLineAction {

	/**
	 * get the help description string
	 * 
	 * @return - help description of the command
	 */
	public String getHelp();
	
	/**
	 * execute this command
	 * 
	 * @param args - the args to the command
	 */
	public void execute(String[] args);
}
