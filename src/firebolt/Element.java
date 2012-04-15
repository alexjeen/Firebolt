package firebolt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.Map.Entry;
import psd.model.Layer;
import psd.parser.layer.LayerType;
import firebolt.css.Stylesheet;
import firebolt.css.StylesheetSelector;
import firebolt.metadata.MetadataParser;

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
	 * Siblings of the element, index == flow order
	 */
	private LinkedList<Element> siblings;
	
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
		siblings = new LinkedList<Element>();
		tag = tagname;
		layer = l;
		if(id.length() > 0) {
			attributes.put("id", id);
			css = new StylesheetSelector("#" + id);
		}
		else {
			css = new StylesheetSelector(tagname);
		}
		new MetadataParser(this, attr);
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
			
			// calculate next and previous in flow elements
			Element nif = getNextInFlow();
			Element pif = getPrevInFlow();

			StylesheetSelector parentSelector = parent.getSelector();
			
			if((y - yp) > 0 && pif == null) {
				// @TODO: according to the box model, the height should decrease - padding
				parentSelector.addProperty("padding-top", (y - yp) + "px");
				if(parentSelector.getProperty("height") != null) {
					int newHeight = Stylesheet.pixelsToInt(parentSelector.getProperty("height")) - (y - yp);
					parentSelector.addProperty("height", newHeight + "px");
				}
			}
			
			if(pif == null) {
				// check for the left margin of this element (judging by it's parent)
				
			}
			
			// check for margin top of this element
			if(nif != null) {
				Layer nifL = nif.getLayer();
				if((nifL.getY() - yb) > 0) {
					nif.getSelector().addProperty("margin-top", (nifL.getY() - yb) + "px");
				}
			}
			
			// calculate floats (http://www.w3schools.com/cssref/pr_class_float.asp)
			// a float occurs when this element has a sibling are on the same "level"
			if(nif != null || pif != null) {
				if(nif != null)
				{
					int nif_x = nif.getLayer().getX();
					
					// get the ranges for the Y axis of the next in flow element..
					int nif_y = nif.getLayer().getY();
					
					if(nif_y == y) {
						// @TODO: more complex comparison for parallel Y elements..
						css.addProperty("float", "left");
						nif.getSelector().addProperty("float", "left");
						
						if((nif_x - xr) > 0) {
							css.addProperty("margin-right", (nif_x - xr) + "px");
						}
					}
				}
				if(pif != null)
				{
					
				}
			}
			
		}
	
		for(Class<Heuristic> heur : Heuristic.getHeuristics())
		{
			try 
			{
				Heuristic heuristic = heur.newInstance();
				heuristic.changeElement(this);
			} 
			catch (InstantiationException e) 
			{
				System.out.println("Couldn't initiate Heuristic: " + e.getMessage());
			} 
			catch (IllegalAccessException e) 
			{
				System.out.println("Couldn't access Heuristic: " + e.getMessage());
			}
		}
		
	}
	
	/**
	 * Gets the next element in the flow
	 * 
	 * @see http://explainth.at/en/css/flow.shtml
	 * @return element next in flow, otherwise null
	 */	
	public Element getNextInFlow()
	{
		int thisIndex = siblings.indexOf(this);
		
		if(thisIndex == 0) {
			// this is the first element..
			return null;
		}
		else {
			return siblings.get(thisIndex - 1);
		}
	}
	
	/**
	 * Gets the prev element in the flow
	 * 
	 * @see http://explainth.at/en/css/flow.shtml
	 * @return element prev in flow, otherwise null
	 */		
	public Element getPrevInFlow()
	{
		int thisIndex = siblings.indexOf(this);
		int siblingsSize = siblings.size();
		
		if((thisIndex + 1) == siblingsSize || siblingsSize == 1) {
			// this is the last element, so it has no next
			return null;
		}
		else {
			// there must be a next
			return siblings.get(thisIndex + 1);
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
		if(tag.length() == 0) {
			return "";
		}
		
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
	 * Adds a sibling to this element
	 * 
	 * @param e the sibling
	 */
	public void addSibling(Element e)
	{
		siblings.add(e);
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
