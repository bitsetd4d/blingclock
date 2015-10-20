package countdowntimer.anim;

public class Interpolator {

	private double accelT;
	private double deccelT;

	private static final int STEPS = 100;
	private static final double dt = 1.0/STEPS;
	private float[] distance = new float[STEPS];
	
	public Interpolator(double accel, double decel) {
		this.accelT = accel;
		this.deccelT = decel;
		calcDistance();
	}
	
	public double getFraction(double t) {
		int i = (int)(t * STEPS);
		float d1 = distance[i];
		if (i == STEPS - 1) return d1;
		float d2 = distance[i+1];
		double dv = 0;
		double t1 = i * dt;
		double t2 = (i+1) * dt;
		if (t1 < t2) { 
			double r = (t2 - t) /  (t2 - t1);
			dv = (d2 - d1) * r;
		}
		return d1 + dv;
	}
	
	private void calcDistance() {
		float d = 0;
		float speed = 3;
		for (int i=0; i<STEPS; i++) {
			if (dt < accelT) {
				d += speed;
				speed += 0.2;
			} else if (dt < deccelT) {
				d += speed;
			} else {
				d += speed;
				speed -= 0.3;
				if (speed < 0.2) speed = 0.2f;
			}
			distance[i] = d;
		}
		float totalDistance = distance[STEPS-1];
		for (int i=0; i<STEPS; i++) {
			distance[i] = distance[i] / totalDistance;
		}	
	}
	
	public static void main(String[] args) {
		Interpolator it  = new Interpolator(0.3f,0.8f);
		for (double x=0; x<1; x+= 0.05) {
			System.out.println("x="+x+"\t\t"+it.getFraction(x)); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
}
