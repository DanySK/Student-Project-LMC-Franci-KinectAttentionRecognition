package observer;

import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.StatusException;
import org.OpenNI.UserEventArgs;

import control.KinectManager;

/** This observer notifies the KinectManager that a new user has been detected.
 * 
 * @author Silvia Franci
 *
 */
public class NewUserObs implements IObserver<UserEventArgs>{
	private KinectManager manager;
	
	/**
	 * @Constructor
	 * @param manager KinectManager
	 */
	public NewUserObs(KinectManager manager){
		this.manager=manager;
	}

	@Override
	public void update(IObservable<UserEventArgs> observer, UserEventArgs args) {
		System.out.println("[OBS] New user " + args.getId());
		try{
				System.out.println("[OBS] start calibration "+ args.getId());
				manager.getSkeletonCap().requestSkeletonCalibration(args.getId(), true);
		} catch (StatusException e){
			e.printStackTrace();
		}
		
	}

}
