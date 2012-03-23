package firebolt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;
import psd.model.Layer;
import psd.parser.layer.LayerType;
import firebolt.css.StylesheetSelector;
import metadata.MetaDataParser;

/**
 * The Element class basically holds every HTML element
 * 
 * @author Alex Jeensma
 */
public class Element {
	/**
	 * Holds the tag name
	 */
	private String tag;
	
	/**
	 * Layer coupled with this Element
	 */
	private Layer layer;

	/**
	 * Holds the attributes of this element
	 */
	private HashMap<String,String> attributes = new HashMap<String,String>();
	
	/**
	 * Holds the CSS properties for this element
	 */
	private StylesheetSelector css;
	
	/**
	 * Parent (null if no parent)
	 */
	private Element parent;
	
	/**
	 * Children of this layer
	 */
	private LinkedList<Element> children;
	
	/**
	 * Create a new element
	 * 
	 * @param tagname
	 * @param id
	 * @param attributes
	 */
	public Element(String tagname, String id, HashMap<String,String>attr, Layer l)
	{
		children = new LinkedList<Element>();
		tag = tagname;
		layer = l;
		if(id.length() > 0) {
			attributes.put("id", id);
			css = new StylesheetSelector("#" + id);
		}
		else {
			css = new StylesheetSelector(tagname);
		}
		
		new MetaDataParser(this, attr);
		
		
	}
	
	/**
	 * Recursive all the children + this node and build the style
	 */
	public void recursiveBuildStyle()
	{
		buildStyle();
		
		for(Element e : children)
		{
			e.recursiveBuildStyle();
		}
	}
	
	/**
	 * Build the style of the element
	 * 
	 * @TODO Do some complex calculations based on the layer
	 */
	public void buildStyle()
	{
		// get the image from the layer
		BufferedImage bi = layer.getImage();
		
		// image dimensions
		int layerWidth = bi.getWidth();
		int layerHeight = bi.getHeight();
		
		/** 
		 *	BACKGROUND OF THE ELEMENT 
		 * 	- Solid color
		 *  - Gradient
		 *  - Image
		 */
		
		// only handles solid colors atm
		Color c = new Color(bi.getRGB(0, 0));
		
		css.addProperty("background", "rgb("+c.getRed()+","+c.getGreen()+","+c.getBlue()+")");
		
		/**
		 * DIMENSIONS OF THE ELEMENT
		 * - Width
		 * - Height
		 */
		
		if(!tag.equals("body")) {
			css.addProperty("width", layerWidth + "px");
			css.addProperty("height", layerHeight + "px");			
		}
		
		/**
		 * BOX MODEL OF THE ELEMENT
		 * - Margin
		 * - Padding
		 * - Border
		 */
		if(hasParent() && parent.getLayer().getType() == LayerType.NORMAL) {
			Layer p = parent.getLayer();
			
			// parent x position
			int xp = p.getX();
			// parent x position right
			int xpr = p.getWidth() + xp;
			// parent y position
			int yp = p.getY();
			// parent y position bottom
			int ypb = p.getHeight() + yp;
			
			// x position
			int x = layer.getX();
			// x right position
			int xr = layer.getWidth() + x;
			// y position
			int y = layer.getY();
			// y bottom position
			int yb = layer.getHeight() + y;
			
			// check for center align in this parent element
			if((xr - xpr) == (xp - x)) {
				css.addProperty("margin-left", "auto");
				css.addProperty("margin-right", "auto");
			}
			
			if((y - yp) > 0) {
				// @TODO: according to the box model, the height should decrease - padding
				StylesheetSelector parentSelector = parent.getSelector();
				parentSelector.addProperty("padding-top", (y - yp) + "px");
			}
			
			// check for margin top of this element
			Element nif = getNextInFlow();
			
			if(nif != null) {
				Layer nifL = nif.getLayer();
				if((nifL.getY() - yb) > 0) {
					nif.getSelector().addProperty("margin-top", (nifL.getY() - yb) + "px");
				}
			}
		}
	
	}
	
