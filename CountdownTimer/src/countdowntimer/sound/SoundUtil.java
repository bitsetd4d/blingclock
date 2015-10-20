package countdowntimer.sound;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import countdowntimer.preferences.Preferences;

public class SoundUtil {
	
	public static void playAudioClip(final String name,boolean canDitch) {
		if (!Preferences.getInstance().isAlarmSoundMuted()) {
			URL url = getSoundUrl(name);
			SoundPlayer.playSound(url,0,canDitch);
		}
	}
	
	private static URL getSoundUrl(String name) {
		Class cls = SoundUtil.class;
		URL url = cls.getResource("/sounds/" + name); //$NON-NLS-1$
		try {
			if (url == null) {
				return new URL("file:sounds" + File.separator + name); //$NON-NLS-1$
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;	
	}

}
