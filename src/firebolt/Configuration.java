 package firebolt;

import java.util.HashMap;

/**
 * Configuration object
 * 
 * @author Alex Jeensma
 */
public class Configuration {
	/**
	 * Key => value, all the configuration options
	 */
	private HashMap<String,String> options = new HashMap<String,String>();
	
	/**
	 * Adds a configuration option
	 * 
	 * @param key key of the configuration
	 * @param value value of the configuration
	 */
	public void add(String key, String value)
	{
		options.put(key, value);
	}
	
	/**
	 * Get a key if it exists, otherwise returns null
	 * 
	 * @param key key to find
	 * 
	 * @return value if found
	 */
	public String get(String key)
	{
		return options.get(key);
	}
}
