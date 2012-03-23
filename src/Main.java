import firebolt.*;

/**
 * 
 * Main class (entry point Firebolt)
 * 
 * @author Alex Jeensma
 */
public class Main {	
	/**
	 * Takes a .psd file as input and parses it
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0) {
			System.out.println("Usage: firebolt design.psd");
		}
		
		try
		{
			new Parser(args[0]);
		}
		catch(Exception e) {
			System.out.println("Something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