	/**
	 * Gets the next element in the flow
	 * 
	 * @see http://explainth.at/en/css/flow.shtml
	 * @return element prev in flow, otherwise null
	 */	
	public Element getNextInFlow()
	{
		if(!hasParent()) { return null; } 
		
		LinkedList<Element> parentChildren = parent.getChildren();
		int thisIndex = parentChildren.indexOf(this);
		
		if((thisIndex - 1) >= 0) {
			return parentChildren.get(thisIndex - 1);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Merges 2 elements together, if you have this structure
	 * 
	 * F <body>
	 * 	 L Layer
	 *   L Layer2
	 *   L <body>
	 *   
	 * Folder <body> and Layer <body> will get merged into one element
	 * 
	 * So the layer <body>, will become the group <body>!
	 * 
	 * @param child the child
	 */
	public void merge(Element child)
	{
		// this = top folder (parent)
		// child = same named element as top folder, new element
		if(child.hasParent()) {
			// get the parent, of the child, and remove it from the children
			child.getParent().removeChild(child);
		}
		
		// copy settings from the layer element to the group element
		
		// attributes
		attributes.putAll(child.getAttributes());
		
		// layer information
		layer = child.getLayer();
		
	
	}
	
	/**
	 * Adds a child
	 * 
	 * @param e the child
	 */
	public void addChild(Element e)
	{
		children.add(e);
		e.setParent(this);
	}
	
	/**
	 * Returns the tag + it's attributes
	 */
	public String toString()
	{
		String elem = "<"+tag+buildAttributes()+">";
		return elem;
	}
	
	/**
	 * Build the attributes string
	 * 
	 * @return attributes as string
	 */
	private String buildAttributes()
	{
		String attributesString = "";
		Iterator<Entry<String, String>> entries = attributes.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>)entries.next();
		    String attr = (String)entry.getKey();
		    String value = (String)entry.getValue();
		    attributesString += " " + attr + "=\"" + value + "\"";
		}
		return attributesString;		
	}
	
	/**
	 * Build the append string (replace tag name, tag id, etc)
	 * 
	 * @param append the append string
	 * 
	 * @return parsed string
	 */
	private String buildAppend(String append)
	{
		String id = "";
		
		if(attributes.get("id") != null) {
			id = "#" + attributes.get("id");
		}
		else if(attributes.get("class") != null) {
			id = "." + attributes.get("class");
		}
		
		append = append.replace("tag_name", tag);
		append = append.replace("tag_id", id);
		
		if(id.length() == 0) {
			// no length? no id comment string
			append = "";
		}
		
		return append;
	}
	
	/**
	 * Print the element and it's children recursively
	 * 
	 * @TODO Make a String repeat method
	 * 
	 * @return parsed HTML
	 */
	public String printRecursive(int level, String appendFirst, String appendLast)
	{
		/*if(tag.length() == 0) {
			return "";
		}*/
		
		String HTML = "<"+tag;
		String indentation = "";
		
		HTML += buildAttributes();
		
		HTML += ">"+buildAppend(appendFirst)+"\n";
		
		level++;
		// Element e : children
		for(int y = children.size() - 1; y >= 0; y--) {
			indentation = "";
			for(int i = 0; level > i; i++) {
				indentation += "\t";
			}
			HTML += indentation + children.get(y).printRecursive(level, appendFirst, appendLast);
		}
		level--;
		
		indentation = "";
		for(int i = 0; level > i; i++) {
			indentation += "\t";
		}		
		
		HTML += indentation + "</"+tag+">"+buildAppend(appendLast)+"\n";
		
		return HTML;
	}
	
	/**
	 * Adds a attribute to the element
	 * 
	 * @param attr attribute name
	 * @param value value of the attribute
	 */
	public void addAttribute(String attr, String value) 
	{
		attributes.put(attr, value);
	}
	
	/**
	 * Remove the Element c from the children collection
	 * 
	 * @param c the child to remove
	 */
	public void removeChild(Element c)
	{
		children.remove(c);
	}
	
	/**
	 * 	GETTERS AND SETTERS
	 */ 

	/**
	 * Get the parent
	 * 
	 * @return parent element
	 */
	public Element getParent()
	{
		return parent;
	}
	
	/**
	 * Set a parent
	 * 
	 * @param e the parent
	 */
	public void setParent(Element e)
	{
		parent = e;
	}	
	
	/**
	 * Does this element have a parent?
	 * 
	 * @return true on parent
	 */
	public boolean hasParent()
	{
		return parent != null;
	}
	
	/**
	 * Get the layer
	 * 
	 * @return layer
	 */
	public Layer getLayer()
	{
		return layer;
	}
	
	/**
	 * Get the attributes for this element
	 * 
	 * @return attributes
	 */
	public HashMap<String,String> getAttributes()
	{
		return attributes;
	}
	
	/**
	 * Get the children of this Element
	 * 
	 * @return children
	 */
	public LinkedList<Element> getChildren()
	{
		return children;
	}
	
	/**
	 * Get the tag name
	 * 
	 * @return tagstring
	 */
	public String getTag()
	{
		return tag;
	}	
	
	/**
	 * Get the stylesheet selector
	 * 
	 * @return style for this element
	 */
	public StylesheetSelector getSelector()
	{
		return css;
	}
}
