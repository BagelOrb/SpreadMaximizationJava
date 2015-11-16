package network.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import mathViz.Render2D;
import mathViz.RenderPanel;
import network.ForThreader;
import network.Network;
import network.analysis.Debug;

@SuppressWarnings("serial")
public class JFrameExperiment extends JFrame implements KeyListener {

	public static boolean keepRunning = true;
	public static boolean orthonormalize = false;
	public static boolean intermediateOutput = false;
	public static boolean isPauzed = false; 
	public static JFrameExperiment frame;
	
//	public static WeightRenderFrame weightRenderFrame = null;
	
	public RenderPanel renderPanel;
	public Network network;
//	public WeightRenderPanel weightRenderPanel;
	
	
	static int imgW = 1000;
	static int imgH = 500;
	private static JLabel pauzeJLabel;
	
	public static void run() {			try {
        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    } catch (Exception ex) {
        ex.printStackTrace();}
    /* Turn off metal's use of bold fonts */
    UIManager.put("swing.boldMetal", Boolean.FALSE);
     
    //Schedule a job for event dispatch thread:
    //creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
        @Override
		public void run() {
            createAndShowGUI();
        }
    });
	}
	

	
	public JFrameExperiment(String name) {
		super(name);
		this.addKeyListener(this);
	}
	@Override
	public void keyPressed(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) { 
			keepRunning = false;
            Debug.out("Quitting experiment...");
		}
		if (ke.getKeyChar() == 'o') { 
			orthonormalize = true;
            Debug.out("Orthonormalizing weight vectors...");
		}
		if (ke.getKeyChar() == 'p')
		{
			setPauzed(!isPauzed);
			
		}
		if (ke.getKeyChar() == 'i')
		{
			intermediateOutput = true;
			Debug.out("Generating intermediate output...");
			
		}
	}

	private void setPauzed(boolean b) {
		isPauzed = b;
		pauzeJLabel.setText("Pauzed: "+isPauzed);
	}



	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private static void createAndShowGUI() {
        //Create and set up the window.
		frame = new JFrameExperiment("Experiment");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setLayout(new BorderLayout());
        pauzeJLabel = new JLabel("Pauzed: "+isPauzed);
        frame.add(pauzeJLabel, BorderLayout.NORTH);
//        frame.add(new JLabel("<html>Press [Esc] to round off the experiment...\r\n <br>"
//        						+"Press [O] for intermediate output...</html>" ), BorderLayout.NORTH);
        
        frame.renderPanel = new RenderPanel(imgW, imgH);
        frame.add(frame.renderPanel);
        //Set up the content pane.
        //frame.addComponentsToPane();
         
        
//        frame.weightRenderPanel = new WeightRenderPanel(frame);
//        frame.add(frame.weightRenderPanel, BorderLayout.EAST);
        
        
        
        frame.setPreferredSize(new Dimension(imgW+10, imgH+70));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	ForThreader.threadPool.shutdown();;
            }
        });
    }
//	public static void main(String[] args) {
//		QuitExperiment.run();
//		while (true) {
//            Debug.out();
//			if (!keepRunning) {
//                Debug.out("stopped!");
//				break;
//			}
//		}
//		frame.dispose();
//	}



	public void graphValues(LinkedList<double[]> valuesOverTime) {
		Render2D rendered = Render2D.graphValues(valuesOverTime, imgW, imgH); 
		renderPanel.setImage(rendered.toBufferedImage());
	}
}
