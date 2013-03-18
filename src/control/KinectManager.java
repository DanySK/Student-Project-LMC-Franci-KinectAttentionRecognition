package control;

import java.util.HashMap;

import observer.CalibrationCompleteObs;
import observer.NewUserObs;

import org.OpenNI.Context;
import org.OpenNI.DepthGenerator;
import org.OpenNI.GeneralException;
import org.OpenNI.ImageGenerator;
import org.OpenNI.OutArg;
import org.OpenNI.Point3D;
import org.OpenNI.PoseDetectionCapability;
import org.OpenNI.ScriptNode;
import org.OpenNI.SkeletonCapability;
import org.OpenNI.SkeletonJoint;
import org.OpenNI.SkeletonJointPosition;
import org.OpenNI.SkeletonProfile;
import org.OpenNI.StatusException;
import org.OpenNI.UserGenerator;

import util.LoadParam;
import util.PosAndTime;
import util.UserData;
import view.AttentionIndexFrame;
import view.SceneDrawer;

/** Class responsible of managing the OpenNI tools.
 * 
 * @author Silvia Franci
 *
 */
public class KinectManager {
	/**
	 * Maximum number of user detectable per frame
	 */
	private final String CONFIG_XML_FILE = "OpenNIConfig.xml";
	private final String APP_CONFIG_XML = "AppConfig.xml";
	public int MAX_USER;
	private OutArg<ScriptNode> scriptNode;
	private Context context;
	
	private DepthGenerator depthGen;
	private ImageGenerator imageGen;

	private UserGenerator userGen;
	private SkeletonCapability skeletonCap;
	private PoseDetectionCapability poseDetectionCap;
	private String calibPose = null;
	private HashMap<Integer, HashMap<SkeletonJoint, PosAndTime>> joints;
	private boolean trackingStarted[];

	private boolean shouldRun = true;

	private UserAnalizer[] userAnalizer;
	private int analizer;
	
	private SceneDrawer view;
	private AttentionIndexFrame attFrame;
	private boolean draw;
	
	private int TF;
	private long necessaryTime;
	private boolean userDataReady[],stop[];
	private int[] tf;
	private UserData[] data;
	
	SkeletonJoint marker[] = {	SkeletonJoint.HEAD,
			SkeletonJoint.NECK,
			SkeletonJoint.LEFT_SHOULDER,	SkeletonJoint.RIGHT_SHOULDER,
			SkeletonJoint.LEFT_ELBOW,		SkeletonJoint.RIGHT_ELBOW,
			SkeletonJoint.LEFT_HAND,		SkeletonJoint.RIGHT_HAND,
			SkeletonJoint.TORSO,			
			SkeletonJoint.LEFT_HIP,			SkeletonJoint.RIGHT_HIP,
			SkeletonJoint.LEFT_KNEE,		SkeletonJoint.RIGHT_KNEE,
			SkeletonJoint.LEFT_FOOT,		SkeletonJoint.RIGHT_FOOT, 
			 };

	/**
	 * @Constructor
	 */
	public KinectManager(){
		LoadParam lp = new LoadParam();
		lp.readParam(APP_CONFIG_XML);
		draw = lp.getDraw();
		TF = lp.getTf();
		necessaryTime = lp.getTime();
		MAX_USER=lp.getMaxUser();
		
		userDataReady = new boolean[MAX_USER];
		tf = new int[MAX_USER];
		data = new UserData[MAX_USER];
		stop = new boolean[MAX_USER];
		trackingStarted = new boolean[MAX_USER];
		userAnalizer = new UserAnalizer[MAX_USER];
		
		analizer=0;
		
		for(int i=0;i<MAX_USER;i++){
			trackingStarted[i]=false;
			userDataReady[i]=false;
			tf[i]=TF;
		}
		
		setUpEnvironment();
		
		attFrame = new AttentionIndexFrame(this);
		attFrame.setVisible(true);
		
		if(draw){
			view = new SceneDrawer(this,lp.getDrawSkel(),lp.getDrawRgb(),lp.getDrawID());
			view.setVisible(true);
		}
		
			
	}
	
	public void setUpEnvironment(){

		try {
			scriptNode = new OutArg<ScriptNode>();

			context = Context.createFromXmlFile(CONFIG_XML_FILE, scriptNode);
			
			if(draw){
				depthGen = DepthGenerator.create(context);
				imageGen = ImageGenerator.create(context);
			}

			userGen = UserGenerator.create(context);

			skeletonCap = userGen.getSkeletonCapability();
			poseDetectionCap = userGen.getPoseDetectionCapability();

			userGen.getNewUserEvent().addObserver(new NewUserObs(this));

			skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObs(this));

			joints = new HashMap<Integer, HashMap<SkeletonJoint,PosAndTime>>();

			calibPose = skeletonCap.getSkeletonCalibrationPose();

			skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);

