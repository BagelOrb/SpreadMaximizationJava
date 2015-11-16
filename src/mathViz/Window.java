package mathViz;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;


@SuppressWarnings("serial")
public class Window extends JFrame {

	static Window frame;
	private Component renderPanel;
	private JPanel mainPanel;
	
	
	public Window(String name) {
		super(name);
//		addComponentListener(this);
	}

	

	
	private void addComponentsToPane() {
		mainPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		//c.fill = GridBagConstraints.BOTH;
		
		renderPanel = new RenderPanel(1000, 500);
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER; // does nothing? already filled!
		c.gridheight = 1;
		mainPanel.add(renderPanel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.CENTER; // does nothing? already filled!
		c.gridheight = 1;
		mainPanel.add(new JButton("saf"), c);
//    	mainPanel = (JPanel) this.getContentPane();
//    			//new JPanel(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
//    	mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
//    	
////    	GridBagConstraints c = new GridBagConstraints();
////        //c.fill = GridBagConstraints.BOTH;
////        
//        renderPanel = new RenderPanel();
////        c.gridx = 1;
////        c.gridy = 0;
////        c.anchor = GridBagConstraints.CENTER; // does nothing? already filled!
////        c.gridheight = 1;
//        mainPanel.add(renderPanel);
	}

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
	

	private static void createAndShowGUI() {
        //Create and set up the window.
		frame = new Window("MathViz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Set up the content pane.
        frame.addComponentsToPane();
         
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

	public static void main(String[] args) {
		run();
	}


}