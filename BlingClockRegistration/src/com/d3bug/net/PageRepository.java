package com.d3bug.net;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PageRepository {
	
	private Timer timer = new Timer("PageReaderTimer",true);
	private Executor executor = Executors.newSingleThreadExecutor();
	private Map<String,String> urls = new HashMap<String,String>();
	private Map<String,String> pageCache = new HashMap<String,String>();
	private Object updateLock = new Object();
	
	private static PageRepository INSTANCE = new PageRepository();
	public static PageRepository getInstance() { return INSTANCE; }
	
	public PageRepository() { 
		timer.schedule(new TimerTask() { public void run() {
			refreshAllPagesInCache();
		}},60 * 60 * 1000,60 * 60 * 1000);  
	}
	
	protected void refreshAllPagesInCache() {
		synchronized (updateLock) {
			for (Map.Entry<String,String> entry : urls.entrySet()) {
				readPageAndStoreInCache(entry.getKey(),entry.getValue());
			}
		}
	}

	public void registerPage(String site,String page,String url) {
		String key = site + "." + page;
		urls.put(key,url);
		readPageAndStoreInCache(key,url);
	}
	
	private void readPageAndStoreInCache(final String key, final String url) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					//System.out.println("PageRepository: Reading "+key+" --> "+url);
					String page = PageReader.readPage(url);
					pageCache.put(key,page);
					System.out.println(new Date() + " PageRepository: Read "+key+", page was "+page.length()+" chars.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		executor.execute(r);
	}

	public String getPage(String site,String page) {
		String key = site + "." + page;
		return pageCache.get(key);
	}

}
