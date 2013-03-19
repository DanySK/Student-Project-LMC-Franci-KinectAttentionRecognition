package util;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * Class to read the application paramenter in the configuration file
 * 
 * @author Silvia Franci
 *
 */
public class LoadParam {
	private int tf,maxUser;
//	private int attPose,singleSupp,doubleSupp,standing,sitting,leftImb,rightImb,centered,nBoS,largeBoS,smallBos,shoulder,stat,slow,fast,time;
	private long necessaryTime;
	private boolean draw,skel,rgb,id;
	private HashMap<String, HashMap<String,Integer>> w;
	
	/**
	 * @Constructor
	 */
	public LoadParam(){
		w = new HashMap<String, HashMap<String,Integer>>();
	}
	
	/**
	 * Extract the parameters from the xml file in input
	 * @param file Input file
	 */
	public void readParam(String file){
		HashMap<String, Integer> tmp;
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(new File(file));
			doc.normalize();
			Element root = doc.getDocumentElement();
			
			NodeList attParam = root.getElementsByTagName("attentionParam");
						
			Element list = (Element) attParam.item(0);
			
			maxUser = Integer.parseInt(list.getElementsByTagName("maxUser").item(0).getFirstChild().getNodeValue());
			tf = Integer.parseInt(list.getElementsByTagName("tf").item(0).getFirstChild().getNodeValue());
			necessaryTime = Long.parseLong(list.getElementsByTagName("necessaryTime").item(0).getFirstChild().getNodeValue());
			
			NodeList feature = list.getElementsByTagName("featureWeight");
			
			for(int i=0;i<feature.item(0).getChildNodes().getLength();i++){
				tmp = new HashMap<String, Integer>();
				Node n = feature.item(0).getChildNodes().item(i);
				
				if(!n.getNodeName().equals("#text")){
//					System.out.println(s+"   "+n.getChildNodes().getLength());
					if(n.getChildNodes().getLength()==1){
//						System.out.println(n.getTextContent());
						tmp.put(n.getNodeName(), Integer.parseInt(n.getTextContent()));
					}else{
						for(int j=0;j<n.getChildNodes().getLength();j++){
							Node n2 = n.getChildNodes().item(j);
//							String s2 = n.getChildNodes().item(j).getNodeName();
							if(!n2.getNodeName().equals("#text")){
//								System.out.println(n2.getNodeName());
//								System.out.println(n2.getTextContent());
								tmp.put(n2.getNodeName(), Integer.parseInt(n2.getTextContent()));
							}
						}
					}
//					System.out.println(n.getChildNodes().getLength());
					
					w.put(n.getNodeName(), tmp);
				}
				
			}
			
			NodeList visParam = root.getElementsByTagName("visualizationParam");
//			System.out.println("Vis: ");
			
			list = (Element)visParam.item(0);
			
			draw = Boolean.parseBoolean(list.getAttribute("active"));
//			System.out.println(draw);
			
			if(draw){
				skel = Boolean.parseBoolean(list.getElementsByTagName("skeleton").item(0).getFirstChild().getNodeValue());
				rgb = Boolean.parseBoolean(list.getElementsByTagName("rgb").item(0).getFirstChild().getNodeValue());
				id = Boolean.parseBoolean(list.getElementsByTagName("printId").item(0).getFirstChild().getNodeValue());
//				System.out.println(skel+"  "+rgb+"   "+id);
			}
			
		} catch (Exception e) {
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
		return necessaryTime;
	}
	
	public HashMap<String, HashMap<String,Integer>> getWheights(){
		return w;
	}
	
	public static void main(String[] args){
		LoadParam p = new LoadParam();
		p.readParam("AppConfig.xml");
	}
}
