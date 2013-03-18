package featureExtractor;

import org.OpenNI.Point3D;
/**
 * Detect if the users has left the scene
 * @author Silvia Franci
 *
 */
public class LostUserDetector {
	private final int TIMEOUT = 2000;
	private long start;
	private boolean first;
	
	/**
	 * @Constructor 
	 */
	public LostUserDetector(){
		first=true;
	}
	
	/**
	 * Detect if the users has left the scene
	 * @param com Center of mass 3D coordinate
	 * @param t Timeout for detecting a lost user
	 * @return If the users has left the scene
	 */
	public boolean isUserLost(Point3D com,long t){
//		System.out.println("com   x= "+com.getX()+"   y= "+com.getY()+"  z= "+com.getZ());
		if(first){
			if(com.getX()==0 && com.getY()==0 && com.getZ()==0){
				start=t;
				first=false;
			}
			return false;
		}else{
			if(com.getX()==0 && com.getY()==0 && com.getZ()==0){
				long t2 = t;
				long tmp = t2 - start;
//				System.out.println("tmp= "+tmp+"  t2= "+t2+"  start= "+start);
				if(tmp>TIMEOUT){
					return true;
				}else{
					return false;
				}
			}else{
				first = true;
				return false;
			}
		}
		
		
		
	}
}
