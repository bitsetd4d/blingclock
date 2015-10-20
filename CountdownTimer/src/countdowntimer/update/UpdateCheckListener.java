package countdowntimer.update;

public interface UpdateCheckListener {
	
	void onCurrentVersion(String version);
	void onUnableToCheck();

}
