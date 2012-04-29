package firebolt;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import psd.model.Layer;

/**
 * This class disects a layername into it's different components
 * @author Alex Jeensma
 */
public class LayerMatcher 
{
	/**
	 * Optional tag name
	 */
	private String tag = "";
	
	/**
	 * Optional ID
	 */
	private String id = "";
	
	/**
	 * Optional attributes
	 */
	private HashMap<String,String> attributes = new HashMap<String,String>();	
	
	/**
	 * Create a new layer and disect it into it's components
	 * 
	 * @param layer the layer to dissect
	 */
	public LayerMatcher(Layer layer)
	{
		// match 1 = tagname
		// match 2 = id
		// match 3 = attributes
		String layerRegex = "^(<[a-z^>]*>)?([A-z\\-\\_]*)?(\\[.*\\])?$";
		String layerName = layer.toString();
		
		Pattern p = Pattern.compile(layerRegex);
		Matcher m = p.matcher(layerName);
	
		if(m.matches()) {
			// get the tagname (if its there, otherwise default to div)
			tag = m.group(1) != null ? m.group(1).substring(1, m.group(1).length() - 1) : "div";
			// get the identification (if its there, otherwise leave it empty)
			id = m.group(2) != null ? m.group(2) : "";
			// check for attributes
			if(m.group(3) != null) {
				// has attributes
				StringTokenizer st = new StringTokenizer(m.group(3).substring(1, m.group(3).length() - 1), ",");
				StringTokenizer attr;
				while(st.hasMoreTokens()) {
					attr = new StringTokenizer(st.nextToken(), "=");
					while(attr.hasMoreTokens()) {
						String key = attr.nextToken();
						String value = attr.nextToken();
						attributes.put(key, value.substring(1, value.length() - 1));
					}
				}
			}
		}
		else {
			// verdorie nog aan toe
			System.err.println("Couldn't parse layer: " + layerName);
		}		
	}

	/** 
	 * Get the tagname
	 * 
	 * @return the tag name
	 */
	public String getTag() 
	{
		return tag;
	}

	/**
	 * Get the ID
	 * 
	 * @return the ID
	 */
	public String getID()
	{
		return id;
	}
	
	/**
	 * Get the attributes map
	 * 
	 * @return the attributes
	 */
	public HashMap<String,String> getAttributes()
	{
		return attributes;
	}
}
