package util;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Class to read the application paramenter in the configuration file
 * 
 * @author Silvia Franci
 *
 */
public class LoadParam {
	private int tf,maxUser;
	private long time;
	private boolean draw,skel,rgb,id;
	
	/**
	 * Extract the parameters from the xml file in input
	 * @param file Input file
	 */
	public void readParam(String file){
		
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(new File(file));
			
			Element root = doc.getDocumentElement();
			
			NodeList attParam = root.getElementsByTagName("attentionParam");
			
			Element list = (Element) attParam.item(0);
			
			maxUser = Integer.parseInt(list.getElementsByTagName("maxUser").item(0).getFirstChild().getNodeValue());
			tf = Integer.parseInt(list.getElementsByTagName("tf").item(0).getFirstChild().getNodeValue());
			time = Long.parseLong(list.getElementsByTagName("necessaryTime").item(0).getFirstChild().getNodeValue());
			
			
//			System.out.println(stf+"  "+time);
			
			NodeList visParam = root.getElementsByTagName("visualizationParam");
//			System.out.println("Vis: ");
			
			list = (Element)visParam.item(0);
			
			draw = Boolean.parseBoolean(list.getAttribute("active"));
			System.out.println(draw);
			
			if(draw){
				skel = Boolean.parseBoolean(list.getElementsByTagName("skeleton").item(0).getFirstChild().getNodeValue());
				rgb = Boolean.parseBoolean(list.getElementsByTagName("rgb").item(0).getFirstChild().getNodeValue());
				id = Boolean.parseBoolean(list.getElementsByTagName("printId").item(0).getFirstChild().getNodeValue());
//				System.out.println(skel+"  "+rgb+"   "+id);
			}
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Return if the visualization of the scene is active
	 * @return If the visualization of the scene is active
	 */
	public boolean getDraw(){
		return draw;
	}
	
	/**
	 * Return the maximum number of detectable users
	 * @return Maximum number of detectable users
	 */
	public int getMaxUser(){
		return maxUser;
	}
	
	/**
	 * Indicates after how many frames must be calculated the attention index
	 * @return Number of frames
	 */
	public int getTf(){
		return tf;
	}
	
	/**
	 * Return if it's enabled the drawing of the skelenton
	 * @return If it's enabled the drawing of the skelenton
	 */
	public boolean getDrawSkel(){
		return skel;
	}
	
	/**
	 *  Return if it's enabled the rgb capture
	 * @return If it's enabled the rgb capture
	 */
	public boolean getDrawRgb(){
		return rgb;
	}
	
	/**
	 * Return if it's enabled the drawing of the users's ID
	 * @return If it's enabled the drawing of the users's ID
	 */
	public boolean getDrawID(){
		return id;
	}
	
	/**
	 * Return the time required to read a possible text on the screen.
	 * @return Time required to read a possible text on the screen
	 */
	public long getTime(){
		return time;
	}
	
	public static void main(String[] args){
		LoadParam p = new LoadParam();
		p.readParam("AppConfig.xml");
	}
}
