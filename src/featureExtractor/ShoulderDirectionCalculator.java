package featureExtractor;

import util.PosAndTime;
import util.Vector3D;

/** Calculate the direction (in degree) of the user's body with respect to the camera
 * 
 * @author Silvia Franci
 *
 */
public class ShoulderDirectionCalculator {
	private PosAndTime pt[];
	private float angle;
	private int attentionIndex;
	
	/**
	 * @Constructor
	 */
	public ShoulderDirectionCalculator(){
		pt = new PosAndTime[3];
	}
	
	/**
	 * Update the position of the left and right shoulder and the torso. Recalculates the angle 
	 * @param leftS Left shoulder position
	 * @param rightS Right shoulder position
	 * @param torso Torso position
	 */
	public int updateUserDir(PosAndTime leftS,PosAndTime rightS,PosAndTime torso){
							
		if(leftS.getConf()!=0 || rightS.getConf()!=0 || torso.getConf()!=0){
			pt[0] = leftS;
			pt[1] = rightS; 
			pt[2] = torso;
						
			return calcShoulderDirection();
		}
		return 0;		
	}
	
	private int calcShoulderDirection(){
		
		Vector3D v1 = new Vector3D(pt[2].getPos(),pt[0].getPos()); //vettore spallaL->torso
		
		Vector3D v2 = new Vector3D(pt[1].getPos(),pt[0].getPos()); //vettore spallaL->spallaR
	
		Vector3D norm = v1.crossProduct(v2).normalize();
		
		//angolo rispetto all'asse z (uscente dallo schermo)
		angle = (float) Math.toDegrees(Math.acos(Math.abs(norm.getZ())));
		
//		System.out.println("[SHOULDER] ANGOLO: "+angle);
		
//		attentionIndex = 14-Math.round(angle*14/90);
		if(angle<15){
			attentionIndex=14;
		}else{
			attentionIndex=2;
		}
		
		return attentionIndex;
		
	}
	
}
	