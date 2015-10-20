package countdowntimer.mini;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageLoader {

	public static Icon loadImage(String name) {
		Class cls = ImageLoader.class;
		URL url = cls.getResource("/images/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				url = new URL("file:images" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		return new ImageIcon(url);
	}
	
}
