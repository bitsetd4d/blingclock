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


public class DigitGrabMain extends JFrame {

	private static final int W = 256;
	private static final int H = 512;
	private LEDDigit digit = new LEDDigit();
	
	public static void main(String[] args) throws IOException {
		new DigitGrabMain().open();
	}

	private void open() throws IOException {
		setBackground(new Color(0,0,0));

		//Styling.applyTheme(TweakValue.COLOR_THEME_GREEN,2f);
		add(digit);
		digit.setCharacter("8");
		setSize(W,H);
		setVisible(true);
		
		for (int i=0; i<=9; i++) {
			String c = String.valueOf(i);
			digit.setCharacter(c);
			setSize(W,H);
			RenderedImage image = createImageFromDigit();
			File f = new File("digit-grab-out/digit-"+c+".png");
			ImageIO.write(image,"png",f);
		}
	}
	
	// Returns a generated image.
	public RenderedImage createImageFromDigit() {
	    BufferedImage bufferedImage = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g2d = bufferedImage.createGraphics();
	    digit.setVisible(false);
	    digit.paint(g2d);
	    g2d.dispose();
	    digit.setVisible(false);
	    return bufferedImage;
	}
}
