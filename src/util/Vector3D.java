package util;

import org.OpenNI.Point3D;

/**Class representing 3D vector.
 * 
 * @author Silvia Franci
 *
 */
public class Vector3D {
	private float x,y,z;
	
	/**
	 * @Constructor 
	 * @param x X value
	 * @param y Y value
	 * @param z Z value
	 */
	public Vector3D(float x, float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	/**
	 * @Constructor
	 * @param p1 First point
	 * @param p2 Second point
	 */
	public Vector3D(Point3D p1, Point3D p2){
		x=p1.getX()-p2.getX();
		y=p1.getY()-p2.getY();
		z=p1.getZ()-p2.getZ();
	}
	
	/**
	 * Return the X value
	 * @return X value
	 */
	public float getX() {
		return x;
	}

	/**
	 * Set the X value
	 * @param x X value
	 */
	public void setX(float x) {
		this.x = x;
	}
	
	/**
	 * Return the Y value
	 * @return Y value
	 */
	public float getY() {
		return y;
	}

	/**
	 * Set the Y value
	 * @param y Y value
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * Return the Z value
	 * @return Z value
	 */
	public float getZ() {
		return z;
	}

	/**
	 * Set the Z value
	 * @param z Z value
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Return the cross product of the vector with the vector v2
	 * @param v2 Vector
	 * @return Cross product
	 */
	public Vector3D crossProduct(Vector3D v2) {
		float xComp = this.getY()*v2.getZ()-this.getZ()*v2.getY();
		float yComp = this.getZ()*v2.getX()-this.getX()*v2.getZ();
		float zComp = this.getX()*v2.getY()-this.getY()*v2.getX();
		
		return new Vector3D(xComp,yComp,zComp);
	}
	
	/**
	 * Return the scalar representing the scalar product of the vector with the vector v2
	 * @param v2 Vector
	 * @return Scalar product
	 */
	public float scalarProduct(Vector3D v2){
		return x*v2.getX()+y*v2.getY()+z*v2.getZ();
	}
	
	/**
	 * Return the normalized vector
	 * @return Normalized Vector
	 */
	public Vector3D normalize(){
		float length = getModule();
		
		return new Vector3D(x/length,y/length,z/length);
	}
	
	/**
	 * Return the module of the vector
	 * @return Module
	 */
	public float getModule(){
		return (float) Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2)+Math.pow(z, 2));
	}
	
	/**
	 * Return the angle (in degree) between the vector and the vector v2
	 * @param v2 Vector
	 * @return Angle
	 */
	public float getAngle(Vector3D v2){
		float prod = scalarProduct(v2);
		float m1 = this.getModule();
		float m2 = v2.getModule();
		
		return (float) Math.acos(prod/(m1*m2));
	}
	
	public String toString(){
		return "x: "+x+"  y: "+y+"  z: "+z;
	}
	
}
