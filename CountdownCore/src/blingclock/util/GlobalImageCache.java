package blingclock.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

public class GlobalImageCache {
	
	private static GlobalImageCache INSTANCE = new GlobalImageCache();
	public static GlobalImageCache getInstance() { return INSTANCE; }
	
	private Map<String,CachedImage> cachedImages = new HashMap<String,CachedImage>();
	
	private static final int MAX_IMAGES = 30;
	private static final int REAP_TIME = 20 * 1000;
	
	private List<String> keys = new LinkedList<String>();
	private boolean reaperStarted;
	private Timer reaperTimer;
	
	private static boolean DEBUG = false;
	
	public BufferedImage getImage(String key,int w, int h) {
		String cacheKey = key + "." + w + "." + h; //$NON-NLS-1$ //$NON-NLS-2$
		CachedImage imageRecord = cachedImages.get(cacheKey);
		if (imageRecord == null) return null;
		imageRecord.recordUsed();
		//if (DEBUG) System.out.println("Get "+cacheKey+" --> "+ imageRecord.image);
		return imageRecord.image;
	}

	public void storeImage(String key,int w, int h,BufferedImage image) {
		String cacheKey = key + "." + w + "." + h; //$NON-NLS-1$ //$NON-NLS-2$
		cachedImages.put(cacheKey,new CachedImage(image));
		if (DEBUG) System.out.println("Store "+cacheKey); //$NON-NLS-1$
		keys.add(cacheKey);
		if (keys.size() > MAX_IMAGES) {
			String toRemoveKey = keys.remove(0);
			cachedImages.remove(toRemoveKey);
			if (DEBUG) System.out.println("MAX_IMAGES: Global Image Cached, Removed "+toRemoveKey); //$NON-NLS-1$
		}
		if (!reaperStarted) {
			startReaper();
		}
	}

	private void startReaper() {
		if (DEBUG) System.out.println("Start reaper"); //$NON-NLS-1$
		reaperTimer = new Timer(2000,new ActionListener() { public void actionPerformed(ActionEvent e) {
			reapUnusedImages();
		}});
		reaperTimer.start();
		reaperStarted = true;
	}

	public void clearCache() {
		cachedImages.clear();
		keys.clear();
		if (DEBUG) System.out.println("*** IMAGE CACHE CLEARED"); //$NON-NLS-1$
	}
	
	private void reapUnusedImages() {
		HashSet<String> deadKeys = new HashSet<String>();
		long now = System.currentTimeMillis();
		for (Map.Entry<String,CachedImage> et : cachedImages.entrySet()) {
			String key = et.getKey();
			CachedImage im = et.getValue();
			if (im.hasNotBeenReferencedRecently(now)) {
				deadKeys.add(key);
			}
		}
		for (String key : deadKeys) {
			cachedImages.remove(key);
		}
		keys.removeAll(deadKeys);
		if (DEBUG) {
			if (!deadKeys.isEmpty()) {
				System.out.println("Reaper removed "+deadKeys.size()+", cached now "+cachedImages.size()); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	private static class CachedImage {
		
		long timestamp;
		BufferedImage image;

		CachedImage(BufferedImage image) {
			this.image = image;
			recordUsed();
		}
		
		void recordUsed() {
			this.timestamp = System.currentTimeMillis();
		}
		
		boolean hasNotBeenReferencedRecently(long timeNow) {  
			return timeNow - timestamp > REAP_TIME;
		}
		
	}
	
}
