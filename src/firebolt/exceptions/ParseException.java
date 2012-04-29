package firebolt.exceptions;

/**
 * When something goes wrong parsing the document
 * 
 * @author Alex Jeensma
 */

@SuppressWarnings("serial")
public class ParseException extends Exception 
{
	/**
	 * Calls the super constructor
	 * 
	 * @param string parse error
	 */
	public ParseException(String string) {
		super("Firebolt parse error: " + string);
	} 
}
