package metadata;

import firebolt.*;

/**
 * The magic attribute class
 * @author Alex Jeensma
 */
public class Attribute extends Metadata {
	/**
	 * Parse the attribute
	 */
	public void parse() {
		element.addAttribute(key,value);
	}
}
