package observer;

import java.util.HashMap;

import org.OpenNI.CalibrationProgressEventArgs;
import org.OpenNI.CalibrationProgressStatus;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.StatusException;

import util.PosAndTime;

import control.KinectManager;

/** This observer notifies the KinectManager that the calibration is complete.
 * 
 * @author Silvia Franci
 *
 */
public class CalibrationCompleteObs implements IObserver<CalibrationProgressEventArgs>{
	private KinectManager manager;
	
	/**
	 * @Constructor
	 * @param manager KinectManager
	 */
	public CalibrationCompleteObs(KinectManager manager){
		this.manager=manager;
	}
	
	@Override
	public void update(IObservable<CalibrationProgressEventArgs> observable,CalibrationProgressEventArgs args){
		System.out.println("[OBS] User: "+args.getUser()+"  Calibraion complete: " + args.getStatus());
		try{
			if (args.getStatus() == CalibrationProgressStatus.OK){
				System.out.println("[OBS] starting tracking "  +args.getUser());
					manager.getSkeletonCap().startTracking(args.getUser());
	                manager.getJointList().put(new Integer(args.getUser()), new HashMap<SkeletonJoint, PosAndTime>());
//	                System.out.println("[OBS] inserita entry nuovo utente   "+args.getUser()+"    in joints");
	                
	                manager.setTrakingStarted(args.getUser()-1);
			}else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT){
					manager.getSkeletonCap().requestSkeletonCalibration(args.getUser(), true);
			}
		} catch (StatusException e){
			e.printStackTrace();
		}
	}
}
