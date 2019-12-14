package helperClass;

public class BashCommandProcess {
	
	/**
	 * run the given bash command 
	 * @param command the command to be called in bash
	 */
	public static void runBashCommand(String command) {
		try {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

}
