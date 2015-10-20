package blingclock.visualiser;

import java.util.ArrayList;
import java.util.List;

public class SegmentUtils {
	
	private static List<Integer> possibleSegmentUnits = new ArrayList<Integer>();
	
	static {
		buildPossibleSegmentUnits();	
	}
	
	private static void buildPossibleSegmentUnits() {
		final int[] mins = { 1, 2, 3, 5, 10, 15, 20, 30, 60, 120 };
		for (int x : mins) {
			possibleSegmentUnits.add(x * 60);
		}
		final int[] hours = { 1, 2, 4, 6, 12, 24, 48 };
		for (int x : hours) {
			possibleSegmentUnits.add(x * 60 * 60);
		}
		final int[] days = { 1, 2, 7, 30, 365/2, 365, (int)(365.25*2), (int)(365.25*5), (int)(365.25*10), (int)(365.25*50), (int)(365.25*100) };
		for (int x : days) {
			possibleSegmentUnits.add(x*24*60*60);
		}
	}
	
	public static int getSegmentCount(long targetSeconds) {
		if (targetSeconds <= 60) {
			return (int)(targetSeconds / 10);
		} else {
			for (int u : possibleSegmentUnits) {
				long segments = targetSeconds / u;
				if (segments <= 15) return (int)segments;
			}
		}
		return 1;
	}

}
