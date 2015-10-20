package countdowntimer.sound;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import countdowntimer.preferences.Preferences;
import countdowntimer.preferences.UserLocalFileAccess;

public class SoundPlayer {
	
	private static Executor executor = Executors.newFixedThreadPool(2);
	private static AtomicInteger count = new AtomicInteger(0);

	public static void playSound(URL wavfile,int delay,boolean canDitch) {
		playSound(wavfile, 1, delay, canDitch, new DefaultSoundCanceller());
	}
	
	public static void playSound(URL wavfile,int delay,boolean canDitch,SoundCanceller canceller) {
		playSound(wavfile, 1, delay, canDitch, canceller);
	}
	
	public static void decreaseSoundCount() {
		count.decrementAndGet();
	}
	
	public static void playCustomAlarmSound() {
		playCustomAlarmSound(new DefaultSoundCanceller());
	}
	
	public static void playCustomAlarmSound(SoundCanceller canceller) {
		String fn = Preferences.getInstance().getStoredSoundFileName();
		URL url = UserLocalFileAccess.getUrlForFile(fn);
		playSound(url,1,0,false,canceller);
	}
	
	public static void playSound(URL wavfile, int repeat, int delay, boolean canDitch) {
		playSound(wavfile, repeat, delay, canDitch, new DefaultSoundCanceller());
	}
	
	public static void playSound(URL wavfile, int repeat, int delay, boolean canDitch, SoundCanceller canceller) {

		if (Preferences.getInstance().isAlarmSoundMuted()) return;
		if (count.get() <= 2 || !canDitch) {
			count.incrementAndGet();
			System.out.println("Sound "+wavfile+" .. "+canDitch); //$NON-NLS-1$ //$NON-NLS-2$
			if (wavfile.getPath().toLowerCase().endsWith("mp3")) {
				executor.execute(new PlaySoundsJLRunnable(wavfile,repeat,delay,canceller));
			} else {
				executor.execute(new PlaySoundRunnable(wavfile,repeat,delay,canceller));
			}
		} else {
			System.out.println("DITCHED"); //$NON-NLS-1$
		}
	}
	
	private static class DefaultSoundCanceller implements SoundCanceller {

		@Override
		public boolean isCancelled() { return false; }
		
	}

}
