package network.main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class ImagePanel extends JPanel{

    public BufferedImage image;

    public ImagePanel(BufferedImage image) {
    	super();
    	this.image = image;
    	this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
       	g.drawImage(image, 0, 0, this); // see javadoc for more info on the parameters            
    }

}