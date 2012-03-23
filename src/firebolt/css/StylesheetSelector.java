package firebolt.css;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author alex
 *
 */
public class StylesheetSelector {
	
	/**
	 * Holds the FULL selector
	 */
	private String selector;
	
	/**
	 * Holds the CSS properties for this selector
	 */
	private HashMap<String,String> properties;
	
	/**
	 * Creates a new selector
	 * 
	 * @param s the full selector
	 */
	public StylesheetSelector(String s)
	{
		selector = s;
		properties = new HashMap<String,String>();
	}
	
	/**
	 * Print the stylesheet selector and it's properties
	 *  
	 * @return 
	 */
	public String print()
	{
		// no selector or no properties? no need for selector
		if(selector.length() == 0 || properties.size() == 0) { return ""; }
		
		String CSS = "\t" + selector + " {\n";
		
		Iterator<Entry<String, String>> entries = properties.entrySet().iterator();
		
		while (entries.hasNext()) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>)entries.next();
		    String key = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    CSS += "\t\t"+key+": "+value+";\n";
		}
		
		return CSS + "\n\t}\n\n";
	}
	
	/**
	 * Get a certain property
	 * 
	 * @param property property to get
	 * @return property or null
	 */
	public String getProperty(String property)
	{
		return properties.get(property);
	}
	
	/**
	 * Add a CSS property
	 * 
	 * @param property property of the CSS line 
	 * @param value value of the CSS line
	 */
	public void addProperty(String property, String value)
	{
		properties.put(property, value);
	}
}
