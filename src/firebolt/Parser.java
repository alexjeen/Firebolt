package firebolt;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
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
	 * The folder to the output
	 */
	private static String output;
	
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
	public Parser(String path, String outputPath) throws Exception
	{
		psd_file = new File(path);
		

		// check for trailing slash
		if(outputPath.charAt(outputPath.length() - 1) != '/') {
			outputPath += "/";
		}
		
		output = outputPath;		
		
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
	
		// reset body 
		body.getSelector().addProperty("margin", "0px").addProperty("padding", "0px");
		
		// second element == configuration (optional)
		if(parsedPSD.getLayersCount() > 1 && parsedPSD.getLayer(1).toString().equals("configuration")) {
			Configuration config = readConfig(parsedPSD.getLayer(1));
			document.setConfiguration(config);
		}
		else {
			document.setConfiguration(new Configuration());
		}
		
		body.recursiveBuildStyle();
		
		BufferedWriter html = new BufferedWriter(new FileWriter(output + "index.html"));
		
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
		LinkedList<Element> children = new LinkedList<Element>();
		
		document.addCSSLine(e.getSelector());
		for(int x = 0; x < l.getLayersCount(); x++)
		{
			// traverse children
			Layer cL = l.getLayer(x);
			Element c = recursiveParse(cL, e);
			
			e.addChild(c);	
			
			LayerMatcher lm1 = new LayerMatcher(l);
			LayerMatcher lm2 = new LayerMatcher(cL);
			
			String dummy1ID = lm1.getID();
			String dummy2ID = lm2.getID();
			
			if(l.getType() == LayerType.FOLDER && cL.getType() == LayerType.NORMAL && dummy1ID.equals(dummy2ID)) {
				e.merge(c);
			}
			else {
				children.add(c);
			}
		}	
		
		// iterate the siblings
		for(Element parentChild : children)
		{
			for(Element childChild : children)
			{
				parentChild.addSibling(childChild);
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
		// parse
		LayerMatcher lm = new LayerMatcher(layer);
		// parsing done, create the Element
		return new Element(lm.getTag(), lm.getID(), lm.getAttributes(), layer);
	}
	
	/**
	 * Gets the output folder
	 * 
	 * @return the output folder including a trailing slash
	 */
	public static String getOutput()
	{
		return output;
	}
	
}
