package il.ac.haifa.videopacity.player;

import il.ac.haifa.videopacity.media.ImageProducer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *	Movie Player that records 
 */
public class PressRecordingMoviePlayer extends JFrame implements KeyListener{
	
	private static final long serialVersionUID = 1L;
	//message that will appear in the window prior to the movie start
	private static final String EXPLANATION_MESSAGE = "Press Space to Start Movie";
	//prefix that will be at the beginning of each log file to destinguish beteen the diffrent logs
	public static final char LOG_PREFIX = '@';
	
	//the log file to print the keyPresses into
	private PrintWriter log;
	//timer to schedual the frame changes
	private Timer schedualer;
	//the panel to draw the images onto
	private MessageShowingPanel panel;
	//the image producer to draw it's frames on the panel 
	private ImageProducer imageProducer;
	//flag that indicates wather a space was pressed before
	private boolean isFirstSpaceType;
	//current frame counter to help log the presses of the user
	private int currentFrameNumber;
	
	/**
	 * Ctor
	 * 
	 * @param imageProducer - the image producer to display in the window
	 * @param log - the printer where to print the key presses
	 */
	public PressRecordingMoviePlayer(ImageProducer imageProducer, PrintWriter log){
		//init members
		this.currentFrameNumber=0;
		this.imageProducer=imageProducer;
		this.log=log;
		this.panel=new MessageShowingPanel();
		this.isFirstSpaceType=true;
		this.schedualer = new Timer();
		
		//init this window
		super.setContentPane(panel);
		panel.setPreferredSize(new Dimension(imageProducer.getWidth(),imageProducer.getHeight()));
		super.setResizable(false);
		super.pack();
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setVisible(true);
		
		//listen to key presses
		this.addKeyListener(this);
		//enable panel to show the starting message
		this.panel.setTextShown(true);
		//print title into log
	}
	
	@Override
	public void keyPressed(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}

	/**
	 * Key handler for key presses( after key has been released)
	 */
	@Override
	public void keyTyped(KeyEvent evt) {
		//check that the key that was pressed is the space key
		if(evt.getKeyChar()!=' '){
			return;
		}
		//if first space press then start the movie
		if(this.isFirstSpaceType){
			this.isFirstSpaceType=false;
			this.panel.setTextShown(false);
			this.schedualer.scheduleAtFixedRate(new FrameGrabber(), 0, (int)(1000/imageProducer.getFrameRate()));
			return;
		}
		//if not first keypress log this press
		log.println(LOG_PREFIX + "" + this.currentFrameNumber);
	}
	
	/**
	 *	Timer Task that draws the Images from a image producer
	 *  in regular intervals 
	 */
	private class FrameGrabber extends TimerTask{

		@Override
		public void run() {
			//if no more frames in the image producer cancel this task
			//and close the lgo file
			if(!imageProducer.hasNext()){
				cancel();
				log.close();
				return;
			}
			//draw next image on the panel
			currentFrameNumber++;
			BufferedImage frame=imageProducer.getNextImage().getAsBufferedImage();
			Graphics g=panel.getGraphics();
			g.drawImage(frame,0,0,null);
			g.dispose();
		}
		
	}
	
	/**
	 *	Panel that displays the explanation message 
	 */
	private class MessageShowingPanel extends JPanel{
		
		private static final long serialVersionUID = 1L;
		//flag to indicate whather to draw the explanation on the panel
		private boolean isTextShown;
		
		/**
		 * Ctor
		 */
		public MessageShowingPanel(){
			this.isTextShown = false;
		}
		
		/**
		 * paint method that is called everytime the window needs to be refreshed
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			//if flag is turned on, then draw the text message onto it
			if(this.isTextShown){
				g.setColor(Color.BLACK);
				g.setFont(new Font("Courier",Font.BOLD,getWidth()/20));
				g.drawString(EXPLANATION_MESSAGE, getWidth()/9, getHeight()/2);
			}
		}
		
		/**
		 * set if the explanation message should be shown or not
		 * 
		 * @param isShown
		 */
		public void setTextShown(boolean isShown){
			this.isTextShown = isShown;
			//ask for repaint to update the message
			this.repaint();
		}
		
		
	}

	
}
