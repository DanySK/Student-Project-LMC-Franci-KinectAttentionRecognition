package featureExtractor;

/**Calculates the time that the user spent in front of the camera.
 * 
 * @author Silvia Franci
 *
 */
public class TimeCalculator {
	private long time[];
	private boolean first;
	private int attentionIndex;
	private long necessaryTime;
	
	/**
	 * @Constructor
	 */
	public TimeCalculator(long necessaryTime){
		this.necessaryTime=necessaryTime;
		time = new long[2];
		first=true;
	}
	
	/**
	 * Update the time and recalculates the total time
	 * @param t Actual time
	 */
	public int updateUserTime(long t) {
		
		if(first){
			time[0]=t;
			first=false;
			return 0;
		}else{
			time[1]=t-time[0];
			
//			System.out.println("[TIME] presente da :"+time[1]+" millisec");
			
			attentionIndex = (int) (time[1]*14/necessaryTime);
			
			return attentionIndex;
		}	
	}
	
	public void reset(){
		first=true;
	}
	
}
