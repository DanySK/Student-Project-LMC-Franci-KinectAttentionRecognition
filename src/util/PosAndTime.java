package util;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJointPosition;
/** Class containing information about the skeleton joint position and the moment of the capture
 * 
 * @author Silvia Franci
 *
 */
public class PosAndTime {
	private SkeletonJointPosition sj;
	private long time;
	
	/**
	 * @Constructor
	 * @param time Moment of the capture
	 * @param sj Skeleton joint position and the relative confidence
	 */
	public PosAndTime(long time, SkeletonJointPosition sj){
		this.time=time;
		this.sj=sj;
	}
	
	/**
	 * Return the skeleton joint position
	 * @return Skeleton joint position
	 */
	public Point3D getPos() {
		return sj.getPosition();
	}
	
	/**
	 * Return the confidence: how the position is accurate
	 * @return Confidence
	 */
	public float getConf(){
		return sj.getConfidence();
	}

	/**
	 * Return the moment of the capture
	 * @return Moment of capture
	 */
	public long getTime() {
		return time;
	}
	
	/**
	 * Set the moment of capture
	 * @param time
	 */
	public void setTime(long time) {
		this.time = time;
	}
	
}
