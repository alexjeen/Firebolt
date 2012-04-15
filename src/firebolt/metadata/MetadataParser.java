package firebolt.metadata;

import firebolt.Element;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

/**
 * @author Alex Jeensma
 */
public class MetadataParser {
	/**
	 * Constant hashmap that holds the metadata objects
	 * Maps a short string like attr to a object
	 */
    public final static HashMap<String,Metadata> metaDataObjects = new HashMap<String,Metadata>();
    static {
    	metaDataObjects.put("attr", new Attribute());
    	metaDataObjects.put("css", new CSS());
	}	
	
	/**
	 * Parse the metadata given with an element
	 * 
	 * @param el
	 * @param attributes
	 */
	public MetadataParser(Element el, HashMap<String,String> attributes)
	{
		Iterator<Entry<String, String>> entries = attributes.entrySet().iterator();
		
		try
		{
			while (entries.hasNext()) {
				Map.Entry<String,String> entry = (Map.Entry<String,String>)entries.next();
			    String key = (String)entry.getKey();
			    String value = (String)entry.getValue();
			    
			    StringTokenizer parsedKey = new StringTokenizer(key, ":");
			    
			    while(parsedKey.hasMoreTokens()) {
			    	String namespace = parsedKey.nextToken();
			    	String namespace_value = parsedKey.nextToken();
			    	
			    	Metadata m = metaDataObjects.get(namespace);
			    	if(m == null) {
			    		throw new Exception("Namespace " + namespace + " not found..");
			    	}
			    	m.setMetadata(el, namespace_value, value);
			    	m.parse();
			    }
			    	
			}			
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
}
