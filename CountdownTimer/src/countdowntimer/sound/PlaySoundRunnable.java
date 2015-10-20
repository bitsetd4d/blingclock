package countdowntimer.sound;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class PlaySoundRunnable implements Runnable {
	
	private final static int EXTERNAL_BUFFER_SIZE = 5242880; // 1280Kb
	
	private URL url;
	private int repeat;
	private int delay;
	private SoundPosition curPosition;
	
	private AudioInputStream audioInputStream;
	private SourceDataLine auline;
	private SoundCanceller canceller;

	public PlaySoundRunnable(URL url,int repeat,int delay,SoundCanceller canceller) {
		this.url = url;
		this.repeat = repeat;
		this.delay = delay;
		this.canceller = canceller;
	}
	
	public void run() {
		SoundPlayer.decreaseSoundCount();
		System.out.println("Playing "+url+" -- repeat "+repeat+", delay "+delay); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		for (int i=0; i<repeat; i++) {
			if (!canceller.isCancelled()) {
				sleepThread();
				prepareAudioStream();
				if (audioInputStream == null) return;
				if (!prepareAULine()) return;
	
				if (auline.isControlSupported(FloatControl.Type.PAN)) {
					FloatControl pan = (FloatControl) auline.getControl(FloatControl.Type.PAN);
					if (curPosition == SoundPosition.RIGHT)
						pan.setValue(1.0f);
					else if (curPosition == SoundPosition.LEFT)
						pan.setValue(-1.0f);
				}
	
				playSoundNow();
			}
		}			
	}

	private void playSoundNow() {

		auline.start();
		int nBytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} finally {
			auline.drain();
			auline.close();
		}
	}

	private boolean prepareAULine() {
		AudioFormat format = audioInputStream.getFormat();
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
			return true;
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void prepareAudioStream() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(url);
		} catch (UnsupportedAudioFileException e1) {
			e1.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}

	private void sleepThread() {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e2) {}
	} 
	
}
