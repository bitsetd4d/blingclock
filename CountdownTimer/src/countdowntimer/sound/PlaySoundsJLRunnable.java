package countdowntimer.sound;

import java.net.URL;

import javazoom.jl.player.Player;

public class PlaySoundsJLRunnable implements Runnable {
	
	private URL url;
	private int repeat;
	private int delay;
	private SoundCanceller canceller;

	public PlaySoundsJLRunnable(URL url,int repeat,int delay,SoundCanceller canceller) {
		this.url = url;
		this.repeat = repeat;
		this.delay = delay;
		this.canceller = canceller;
	}
	
	public void run() {
		Player p = null;
		try {
			SoundPlayer.decreaseSoundCount();
			System.out.println("JL Playing "+url+" -- repeat "+repeat+", delay "+delay); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			for (int i=0; i<repeat; i++) {
				if (!canceller.isCancelled()) {
					p = new Player(url.openStream());
					sleepThread();
					p.play();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//if (p != null) p.close();
		}
	}

	private void sleepThread() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e2) {}
	} 

}
