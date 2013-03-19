package featureExtractor;

import java.util.HashMap;

import org.OpenNI.Point3D;

import util.Vector3D;
/**
 * Detect if the user is in an attentive pose 
 * @author Silvia Franci
 *
 */
public class AttentionPoseDetector {
	private Vector3D headRightH,headLeftH,torsoRightE,torsoLeftE;
	private HashMap<String, Integer> w;
	
	/**
	 * @Constructor
	 * @param hashMap
	 */
	public AttentionPoseDetector(HashMap<String, Integer> w) {
		this.w=w;
	}

	/**
	 * Detect if the user is in an attentive pose
	 * @param head Head 3D coordinate
	 * @param torso Torso 3D coordinate 
	 * @param rightH Right hand 3D coordinate
	 * @param leftH Left hand 3D coordinate
	 * @param rightE Right elbow 3D coordinate
	 * @param leftE Left Elbow 3D coordinate
	 * @return Attention index relative to the attentive pose
	 */
	public int isInAttentionPose(Point3D head, Point3D torso, Point3D rightH, Point3D leftH, Point3D rightE, Point3D leftE){
//		log("HEAD   x= "+head.getX()+"  y= "+head.getY()+"  z= "+head.getZ());
//		log("TORSO   x= "+torso.getX()+"  y= "+torso.getY()+"  z= "+torso.getZ());
//		log("RIGHTH  x= "+rightH.getX()+"  y= "+rightH.getY()+"   z= "+rightH.getZ());
//		log("LEFTH  x= "+leftH.getX()+"  y= "+leftH.getY()+"   z= "+leftH.getZ());
//		log("RIGHTE  x= "+rightE.getX()+"  y= "+rightE.getY()+"   z= "+rightE.getZ());
//		log("LEFTE  x= "+leftE.getX()+"  y= "+leftE.getY()+"   z= "+leftE.getZ());
		
		
		headRightH = new Vector3D(head,rightH);
		headLeftH = new Vector3D(head,leftH);
		torsoRightE = new Vector3D(torso,rightE);
		torsoLeftE = new Vector3D(torso,leftE);
		
		if((headRightH.getModule()<300 && torsoRightE.getModule()<250) || (headLeftH.getModule()<300 && torsoLeftE.getModule()<250)){
			return w.get("attentivePose");
		}else{
			return 0;
		}
		
	}
	
}
