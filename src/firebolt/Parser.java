package firebolt;

import java.io.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.*;
import psd.model.*;
import psd.parser.layer.LayerType;

/**
 * Represents the main object that reads the file, does the parsing and generates the HTML
 * 
 * @author Alex Jeensma
 */
public class Parser {
	
	/**
	 * File object representing the Photoshop file
	 */
	private File psd_file;
	
	/**
	 * Holds the document
	 */
	private Document document;
	
	/**
	 * Constructor, sets up the parser and calls necessary methods
	 * 
	 * @param path path to the psd file that adheres to the specification
	 * @throws Exception 
	 */
	public Parser(String path) throws Exception
	{
		psd_file = new File(path);
		
		if(!psd_file.exists()) {
			throw new Exception("Read error: File '" + path + "' doesn't exist!");
		}
		
		document = new Document();
		
		// parse the PS file
		Psd parsedPSD = new Psd(psd_file);
		
		// element top <body>
		Element body = null;
		
		// top element == <body>
		body = recursiveParse(parsedPSD.getLayer(0), null);
		document.setBody(body);	
		
		// second element == configuration (optional)
		if(parsedPSD.getLayersCount() > 1 && parsedPSD.getLayer(1).toString().equals("configuration")) {
			Configuration config = readConfig(parsedPSD.getLayer(1));
			document.setConfiguration(config);
		}
		else {
			document.setConfiguration(new Configuration());
		}
		
		body.recursiveBuildStyle();
		
		BufferedWriter html = new BufferedWriter(new FileWriter("/Users/Alex/Documents/eclipse_workspace/Firebolt/html/index.html"));
		
		html.write(document.print());
		
		html.close();
	}
	
	/**
	 * Recursive parser, builds a ElementTree 
	 * 
	 * @param l
	 * @return
	 * @throws Exception
	 */
	private Element recursiveParse(Layer l, Element currentParent) throws Exception
	{
		// parent of current layer
		Element e = parseLayer(l);
		
		document.addCSSLine(e.getSelector());
		for(int x = 0; x < l.getLayersCount(); x++)
		{
			// traverse children
			Layer cL = l.getLayer(x);
			Element c = recursiveParse(cL, e);
			
			e.addChild(c);	
			
			if(l.getType() == LayerType.FOLDER && cL.getType() == LayerType.NORMAL && cL.toString().equals(l.toString())) {
				e.merge(c);
			}
		}		
		return e;
	}
	
	/**
	 * Read the configuration group
	 * 
	 * @param layer
	 * @return
	 * @throws Exception
	 */
	private Configuration readConfig(Layer group) throws Exception
	{
		Configuration c = new Configuration();
		
		for(int x = 0; group.getLayersCount() > x; x++)
		{
			String cfgStr = group.getLayer(x).toString();
			String[] parts = cfgStr.split("=");
			
			c.add(parts[0], parts[1].substring(1, parts[1].length() - 1));
		}
		
		return c;
	}
	
	/**
	 * Parses a layer into a element
	 * 
	 * @param layerName
	 * @throws Exception 
	 */
	private Element parseLayer(Layer layer) throws Exception
	{
		// match 1 = tagname
		// match 2 = id
		// match 3 = attributes
		String layerRegex = "^(<[a-z^>]*>)?([a-z]*)?(\\[.*\\])?$";
		String layerName = layer.toString();
		
		String tag = "";
		String id = "";
		HashMap<String,String> attributes = new HashMap<String,String>();
		
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
			
		}
		
		// parsing done, create the Element
		return new Element(tag, id, attributes, layer);
	}
	
}
