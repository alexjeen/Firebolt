package metadata;

import firebolt.Element;

/**
 * Holds the basic Metadata class
 * 
 * @author Alex Jeensma
 *
 */
public abstract class Metadata {
	/**
	 * Holds the element for this Metadata rule
	 */
	protected Element element;
	
	/**
	 * Holds the key without the Metadata prepended
	 */
	protected String key;
	
	/**
	 * Holds the value (without the quotes)
	 */
	protected String value;
	
	/**
	 * Creates a new metadata line
	 * 
	 * @param e the element for this line
	 * @param k key string
	 * @param v value string
	 */	
	public void setMetadata(Element e, String k, String v)
	{
		element = e;
		key = k;
		value = v;		
	}

	/**
	 * Must be implemented by the child class
	 */
	public abstract void parse();
}
