package blingclock.layout;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MetricsSharing {
	
	private static MetricsSharing instance = new MetricsSharing();
	public static MetricsSharing getInstance() { return instance; }
	
	private List<MetricsListener> listeners = new CopyOnWriteArrayList<MetricsListener>();
	private Map<String,Double> metrics = new ConcurrentHashMap<String, Double>(); 
	
	public void addListener(MetricsListener listener) {
		listeners.add(listener);
	}
	public void removeListener(MetricsListener listener) {
		listeners.remove(listener);
	}
	
	public void setMetric(String key,double value) {
		//System.out.println("put "+key+" = "+value);
		Double old = metrics.put(key,value);
		if (old == null || old != value) {
			for (MetricsListener l : listeners) {
				l.onMetricUpdated(key, value);
			}
		}
	}
	public double getMetric(String key) {
		Double v = metrics.get(key);
		if (v == null) return 0;
		return v;
	}

}
