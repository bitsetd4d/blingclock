package countdowntimer;

import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

import countdowntimer.preferences.Preferences;

public class FullScreenUtil {
	
	private static boolean usedfullscreenApi;
	
	public static boolean isWindows() {
		return !isOSX();
	}
	
	public static boolean isOSX(){
		String lcOSName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		return lcOSName.startsWith("mac os x"); //$NON-NLS-1$
	}

	public static void fullScreenIfPossible(JFrame w) {
		boolean api = Preferences.getInstance().getUseFullScreenAPI();
		usedfullscreenApi = api;
		if (api) {
			fullScreenWithAPI(w);
		} else {
			fullScreenUsingMaximise(w);
		}
	}
	
	private static void fullScreenWithAPI(JFrame w) {
		Rectangle r = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		GraphicsDevice gd = w.getGraphicsConfiguration().getDevice();
		if (gs.length > 0) {
			GraphicsDevice device = gs[0];
			//GraphicsDevice device = gd;
			if (device.isFullScreenSupported()) {
				System.out.println("Full screen supported"); //$NON-NLS-1$
			} else {
				System.out.println("Full screen not supported"); //$NON-NLS-1$
			}
			device.setFullScreenWindow(w);
			w.setVisible(true);
			r = new Rectangle(0,0,device.getDisplayMode().getWidth(),device.getDisplayMode().getHeight());
		}
		if (r == null) {
			r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		}
		w.setBounds(r);
		//System.out.println(r);
		w.setExtendedState(Frame.MAXIMIZED_BOTH);
	}
	

	private static void fullScreenUsingMaximise(JFrame w) {
		w.setExtendedState(Frame.MAXIMIZED_BOTH);
		w.setAlwaysOnTop(true);
	}


	public static void unFullScreen() {
		if (usedfullscreenApi) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			if (gs.length > 0) {
				GraphicsDevice device = gs[0];
				if (device.isFullScreenSupported()) {
					System.out.println("Full screen supported"); //$NON-NLS-1$
				} else {
					System.out.println("Full screen not supported"); //$NON-NLS-1$
				}
				//w.setVisible(false);
				//w.setUndecorated(true);
				device.setFullScreenWindow(null);
			}
		} else {
			// Do nothing
		}
	}
	
	
}
