package firebolt;

import java.util.LinkedList;

import firebolt.commonpractices.*;

/**
 * The Heuristic abstract class allows us to easily add some heuristics to our project
 * Some of them are developed over time by seeing patterns in the HTML 
 * 
 * @author Alex Jeensma
 */
public abstract class CommonPractice {	
	/**
	 * Changes a element based on the different properties by 
	 * using a Heuristic..
	 * 
	 * @param e
	 */
	public abstract void changeElement(
			Element e
	);
	
	/**
	 * Returns a Class list of all heuristics in the firebolt.heuristics package
	 * @return A list of classes that implement this abstract Heuristic class
	 */
	@SuppressWarnings("unchecked")
	public static LinkedList<Class<CommonPractice>> getHeuristics()
    {
		String heuristics[] = {"TestCommonPractice"};
		LinkedList<Class<CommonPractice>> classes = new LinkedList<Class<CommonPractice>>();
			
		try
		{
			for(int i = 0; heuristics.length > i; i++)
			{
				classes.add((Class<CommonPractice>)Class.forName("firebolt.commonpractices." + heuristics[i]));
			} 
		}
		catch(ClassNotFoundException cnfe)
		{
			System.out.println("Error loading a Heuristic: " + cnfe.getMessage());
		}    	
    	
    	return classes; 
    }
	
}
