package blingclock.visualiser;

public interface TimeVisualiser {

	public void setCountdownTimeRemaining(int days,long countDownTenths,long hundredths);  
	public void setTargetSeconds(long targetSeconds);

	public void reset();
	public void flash(boolean onOrOff);

}