			context.startGeneratingAll();
			
		} catch (GeneralException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Start the updating of the skeleton position and the GUI for the depth map visualization
	 */
	public void run(){
		while(shouldRun){
			try {
				context.waitAnyUpdateAll();
			} catch (StatusException e) {
				e.printStackTrace();
			}
			
			updateSkeletonPosition();
			
			if(draw){
				view.updateFrame();
				view.repaint();
			}
			
		}
		
		if(draw){
			view.dispose();
		}
		
		log("muoio");
	}
	
	/**
	 * Update the skeleton position of all users in the scene
	 */
	public void updateSkeletonPosition(){
		try {

			int[] users = userGen.getUsers();
			
//			System.out.println("[KINECT MANAGER] numero users: "+users.length);
			for(int i = 0; i < users.length; i++){
				if(trackingStarted[i]){
					long timeStamp = System.currentTimeMillis();
					
					for(int j=0;j<marker.length;j++){
						SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(users[i], marker[j]);
						
						if (pos.getPosition().getZ() != 0){
														
							SkeletonJointPosition posReal = new SkeletonJointPosition(pos.getPosition(), pos.getConfidence());

							joints.get(users[i]).put(marker[j], new PosAndTime(timeStamp,posReal));
							
						}else{
							joints.get(users[i]).put(marker[j], new PosAndTime(timeStamp, new SkeletonJointPosition(new Point3D(), 0)));
							System.out.println("posizione sconosciuta");
						}
					}
					
					userDataReady(i,joints.get(users[i]),getUserGenerator().getUserCoM(users[i]));
//					System.out.println("[KM] data user "+users[i]+"  READY");
				}
			}

		} catch (StatusException e) {
			log("ECCEZIONE!!!!!!!!!!!!!!!!!! ");
			e.printStackTrace();
		}
	}
	
	/**
	 * Return the skeleton capability
	 * @return SkeletonCapability
	 */
	public SkeletonCapability getSkeletonCap(){
		return skeletonCap;
	}
	
	/**
	 * Return the pose detection capability
	 * @return PoseDetectionCapability
	 */
	public PoseDetectionCapability getPoseCap(){
		return poseDetectionCap;
	}
	
	
	public String getCalibPose(){
		return calibPose;
	}

	/**
	 * Return the Hashmap with the skeleton positions of all users
	 * @return Hashmap
	 */
	public HashMap<Integer, HashMap<SkeletonJoint, PosAndTime>> getJointList(){
		return joints;
	}
	
	/**
	 * Return the generator of the depth map
	 * @return DepthGenerator
	 */
	public DepthGenerator getDeptGen(){
		return depthGen;
	}
	
	/**
	 * Return the generator of the RGB image
	 * @return ImageGenerator
	 */
	public ImageGenerator getImageGen(){
		return imageGen;
	}
	
	/**
	 * Return the generator resposible for the recognition of users
	 * @return
	 */
	public UserGenerator getUserGenerator(){
		return userGen;
	}

	/**
	 * Return if the tracking of the user with ID index+1 is started
	 * @param index User's ID - 1
	 * @return Tracking started
	 */
	public boolean isTrackingStarted(int index) {
		return trackingStarted[index];
	}

	/**
	 * Begins the user tracking
	 * @param index User's ID - 1
	 */
	public void setTrakingStarted(int index) {
		trackingStarted[index] = true;
		//creo un thread che si occupa di analizzare quel determinato user
		userAnalizer[index] = new UserAnalizer(index,this,attFrame,necessaryTime);
		userAnalizer[index].start();
		stop[index]=false;
		analizer++;
	}
		
	public synchronized void userDataReady(int user, HashMap<SkeletonJoint, PosAndTime> j, Point3D com){
		tf[user]--;
		if(tf[user]==0){
//			System.out.println("[FRAME MANAGER] data pronti");
			data[user] = new UserData(user,j,com);
			userDataReady[user]=true;
			notifyAll();
		}
	}
	
	public synchronized UserData getUserData(int user) throws InterruptedException{
//		System.out.println("[FRAME MANAGER] richiesti dati");
		while(!(userDataReady[user] || stop[user])){
				wait();
		}
		tf[user]=TF;
		userDataReady[user]=false;
		
		if(stop[user]){
			data[user]=null;
		}
		return data[user];
	}
	
	/**
	 * Stop the KinectManager and the UserAnalizers
	 */
	public void stopManager() {
		log("chiamato stopManager");
		shouldRun=false;
		for(int i=0;i<analizer;i++){
			userAnalizer[i].stopAnalizer();
		}
		stopAll();
		attFrame.dispose();
		
	}
	
	public synchronized void stopAll() {
		for(int i=0;i<MAX_USER;i++){
			stop[i]=true;
		}
		notifyAll();
	}
	
	public synchronized void stopOne(int index){
		stop[index]=true;
		notifyAll();
	}
	
	/**
	 * Set the index of the user that has been lost
	 * @param index User's ID - 1
	 */
	public void lostUser(int index){
		log("PERSO UTENTE "+index);
		trackingStarted[index]=false;
		userAnalizer[index].stopAnalizer();
		analizer--;
		
		stopOne(index);
		
		attFrame.lostUser(index);
	}
	
	public void log(String s){
		System.out.println("[KINECT MANAGER]: "+s);
	}
}
