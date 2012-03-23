package firebolt.css;

import java.util.*;

/**
 * Represents the Stylesheet of the Document. Every element basically has it's own
 * StyleSheetSelector, when the Photoshop file is parsed, all the StyleSheetSelectors
 * are accumulated and build up to a valid CSS file
 * 
 * @author Alex Jeensma
 */
public class Stylesheet {
	/**
	 * List of all the selectors (in order)
	 */
	private LinkedList<StylesheetSelector> selectors = new LinkedList<StylesheetSelector>();

	/**
	 * Adds a new selector to the end of the selectors list
	 * 
	 * @param s the full selector
	 * @return the created StylesheetSelector
	 */
	public void addSelector(StylesheetSelector s) 
	{
		selectors.add(s);
	}
	
	/**
	 * Print the Stylesheet
	 * 
	 * @return printed string
	 */
	public String printRecursive()
	{
		String CSSString = "";
		
		for(StylesheetSelector s : selectors) 
		{
			CSSString += s.print();
		}
		
		return CSSString;
	}
}
