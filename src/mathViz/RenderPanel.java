package mathViz;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import network.analysis.Debug;

@SuppressWarnings("serial")
public class RenderPanel extends JPanel implements ComponentListener {

	
	public BufferedImage image;
	private int width;
	private int height;

	public RenderPanel(int imgW, int imgH) {
		width = imgW;
		height = imgH;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(10, 10, new Color(1f, 1f, 1f).getRGB());
		this.setPreferredSize(new Dimension(width, height));
		addComponentListener(this);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(((Image) image).getScaledInstance(width, height, Image.SCALE_DEFAULT), 1,1, null); // see javadoc for more info on the parameters    
        
    }
	
	public void setImage(BufferedImage newImg) {
		image = newImg;
		repaint();
	}

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(100,100);
    }
    
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
        Debug.out("renderImage resized");
//		image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
//		image.setRGB(10, 10, new Color(1f, 1f, 1f).getRGB());
//		image.setRGB(this.getWidth()/2, this.getHeight()/2, new Color(1f, 1f, 1f).getRGB());
		revalidate();
		this.repaint();
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}
