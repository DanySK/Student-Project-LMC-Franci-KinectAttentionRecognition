package view;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.swing.JFrame;

import org.OpenNI.DepthMetaData;
import org.OpenNI.ImageMetaData;
import org.OpenNI.Point3D;
import org.OpenNI.SceneMetaData;
import org.OpenNI.StatusException;

import control.KinectManager;

/** GUI for the visualisation of the depth map and the highlighting of the detected user
 * 
 * @author Silvia Franci
 *
 */
public class SceneDrawer extends JFrame {
	private static final long serialVersionUID = 1L;
	private KinectManager manager;

	private int width, height;
	private byte[] imgbytes;
	private float histogram[];
	private BufferedImage bimg, rgbImg;
	private SkeletonDrawer sd;
	private Dimension rgbDim;
	private int[] rgbImgArray;

	private boolean drawPixels = true;
	private boolean drawSkeleton;
	private boolean drawRgb;
	private boolean printID;

	Color colors[] = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE};
	
	/**
	 * @Constructor
	 * @param manager KinectManager
	 */
	public SceneDrawer(KinectManager manager,boolean drawSkeleton,boolean drawRgb,boolean printID) {
		this.setTitle("AttentionTraker");
		this.manager=manager;
		this.drawSkeleton=drawSkeleton;
		this.drawRgb=drawRgb;
		this.printID=printID;

		addWindowListener();
		addKeyListener();

		DepthMetaData depthMD = manager.getDeptGen().getMetaData();
		width = depthMD.getFullXRes();
		height = depthMD.getFullYRes();

		ImageMetaData imageMD = manager.getImageGen().getMetaData();
		rgbDim = new Dimension(imageMD.getFullXRes(), imageMD.getFullYRes());
		rgbImgArray = new int[rgbDim.width * rgbDim.height];
		rgbImg = new BufferedImage(rgbDim.width, rgbDim.height, BufferedImage.TYPE_INT_RGB);


		imgbytes = new byte[width*height*3];
		histogram = new float[10000];

		sd = new SkeletonDrawer(manager);

		this.pack();
	}

	@Override
	public Dimension getPreferredSize() {
		int w;
		if(drawRgb){
			w = rgbDim.width;
		}else{
			w = 0;
		}

		return new Dimension(width+w, height);
	}

	public void paint(Graphics g){
		//		System.out.println("[VIEWER]: DENTRO A PAINT");
		if (drawPixels){

			if(drawRgb){
				rgbImg.setRGB(0, 0, rgbDim.width, rgbDim.height, rgbImgArray, 0, rgbDim.width);
				g.drawImage(rgbImg, rgbDim.width, 0, this);
			}

			DataBufferByte dataBuffer = new DataBufferByte(imgbytes, width*height*3);

			WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null); 

			ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

			bimg = new BufferedImage(colorModel, raster, false, null);

			g.drawImage(bimg, 0, 0, null);
			//    		System.out.println("[VIEWER]: DISEGNATA IMMAGINE");
		}

		try{
			int[] users = manager.getUserGenerator().getUsers();
			for (int i = 0; i < users.length; ++i){
				Color c = colors[users[i]%colors.length];
				c = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());

				g.setColor(c);

				if (drawSkeleton && manager.isTrackingStarted(users[i]-1)){   //manager.getSkeletonCap().isSkeletonTracking(users[i])
					sd.drawSkeleton(g, users[i]);
				}
				
				if (printID){
					Point3D com = manager.getDeptGen().convertRealWorldToProjective(manager.getUserGenerator().getUserCoM(users[i]));
					String label = null;
					label = new String(""+users[i]);
					
					g.drawString(label, (int)com.getX(), (int)com.getY());
				}

			}
		} catch (StatusException e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the data relative to the single frame
	 */
	public void updateFrame() {
		int i = 0;
		int r = 0;
		int g = 0;
		int b = 0;

		ImageMetaData imageMD = manager.getImageGen().getMetaData();

		DepthMetaData depthMD = manager.getDeptGen().getMetaData();
		SceneMetaData sceneMD = manager.getUserGenerator().getUserPixels(0);

		ByteBuffer rgbBuffer = imageMD.getData().createByteBuffer();
		
		for (int x = 0; x < rgbDim.width; x++) {
			for (int y = 0; y < rgbDim.height; y++) {
				i = y * rgbDim.width + x;
				r = rgbBuffer.get(i * 3) & 0xff;
				g = rgbBuffer.get(i * 3 + 1) & 0xff;
				b = rgbBuffer.get(i * 3 + 2) & 0xff;
				rgbImgArray[i] = (r << 16) | (g << 8) | b;
			}
		}

		ShortBuffer scene = sceneMD.getData().createShortBuffer();
		ShortBuffer depth = depthMD.getData().createShortBuffer();
		calcHist(depth);
		depth.rewind();

		//builds an image buffer according to the frequency of each depth value in the histogram
		while(depth.remaining() > 0){
			int pos = depth.position();
			short pixel = depth.get();
			short user = scene.get();

			imgbytes[3*pos] = 0;
			imgbytes[3*pos+1] = 0;
			imgbytes[3*pos+2] = 0;                	

			if (pixel != 0){
				int colorID = user % (colors.length-1);
				if (user == 0){
					colorID = colors.length-1;
				}
				if (pixel != 0)	{
					float histValue = histogram[pixel];
					imgbytes[3*pos] = (byte)(histValue*colors[colorID].getRed());
					imgbytes[3*pos+1] = (byte)(histValue*colors[colorID].getGreen());
					imgbytes[3*pos+2] = (byte)(histValue*colors[colorID].getBlue());
				}
			}
		}
	}

	/**
	 * Calculate the histogram of teh frame 
	 * @param depth Depthmap
	 */
	private void calcHist(ShortBuffer depth) {
		// reset
		for (int i = 0; i < histogram.length; ++i)
			histogram[i] = 0;

		depth.rewind();

		int points = 0;
		while(depth.remaining() > 0) {
			short depthVal = depth.get();
			if (depthVal != 0){
				histogram[depthVal]++;
				points++;
			}
		}

		for (int i = 1; i < histogram.length; i++){ //istogramma cumulativo
			histogram[i] += histogram[i-1];
		}

		if (points > 0) {
			for (int i = 1; i < histogram.length; i++) { 
				histogram[i] = 1.0f - (histogram[i] / (float)points);
			}
		}
	}

	private void addKeyListener() {
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
					manager.stopManager();
				}
			}
		});

	}

	private void addWindowListener() {
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}


}
