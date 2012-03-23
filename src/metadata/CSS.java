package metadata;

import firebolt.css.*;

/**
 * CSS class (just adds a selector directly)
 * 
 * @author Alex Jeensma
 */
public class CSS extends Metadata 
{
	/**
	 * Add the style to the end of the StylesheetSelector
	 */
	public void parse()
	{
		StylesheetSelector s = element.getSelector();
		s.addProperty(key, value);
	}
}
