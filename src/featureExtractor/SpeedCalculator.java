package featureExtractor;

import java.util.HashMap;

import util.PosAndTime;
import util.Vector3D;

/** Calculates the user's speed
 * 
 * @author Silvia Franci
 *
 */
public class SpeedCalculator {
	private float speed;
	private PosAndTime pt[];
	private boolean first;
	private int attentionIndex;
	private HashMap<String, Integer> w;
	
	/**
	 * @param w 
	 * @Constructor
	 */
	public SpeedCalculator(HashMap<String, Integer> w){
		this.w=w;
		pt = new PosAndTime[2];
		first=true;
	}
	
	/**
	 * Update the position of the user and recalculates the speed
	 * @param torso Torso position
	 */
	public int updateUsersSpeed(PosAndTime torso){
		
		if(torso.getConf()!=0){
			if(first){
				pt[0] = torso;
			}else{
				pt[1] = torso;
			}
			return calcSpeed();
		}
		
		return 0;
	}
	
	private int calcSpeed(){
		if(first){
			first=false;
//			System.out.println("[SPEED] settato false");
			return 0;
		}else{
			Vector3D tmp = new Vector3D(pt[1].getPos(),pt[0].getPos());
			float dist = tmp.getModule();
						
			float dt = pt[1].getTime()-pt[0].getTime(); //ms
			
			speed = dist/dt; 
//			System.out.println("[SPEED] SPEED: "+speed);
			
			if(speed<0.2){
				attentionIndex=w.get("stationary");
//					System.out.println("UTENTE FERMO "+attentionIndex);
			}else if(speed>=0.2 && speed<1){
				attentionIndex=w.get("slow");
//					System.out.println("UTENTE LENTO "+attentionIndex);
			}else if(speed>=1){ 
				attentionIndex=w.get("fast");
//					System.out.println("UTENTE VELOCE "+attentionIndex);
			}
			
			pt[0]=pt[1];
			
			return attentionIndex;
			
		}
	}

}
