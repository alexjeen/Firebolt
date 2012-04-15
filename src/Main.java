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
		
		long start = System.currentTimeMillis();
		
		if(args.length <= 1) {
			System.out.println("Usage: firebolt path/to/design.psd /path/to/output_folder");
			System.exit(0);
		}

		try
		{
			Parser p = new Parser(args[0], args[1]);
			long end = System.currentTimeMillis();
			System.out.println("Parsed Photoshop file in " + (end - start) + " ms..\nOutput is in " + p.getOutput());
			System.exit(0);
		}
		catch(Exception e) {
			System.out.println("Something went wrong: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
