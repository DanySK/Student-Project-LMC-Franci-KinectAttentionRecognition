package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import control.KinectManager;

/** GUI to visualize the resulting attention level 
 * 
 * @author Silvia Franci
 *
 */
public class AttentionIndexFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private KinectManager manager;
	private JCheckBox cblist[];
	private JLabel l[];
	private JTextField[] attentionIndex;
	
	/**
	 * @Constructor
	 * @param manager KinectManager
	 */
	public AttentionIndexFrame(KinectManager manager){
		setTitle("AttentionIndex");
		this.manager=manager;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addKeyListener();
		
		cblist = new JCheckBox[manager.MAX_USER];
		l = new JLabel[manager.MAX_USER];
		attentionIndex = new JTextField[manager.MAX_USER];
				
		JPanel p = new JPanel();
		addComponent(p);
		
		this.add(p);
		this.setSize(600, 200);
		this.setResizable(false);
		this.setLocation(600, 500);
	}

	private void addComponent(JPanel p) {
				
		p.setLayout(new GridLayout(manager.MAX_USER/2, 6));
		
		for(int i=0;i<manager.MAX_USER;i++){
			cblist[i] = new JCheckBox();
			cblist[i].setIcon(new ImageIcon("icon/red.png"));
			p.add(cblist[i]);
			
			l[i] = new JLabel("User "+(i+1));
			l[i].setFont(new Font(l[i].getFont().getName(),Font.BOLD,l[i].getFont().getSize()+9));
			l[i].setForeground(Color.BLUE);
			p.add(l[i]);
			
			attentionIndex[i] = new JTextField(10);
			attentionIndex[i].setEditable(false);
			attentionIndex[i].setFont(new Font(attentionIndex[i].getFont().getName(),Font.BOLD,attentionIndex[i].getFont().getSize()+9));
			p.add(attentionIndex[i]);
		}
		
	}
	
	/**
	 * Update the attention level of the relative user, set the icon green
	 * @param user User's ID - 1
	 * @param index Total attention level
	 */
	public void setAttentionIndex(int user, float index){
		cblist[user].setIcon(new ImageIcon("icon/green.png"));
		attentionIndex[user].setText(""+index);
		
	}
	
	/**
	 * Reset the attention level of the relative user, set the icon red
	 * @param user User's ID - 1
	 */
	public void lostUser(int user){
		cblist[user].setIcon(new ImageIcon("icon/red.png"));
		attentionIndex[user].setText("");
	}
	
	private void addKeyListener() {
		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); //$NON-NLS-1$
				getRootPane().getActionMap().put("Cancel", new AbstractAction(){ //$NON-NLS-1$
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e){
//	            	System.out.println("premuto esc");
	            	manager.stopManager();
					}
				}
		);

	}
	
}
