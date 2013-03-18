package view;

import java.awt.Graphics;
import java.util.HashMap;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.StatusException;

import util.PosAndTime;

import control.KinectManager;

/** Draw the skeleton of detected users
 * 
 * @author Silvia Franci
 *
 */
public class SkeletonDrawer {
	private KinectManager manager;
	
	/**
	 * @Constructor
	 * @param manager KinectManager
	 */
	public SkeletonDrawer(KinectManager manager){
		this.manager=manager;
	}
	
	/**
	 * Draw all the part of the skeleton
	 * @param g Graphics object
	 * @param user User's ID
	 * @throws StatusException
	 */
	public void drawSkeleton(Graphics g, int user) throws StatusException {
    	
    	HashMap<SkeletonJoint, PosAndTime> jointList = manager.getJointList().get(new Integer(user));
    	    	
    	drawLine(g, jointList, SkeletonJoint.HEAD, SkeletonJoint.NECK);
    	
    	drawLine(g, jointList, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.TORSO);
    	drawLine(g, jointList, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.TORSO);

    	drawLine(g, jointList, SkeletonJoint.NECK, SkeletonJoint.LEFT_SHOULDER);
    	drawLine(g, jointList, SkeletonJoint.LEFT_SHOULDER, SkeletonJoint.LEFT_ELBOW);
    	drawLine(g, jointList, SkeletonJoint.LEFT_ELBOW, SkeletonJoint.LEFT_HAND);

    	drawLine(g, jointList, SkeletonJoint.NECK, SkeletonJoint.RIGHT_SHOULDER);
    	drawLine(g, jointList, SkeletonJoint.RIGHT_SHOULDER, SkeletonJoint.RIGHT_ELBOW);
    	drawLine(g, jointList, SkeletonJoint.RIGHT_ELBOW, SkeletonJoint.RIGHT_HAND);
    	
    	drawLine(g, jointList, SkeletonJoint.LEFT_HIP, SkeletonJoint.TORSO);
    	drawLine(g, jointList, SkeletonJoint.RIGHT_HIP, SkeletonJoint.TORSO);
    	drawLine(g, jointList, SkeletonJoint.LEFT_HIP, SkeletonJoint.RIGHT_HIP);

    	drawLine(g, jointList, SkeletonJoint.LEFT_HIP, SkeletonJoint.LEFT_KNEE);
    	drawLine(g, jointList, SkeletonJoint.LEFT_KNEE, SkeletonJoint.LEFT_FOOT);

    	drawLine(g, jointList, SkeletonJoint.RIGHT_HIP, SkeletonJoint.RIGHT_KNEE);
    	drawLine(g, jointList, SkeletonJoint.RIGHT_KNEE, SkeletonJoint.RIGHT_FOOT);

    }
	
	/**
	 * Draw a single line between two joints
	 * @param g Graphics object
	 * @param jointList Hashmap with all the joints
	 * @param joint1 First joint
	 * @param joint2 Second joint
	 */
	void drawLine(Graphics g, HashMap<SkeletonJoint, PosAndTime> jointList, SkeletonJoint joint1, SkeletonJoint joint2){
		
		try {
			Point3D pos1 = manager.getDeptGen().convertRealWorldToProjective(jointList.get(joint1).getPos());
			
			Point3D pos2 = manager.getDeptGen().convertRealWorldToProjective(jointList.get(joint2).getPos());
			
//			System.out.println("[SKELETON] DENTRO A DRAW LINE");
			if (jointList.get(joint1).getConf() == 0 || jointList.get(joint2).getConf() == 0)
				return;
			
			g.drawLine((int)pos1.getX(), (int)pos1.getY(), (int)pos2.getX(), (int)pos2.getY());
			
		} catch (StatusException e) {
			e.printStackTrace();
		}
		
		
    }
}
