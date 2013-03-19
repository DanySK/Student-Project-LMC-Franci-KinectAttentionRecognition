package featureExtractor;

import java.util.HashMap;

import org.OpenNI.Point3D;

import util.PosAndTime;
import util.Vector3D;

/** Calculates and interprets the posture data: if the user is sitting or standing, if the user is imbalanced, 
 * calculates the Base of Support and if the uses is in Single Support Phase or Double Support Phase  
 * 
 * @author Silvia Franci
 *
 */
public class PoseParameterCalculator {
	private boolean first;
	private float userHeight, shoulderDist,baseSupport, diffY, diffZ;
	private Point3D pt[],com;
	private int attentionIndex;
	private HashMap<String, Integer> w;
	
	/**
	 * @param w 
	 * @Constructor
	 */
	public PoseParameterCalculator(HashMap<String, Integer> w){
		this.w=w;
		pt = new Point3D[4];
		first=true;
	}
	
	/**
	 * Update the position of the right and left shoulder and the right and left foot and recalculates the parameter
	 * @param rightS Right shoulder position
	 * @param leftS Left shoulder position
	 * @param rightF Right foot position
	 * @param leftF Left foot position
	 * @param com Center of mass
	 */
	public int updatePosition(PosAndTime rightS,PosAndTime leftS,PosAndTime rightF,PosAndTime leftF,Point3D com){
		
		if(rightS.getConf()!=0 || leftS.getConf()!=0 || rightF.getConf()!=0 || leftF.getConf()!=0){
			pt[0]=rightS.getPos();
			pt[1]=leftS.getPos();
			pt[2]=rightF.getPos();
			pt[3]=leftF.getPos();
			
			this.com=com;
			attentionIndex=0;
			return detectPosition();		
		}
		
		return 0;
	}

	private int detectPosition() {
					
		float dr = pt[0].getY()-pt[2].getY();
		float dl = pt[1].getY()-pt[3].getY();
		
		baseSupport = Math.abs(pt[2].getX()-pt[3].getX());
		diffY = Math.abs(pt[2].getY()-pt[3].getY());
		diffZ = Math.abs(pt[2].getZ()-pt[3].getZ());
		
		if(first){
			userHeight=(dr+dl)/2;
			
			Vector3D vec = new Vector3D(pt[0],pt[1]);
			shoulderDist = vec.getModule();
			
			first=false;
		}
		
		float tmp = (dr+dl)/2;
		
		if(tmp<(userHeight*90/100)){
			attentionIndex+=w.get("sitting");
//			System.out.println("[SITCALC] user: è seduto");
		}else{
			attentionIndex+=w.get("standing");
//			System.out.println("[SITCALC] user: è in piedi");
			
			if(diffY>25 && diffZ<100){
				attentionIndex+=w.get("singleSupport");
//				System.out.println("SINGLE SUPPORT");
			}else{
				attentionIndex+=w.get("doubleSupport");
//				System.out.println("DOUBLE SUPPORT");
			}
		}		
		
		float mediaFoot = (pt[2].getX()+pt[3].getX())/2;
		float scost = mediaFoot-com.getX();
		float perc = Math.abs(scost*100/mediaFoot);
		
		if(scost>0 && perc>20){
			attentionIndex+=w.get("leftImbalance");
//			System.out.println("SBILANCIAMENTO A SINISTRA");
		}else if(scost<0 && perc>20){
			attentionIndex+=w.get("rightImbalance");
//			System.out.println("SBILANCIAMENTO A DESTRA");
		}else{
			attentionIndex+=w.get("centered");
//			System.out.println("PESO CENTRATO");
		}
		
		if(baseSupport<(shoulderDist*60/100)){
			attentionIndex+=w.get("smallBoS"); //piccola
//			System.out.println("BOS PICCOLA");
		}else if(baseSupport>=(shoulderDist*60/100) && baseSupport<(shoulderDist+shoulderDist*10/100)){
			attentionIndex+=w.get("normalBoS"); //normale
//			System.out.println("BOS NORMALE");
		}else{
			attentionIndex+=w.get("largeBoS"); //grande
//			System.out.println("BOS GRANDE");
		}
		
		return attentionIndex;			
	}
		
}
