package com.d3bug.saver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import com.d3bug.billing.Receipt;
import com.d3bug.email.Email;
import com.d3bug.registration.AuditTrail;
import com.d3bug.registration.LicenceCode;


public class Saver {

	private long lastAccessTime = System.currentTimeMillis();
	private Object mappedIdLock = new Object();
	private Map<String,SavedId> mappedIds = new HashMap<String,SavedId>();
	private static final int CUTOFF_MINS = 5;

	private String monthCode = getMonthCode();
	private int nextId = 0;
	
	private static String BASE_DIR = "receipts";
	
	private String baseDir;
	private File monthDir;
	
	private Timer housekeepTimer;
	
	private static Saver instance = new Saver();
	public static Saver getInstance() { return instance; }

	public Saver() { 
		String home = System.getProperty("user.home");
		String mydir = home + File.separator + BASE_DIR;
		File f = new File(mydir);
		baseDir = f.getPath();
		if (!f.exists()) {
			System.out.println("Making "+mydir);
			f.mkdir();
		}
		System.out.println("Saver base dir: "+baseDir);
		makeMonthDirectory();
		startHousekeepTimer();
	}
	
	public void persist(Email email) {
		Map<String,String> map = email.getFields();
		store(email.getInternalId(),"email",map);
	}
	
	public void persist(AuditTrail audit) {
		try {
			File f = getFile(audit.getInternalId(),"audit");
			FileWriter fw = new FileWriter(f);
			fw.append(audit.getBody());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void persist(String internalId, SaveCSV csv) {
		try {
			File f = getFile(internalId,"csv");
			FileWriter fw = new FileWriter(f);
			fw.append(csv.asCSV());
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void persist(LicenceCode code) {
		Map<String,String> map = code.getFields();
		store(code.getInternalId(),"code",map);
	}

	public void persist(Receipt receipt) {
		Map<String,String> map = receipt.getFields();
		store(receipt.getInternalId(),"receipt",map);
	}

	
	private void store(String id,String suffix,Map<String,String> map) {
		try {			
			String comment = "Updated "+new Date();
			File f = getFile(id,suffix);
			Properties p = new Properties();
			p.putAll(map);
			BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(f));
			p.store(fos, comment);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private File getFile(String id,String suffix) {
		String fullFileName = monthDir.getAbsolutePath() + File.separator + id + "." + suffix ;
		File f = new File(fullFileName);
		return f;
	}

	public String getNextInternalId(String paypalRef) {
		synchronized (mappedIdLock) {
			lastAccessTime = System.currentTimeMillis();
			checkChangeOfMonth();
			SavedId id = mappedIds.get(paypalRef);
			if (id != null) {
				id.timestamp = System.currentTimeMillis();
				return id.id;
			}
			id = new SavedId();
			id.id = calculateNextId();
			mappedIds.put(paypalRef, id);
			return id.id;
		}
	}
		
	private String calculateNextId() {
		if (nextId > 0) { 
			nextId++;
			return monthCode + nextId;
		}
		String[] allFiles = monthDir.list();
		// Id is base + nnnnnn
		int maxId = 12345;
		for (String f : allFiles) {
			int id = idFromFilename(f);
			maxId = Math.max(maxId, id);
		}
		nextId = maxId;
		System.out.println("SAVER: *** Calculated that last id was "+nextId);
		nextId++;
		return monthCode + nextId;
	}

	private int idFromFilename(String f) {
		if (f.startsWith(monthCode)) {
			int i = f.indexOf(".");
			if (i > 0) {
				String code = f.substring(3,i);
				try {
					return Integer.parseInt(code);
				} catch (NumberFormatException e) {}
			}
		}
		return 0;
	}
	
	public static void main(String[] args) {
		Saver s = new Saver();
		System.out.println(s.idFromFilename("11C333.csv"));
		System.out.println(s.idFromFilename("11B332.csv"));
		System.out.println(s.idFromFilename("11C33222.csv"));
	}

	private void makeMonthDirectory() {
		String monthPath = baseDir + File.separator + getMonthCode();
		monthDir = new File(monthPath);
		boolean made = monthDir.mkdirs();
		if (made) {
			System.out.println("Made dir "+monthPath);
		}
	}
	
	private void checkChangeOfMonth() {
		String now = getMonthCode();
		if (now.equals(monthCode)) return;
		System.out.println("Detected change of month");
		makeMonthDirectory();
	}
	
	private void checkExpiredMappings() {
		synchronized (mappedIdLock) {
			if (mappedIds.isEmpty()) return;
			List<String> toRemove = new ArrayList<String>();
			long cutoff = System.currentTimeMillis() - (CUTOFF_MINS * 60L * 1000);
			for (Map.Entry<String,SavedId> en : mappedIds.entrySet()) {
				if (en.getValue().timestamp < cutoff) {
					toRemove.add(en.getKey());
				}
			}
			if (toRemove.isEmpty()) return;
			System.out.println("Removing ids "+toRemove);
			for (String key : toRemove) {
				mappedIds.remove(key);
			}
		}
	}
	
	// eg. 10A = January 2010
	private static String getMonthCode() { 
		try {
			Date dt = new Date();
			int m = dt.getMonth(); // 0-11
			int y = dt.getYear(); // year - 1900
			char monthCode = (char)(m + 'A');
			return (y - 100)+String.valueOf(monthCode);	
		} catch (Exception e) {
			return "00";
		}
	}
	
	// --------------------------------------------------
	// Housekeeping
	// --------------------------------------------------	
	private void startHousekeepTimer() {
		housekeepTimer = new Timer("Housekeeping",true);
		TimerTask task = new TimerTask() {  public void run() {
			try {
				onHousekeepingTimer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}};
		housekeepTimer.schedule(task,60000,60000);
	}
	
	protected void onHousekeepingTimer() {
		if (lastAccessTime > System.currentTimeMillis() - (CUTOFF_MINS * 60L * 1000)) return;
		checkExpiredMappings();
		checkChangeOfMonth();
	}

	private static class SavedId {
		String id;
		long timestamp = System.currentTimeMillis();
	}


}
