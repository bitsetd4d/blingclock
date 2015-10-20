package countdowntimer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import blingclock.digits.LEDDigit;
import blingclock.digits.LEDSeparator;


public class DigitGrabSeperatorMain extends JFrame {

	private static final int W = 256;
	private static final int H = 512;
	private LEDSeparator sep = new LEDSeparator(false);
	
	public static void main(String[] args) throws IOException {
		new DigitGrabSeperatorMain().open();
	}

	private void open() throws IOException {
		setBackground(new Color(0,0,0));

		//Styling.applyTheme(TweakValue.COLOR_THEME_GREEN,2f);
		add(sep);
		setSize(W,H);
		sep.setOn(true);
		setVisible(true);
		RenderedImage image = createImageFromDigit();
		File f = new File("digit-grab-out/seperator.png");
		ImageIO.write(image,"png",f);
	}
	
	// Returns a generated image.
	public RenderedImage createImageFromDigit() {
	    BufferedImage bufferedImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2d = bufferedImage.createGraphics();
	    sep.setVisible(true);
	    sep.paint(g2d);
	    g2d.dispose();
	    sep.setVisible(true);
	    return bufferedImage;
	}
}
