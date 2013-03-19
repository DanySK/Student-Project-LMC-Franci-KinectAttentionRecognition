package control;

import java.util.HashMap;

import org.OpenNI.SkeletonJoint;

import featureExtractor.AttentionPoseDetector;
import featureExtractor.LostUserDetector;
import featureExtractor.PoseParameterCalculator;
import featureExtractor.ShoulderDirectionCalculator;
import featureExtractor.SpeedCalculator;
import featureExtractor.TimeCalculator;

import util.PosAndTime;
import util.UserData;
import view.AttentionIndexFrame;

/** Analize the data concerning one detected user
 * 
 * @author Silvia Franci
 *
 */
public class UserAnalizer extends Thread{
	private int index, attentionLevel;
	private boolean running,timeReached;
	private HashMap<SkeletonJoint, PosAndTime> joint;
	private KinectManager manager;
	
	private SpeedCalculator speedCalc;
	private ShoulderDirectionCalculator shoulderCalc;
	private TimeCalculator timeCalc;
	private PoseParameterCalculator poseCalc;
	private LostUserDetector lostDetect;
	private AttentionPoseDetector poseDetect;
	
	private AttentionIndexFrame gui;
	
	/**
	 * @Constructor
	 * @param index Index of the user
	 * @param frameManager Monitor for managing information with the KinectManager
	 * @param gui GUI for display the result
	 */
	public UserAnalizer(int index, KinectManager manager,AttentionIndexFrame gui,long necessaryTime){
		this.index=index;
		this.gui=gui;
		this.manager=manager;
		attentionLevel=0;
		timeReached=false;
		running = true;
		
		speedCalc = new SpeedCalculator(manager.getWeights().get("speed"));
		shoulderCalc = new ShoulderDirectionCalculator(manager.getWeights().get("shoulder")); 
		timeCalc = new TimeCalculator(necessaryTime,manager.getWeights().get("time"));
		poseCalc = new PoseParameterCalculator(manager.getWeights().get("pose")); 
		lostDetect = new LostUserDetector();
		poseDetect = new AttentionPoseDetector(manager.getWeights().get("attentivePose"));
	}
	
	@Override
	public void run(){
		int timeIndex = 0;
		log("started");
		 
		while(running){
			try {
				
				UserData data = manager.getUserData(index);
				
				if(data!=null){
					
					joint=data.getJoint();
					
					boolean lost = lostDetect.isUserLost(data.getCom(),joint.get(SkeletonJoint.TORSO).getTime());
					
					if(lost){
						manager.lostUser(index);
					}else{
						
						int speed = speedCalc.updateUsersSpeed(joint.get(SkeletonJoint.TORSO));
						attentionLevel+=speed;
//						log("1) speed= "+speed+"  al= "+attentionLevel);
						
						int shoulder = shoulderCalc.updateUserDir(joint.get(SkeletonJoint.LEFT_SHOULDER), joint.get(SkeletonJoint.RIGHT_SHOULDER), joint.get(SkeletonJoint.TORSO));
						attentionLevel+= shoulder;
//						log("2) shoulder= "+shoulder+"  al= "+attentionLevel);
						
						int pose = poseCalc.updatePosition(joint.get(SkeletonJoint.RIGHT_SHOULDER),joint.get(SkeletonJoint.LEFT_SHOULDER),joint.get(SkeletonJoint.RIGHT_FOOT),joint.get(SkeletonJoint.LEFT_FOOT),data.getCom());
						attentionLevel+=pose;
//						log("3) posa= "+pose+"  al= "+attentionLevel);
						
						if(shoulder>8){
							if(!timeReached){
								timeIndex = timeCalc.updateUserTime(joint.get(SkeletonJoint.TORSO).getTime());
								
								if(timeIndex==14){
									timeReached=true;
								}
							}else{
								timeIndex = 14;
							}
						}else{
							timeIndex=0;
							timeCalc.reset();
						}
						attentionLevel+=timeIndex;
//						log("4) tempo= "+timeIndex+"  al="+attentionLevel);						

						int aPose = poseDetect.isInAttentionPose(joint.get(SkeletonJoint.HEAD).getPos(),joint.get(SkeletonJoint.TORSO).getPos(),joint.get(SkeletonJoint.RIGHT_HAND).getPos(),joint.get(SkeletonJoint.LEFT_HAND).getPos(),joint.get(SkeletonJoint.RIGHT_ELBOW).getPos(),joint.get(SkeletonJoint.LEFT_ELBOW).getPos());
						attentionLevel+=aPose;
						
//						log("5)pose="+aPose+" ai="+attentionLevel);
						
						gui.setAttentionIndex(index, attentionLevel);
						attentionLevel=0;
					}
					
//					log("attention level = "+attentionLevel);
				}
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
				System.exit(0);
			}
			
		}
		log("muoio");
	}
	
	/**
	 * Stop the analizer
	 */
	public void stopAnalizer(){
		running=false;
	}
	
	private void log(String s){
		System.out.println("[USER ANALIZER "+index+"] "+s);
	}
}
