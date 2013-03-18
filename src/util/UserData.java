package util;

import java.util.HashMap;

import org.OpenNI.Point3D;
import org.OpenNI.SkeletonJoint;

/** Class representing the data respective of a single detected user.
 * 
 * @author Silvia Franci
 *
 */
public class UserData {
	private int user;
	private HashMap<SkeletonJoint, PosAndTime> joint;
	private Point3D com;
	
	/**
	 * @Constructor
	 * @param user User's ID
	 * @param joint Skeleton joint position of the user
	 * @param cop User's centre of mass
	 */
	public UserData(int user,HashMap<SkeletonJoint, PosAndTime> joint, Point3D cop){
		this.user=user;
		this.joint=joint;
		this.com=cop;
	}
	
	/**
	 * Return the user's ID
	 * @return ID
	 */
	public int getUser() {
		return user;
	}
	
	/**
	 * Set the user's ID
	 * @param user
	 */
	public void setUser(int user) {
		this.user = user;
	}
	
	/**
	 * Return the Hashmap containing the user's skeleton position 
	 * @return Hashmap
	 */
	public HashMap<SkeletonJoint, PosAndTime> getJoint() {
		return joint;
	}
	
	/**
	 * Set the Hashmap containing the user's skeleton position 
	 * @param joint Hashmap
	 */
	public void setJoint(HashMap<SkeletonJoint, PosAndTime> joint) {
		this.joint = joint;
	}
	
	/**
	 * Return the centre of mass
	 * @return Centre of mass
	 */
	public Point3D getCom() {
		return com;
	}
	
	/**
	 * Set the centre of mass
	 * @param com Centre of mass
	 */
	public void setCom(Point3D com) {
		this.com = com;
	}

}