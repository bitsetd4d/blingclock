package blingclock.util;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.Method;

public class GraphicsUtil {
	
	private static RenderingHints renderHints = setupRenderHints();
	private static RenderingHints textRenderHints = setupTextRenderHints();
	
	public static BufferedImage createCompatibleImage(int width,int height) {
		return getGraphicsConfiguration().createCompatibleImage(width, height);
	}
	
	private static RenderingHints setupRenderHints() {
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
		return rh;
	}
	
	private static RenderingHints setupTextRenderHints() {
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON); 
		return rh;
	}

	public static BufferedImage createTranslucentCompatibleImage(int width,int height) {
		return getGraphicsConfiguration().createCompatibleImage(width, height,Transparency.TRANSLUCENT);
	}
	
    private static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().
                    getDefaultScreenDevice().getDefaultConfiguration();
    }
    
    public static Graphics2D prepareGraphics(Graphics g) {
    	Graphics2D g2d = (Graphics2D)g;
    	g2d.setRenderingHints(renderHints);
    	return g2d;
    }
    
    public static void setRenderHints(Graphics2D g) {
    	g.setRenderingHints(renderHints);
    }
    
    public static void setRenderHintsForTextOnly(Graphics2D g) {
    	g.setRenderingHints(textRenderHints);
    }
    
    public static void setPixels(BufferedImage img, int x, int y, int w, int h,
			int[] pixels) {
		if (pixels == null || w == 0 || h == 0) {
			return;
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length" + " >= w*h"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			WritableRaster raster = img.getRaster();
			raster.setDataElements(x, y, w, h, pixels);
		} else {
			// Unmanages the image
			img.setRGB(x, y, w, h, pixels, 0, w);
		}
	}
    
    public static int[] getPixels(BufferedImage img, int x, int y, int w, int h, int[] pixels) {
		if (w == 0 || h == 0) {
			return new int[0];
		}

		if (pixels == null) {
			pixels = new int[w * h];
		} else if (pixels.length < w * h) {
			throw new IllegalArgumentException(
					"pixels array must have a length" + " >= w*h"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		int imageType = img.getType();
		if (imageType == BufferedImage.TYPE_INT_ARGB
				|| imageType == BufferedImage.TYPE_INT_RGB) {
			Raster raster = img.getRaster();
			return (int[]) raster.getDataElements(x, y, w, h, pixels);
		}

		// Unmanages the image
		return img.getRGB(x, y, w, h, pixels, 0, w);
	}
    
}
