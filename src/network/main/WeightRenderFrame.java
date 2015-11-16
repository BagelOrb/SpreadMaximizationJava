package network.main;

import io.Images;
import io.Images.InputSample;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import util.basics.DoubleArray3D;
import layer.CnnDoubleLayer;
import mathViz.Render2D;
import mathViz.RenderPanel;
import network.ForThreader;
import network.Network;
import network.analysis.Debug;

@SuppressWarnings("serial")
public class WeightRenderFrame extends JFrame  {

	public static WeightRenderFrame frame = null;
	
	public Network network;
//	public WeightRenderPanel weightRenderPanel;

	public JPanel panel;
	public List<ImagePanel> imgPanels;
	
	
	static int imgW = 1000;
	static int imgH = 500;
	
	public static void run(final Network network) {
		try {
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
	            createAndShowGUI(network);
	        }
	    });
	}
	

	
	public WeightRenderFrame(String name) {
		super(name);
	}

	
	private static void createAndShowGUI(Network network2) {
        //Create and set up the window.
		frame = new WeightRenderFrame("WeightRenderFrame");
		frame.network = network2;
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        
//        frame.setLayout(new GridLayout(4, 5));
        frame.setLayout(new BorderLayout());
        //        frame.add(new JLabel("<html>Press [Esc] to round off the experiment...\r\n <br>"
//        						+"Press [O] for intermediate output...</html>" ), BorderLayout.NORTH);
        
        //Set up the content pane.
        //frame.addComponentsToPane();
         
        
//        frame.weightRenderPanel = new WeightRenderPanel(frame);
//        frame.add(frame.weightRenderPanel, BorderLayout.EAST);
        
        
        
        frame.addImagePanels();
        
        frame.setPreferredSize(new Dimension(
        		(int) (frame.panel.getPreferredSize().width  * 1.1), 
        		(int) (frame.panel.getPreferredSize().height * 1.1) + 20));
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    }

	private void addImagePanels() {
		
		
		CnnDoubleLayer layer = frame.network.getLastLayer();
		int nf = layer.params.nFeatures;
		int nRows = (int) Math.sqrt(nf);
		int nCols = (int) Math.ceil(((double) nf)/nRows);
		
		panel = new JPanel(new GridLayout(nRows, nCols));
		
		imgPanels = new ArrayList<ImagePanel>(nf);
		
//		for (int c = 0; c< nCols; c++)
//			for (int r = 0; r< nRows; r++)
//		
//		this.setLayout(new GridLayout(nRows, nCols));
		for (int f =0; f<nf; f++)
		{
			DoubleArray3D w = layer.weights[f];
			BufferedImage image;
			if (w.depth == 1)
				image = getImg(w);
			else
				image = getImgRGB(w);
			ImagePanel imgPanel = new ImagePanel(image);
			imgPanels.add(imgPanel);
			panel.add(imgPanel);
//			panel.add(new JButton("Button 1"));
			
		}
		frame.add(panel);
		
		
	}
	public void resetImages() 
	{
		CnnDoubleLayer layer = frame.network.getLastLayer();

		for (int f = 0 ; f<layer.params.nFeatures; f++)
		{
			DoubleArray3D w = layer.weights[f];
			ImagePanel imgPanel = imgPanels.get(f);
			if (w.depth == 1)
				imgPanel.image = getImg(w);
			else
				imgPanel.image = getImgRGB(w);
			
//			imgPanel.repaint();
		}
		
//		repaint();
	}

//	private static final int scaling = 8;
	
	private int getScaling() {
		return 80/frame.network.getLastLayer().params.widthConvolutionField;
	}
	
	private BufferedImage getImgRGB(DoubleArray3D w) {
		InputSample img = new InputSample(w.clone().data);
		img.rescaleToFitRetainZeroTotal();
		BufferedImage imgClr = Images.getScaledImage(getScaling(), img.toBufferedImageRBG());
		return imgClr;
	}



	private BufferedImage getImg(DoubleArray3D w) {
		InputSample img = new InputSample(w.clone().data);
		img.rescaleToFitRetainZeroTotal();
		BufferedImage[] imgs = img.toBufferedImages();
		BufferedImage rendImg = Images.getScaledImage(getScaling(), imgs[0]);
		return rendImg;
	}

}
