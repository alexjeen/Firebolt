package firebolt;

import firebolt.css.*;

/**
 * Represents the final HTML document, including references
 * to the CSS files, Image file and body of the document
 * 
 * @author Alex Jeensma
 */
public class Document {
	/**
	 * Stylesheet of the document
	 */
	private Stylesheet stylesheet;
	
	/**
	 * Configuration object
	 */
	private Configuration config;
	
	/**
	 * Body element (top element)
	 */
	private Element body;

	/**
	 * Create a new document
	 */
	public Document()
	{
		stylesheet = new Stylesheet();
	}
	
	/**
	 * Set the configuration object
	 * 
	 * @param c the config
	 */
	public void setConfiguration(Configuration c)
	{
		config = c;
	}
	
	/**
	 * Set the body
	 * 
	 * @param e body element (can only be one!)
	 */
	public void setBody(Element e)
	{
		body = e;
	}
	
	/**
	 * Add a StylesheetSelector to the Document it's CSS
	 * 
	 * @param s the selector
	 */
	public void addCSSLine(StylesheetSelector s)
	{
		stylesheet.addSelector(s);
	}
	
	/**
	 * Return the document as string
	 * 
	 * @return HTML string
	 */
	public String print()
	{
		String HTML = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
		
		HTML += "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n";
		HTML += "<head>\n";
		HTML += "\t<title>Your Title</title>\n";
		
		HTML += "\t<style type=\"text/css\">\n";
		HTML += stylesheet.printRecursive();
		HTML += "\t</style>\n";
		
		HTML += "</head>\n";
		
		String appendElement = config.get("comment-template");
		
		if(appendElement != null) {
			String[] aP = appendElement.split("\\|");
			HTML += body.printRecursive(0, aP[0], aP[1]);
		}
		else {
			HTML += body.printRecursive(0, "", "");
		}
		
		HTML += "</html>";
		
		return HTML;
	}
}
