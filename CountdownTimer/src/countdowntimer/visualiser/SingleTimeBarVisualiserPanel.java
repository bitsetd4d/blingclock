package countdowntimer.visualiser;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.GlyphVector;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import blingclock.digits.DigitStyling;
import blingclock.layout.MetricsListener;
import blingclock.layout.MetricsSharing;
import blingclock.util.GlobalImageCache;
import blingclock.util.GraphicsUtil;
import blingclock.visualiser.SegmentUtils;
import blingclock.visualiser.VisualiserPanel;

import countdowntimer.SecondsConverter;
import countdowntimer.Styling;
import countdowntimer.preferences.TimerPreference;

public class SingleTimeBarVisualiserPanel extends VisualiserPanel implements MetricsListener {
	
	private DecimalFormat df = new DecimalFormat("0.#"); //$NON-NLS-1$
	
	private static final long DAY_TENTHS = 24 * 60 * 60 * 10;
	private static final long DAY_HUNDREDTHS = DAY_TENTHS * 10;
	private boolean flash;
	private long countdownTenths;
	private long countdownHundreths;
	private long targetSeconds;
	private long segments = 1;
	
	private double digitHeight;
	
	private double redRatio;
	private double amberRatio;
	
	private int mouseX = -1;
	private boolean redBarNearestMouse;
	private TimerPreference timerPref;
	
	private static boolean trackMouseAndDrawBar = true;
	
	public SingleTimeBarVisualiserPanel(TimerPreference timerPref) {
		this.timerPref = timerPref;
		redRatio = timerPref.getRedRatio();
		amberRatio = timerPref.getAmberRatio();
		digitHeight = MetricsSharing.getInstance().getMetric("digits-h"); //$NON-NLS-1$
		MetricsSharing.getInstance().addListener(this);

		addMouseListener(new MouseAdapter() { 
			  public void mouseClicked(MouseEvent e) {
				  if (!isEnabled() || !trackMouseAndDrawBar) return;
				  onMouseClicked(e.getX(),e.getY());
			  }
			  @Override
			public void mouseExited(MouseEvent e) {
				  if (!isEnabled() || !trackMouseAndDrawBar) return;
				  mouseX = -1;
				  repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (!isEnabled() || !trackMouseAndDrawBar) return;
				onMouseMoved(e.getX(),e.getY());
			}
		});
	
	}
	
	/* Stop prefs being repainted over by bar */
	public static void globalDisableMouseMonitoring() {
		trackMouseAndDrawBar = false;	
	}

	public static void globalEnableMouseMonitoring() {
		trackMouseAndDrawBar = true;
	}

	protected void onMouseMoved(int x, int y) {
		mouseX = x;
		redBarNearestMouse = isRedNearestMouse();
		repaint();
	}

	protected void onMouseClicked(int x, int y) {		
		if (mouseX >= 0) {
			double ratio = (double)mouseX / getWidth();
			ratio = ((int)(ratio * 1000)) / 1000.0;
			if (isRedNearestMouse()) {
				redRatio = ratio;
				timerPref.setRedRatio(ratio);
			} else {
				amberRatio = ratio;
				timerPref.setAmberRatio(ratio);
			}
		}
		repaint();
	}
	
	private boolean isRedNearestMouse() {
		double ratio = (double)mouseX / getWidth();
		if (ratio < redRatio) {
			return true;
		} else if (ratio > amberRatio) {
			return false; 
		} else if (ratio - redRatio > amberRatio - ratio) {
			return false;
		} else {
			return true;
		}
	}

	public void onMetricUpdated(String key, double v) {
		if (key.equals("digits-h")) { //$NON-NLS-1$
			digitHeight = v;
			repaint();
		}
	}

	public void flash(boolean onOrOff) {
		this.flash = onOrOff;
		repaint();
	}

	public void reset() {}

	public void setCountdownTimeRemaining(int days,long countDownTenths,long hundredths) {
		this.countdownTenths = days * DAY_TENTHS + countDownTenths;
		this.countdownHundreths = days * DAY_HUNDREDTHS + hundredths;
		repaint();
	}

	public void setTargetSeconds(long targetSeconds) {
		this.targetSeconds = targetSeconds;
		this.segments = SegmentUtils.getSegmentCount(targetSeconds);
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = GraphicsUtil.prepareGraphics(g);
		double yoffset = (getHeight() - digitHeight) / 2; 

		g2d.setColor(Color.BLACK);
		g2d.fillRect(0,0,getWidth(),getHeight());
		g2d.setColor(DigitStyling.BAR_GRAYED);
		g2d.fill(new Rectangle2D.Double(0, (int)yoffset, getWidth(), digitHeight));
		
		double ratio = countdownHundreths / ((double)targetSeconds * 100);
		int phase = 0;
		if (flash) {
			phase = 0; 
		} else {
			if (ratio > amberRatio) {
				phase = 2;
			} else if (ratio > redRatio) {
				phase = 1;
			} else {
				phase = 0;
			}
		}
		
		if (!trackMouseAndDrawBar) return;
		
		BufferedImage image = getBarImage(phase);
		double b = getWidth() * (flash ? 1 : ratio);
		
		Shape oldClip = g2d.getClip();
		g2d.setClip(0,(int)yoffset,(int)b,(int)digitHeight);
		g2d.drawImage(image,0,(int)yoffset,getWidth(),(int)digitHeight,null);
		
		if (mouseX >= 0) {
			
			g2d.setClip(oldClip);
			int y1 = (int)(yoffset + 2);
			int y2 = (int)(yoffset + digitHeight - 4);

			// Red, Amber
			double redx = getWidth() * redRatio;
			double amberx = getWidth() * amberRatio;
			
			Stroke oldStroke = g2d.getStroke();
			
		    float[] dashPattern = { 10, 5, 5, 5 };
		    int fontHeight = (int)Math.max(digitHeight/12,12);
			Font f = new Font(Styling.FONT,Font.PLAIN,fontHeight);
		    
			if (redBarNearestMouse) {
			    g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        dashPattern, 0));
			    g2d.setColor(Color.WHITE);
			    g2d.drawLine((int)redx,y1,(int)redx,y2);
			}
		    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
		                                  BasicStroke.JOIN_MITER, 10,
		                                  dashPattern, 0));
			g2d.setColor(Color.RED);
			g2d.drawLine((int)redx,y1,(int)redx,y2);
			GlyphVector[] redGvs = createGVsForTime(g2d, f, redx);
			drawText(g2d, redGvs[0], redGvs[1], (float)redx, y1, false);

			if (!redBarNearestMouse) {
			    g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER, 10,
                        dashPattern, 0));
			    g2d.setColor(Color.WHITE);
			    g2d.drawLine((int)amberx,y1,(int)amberx,y2);
			}
		    g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER, 10,
                    dashPattern, 0));
			g2d.setColor(Color.YELLOW);
			g2d.drawLine((int)amberx,y1,(int)amberx,y2);
			GlyphVector[] amberGvs = createGVsForTime(g2d, f, amberx);
			drawText(g2d, amberGvs[0], amberGvs[1], (float)amberx, y1, false);
			
			g2d.setStroke(oldStroke);
			
			// Mouse
			g2d.setColor(DigitStyling.BAR_HIGHLIGHT);
			g2d.drawLine(mouseX, y1 + 10, mouseX, (y2 - 10));

			Polygon t1 = new Polygon();
			t1.addPoint(mouseX-5,y1);
			t1.addPoint(mouseX+5,y1);
			t1.addPoint(mouseX,y1+10);
			g2d.draw(t1);
			
			Polygon t2 = new Polygon();
			t2.addPoint(mouseX-5,y2);
			t2.addPoint(mouseX+5,y2);
			t2.addPoint(mouseX,(y2 - 10));
			g2d.draw(t2);
			
			g2d.setColor(Color.WHITE);
			
			GlyphVector[] gvs = createGVsForTime(g2d,f,mouseX);			
			drawText(g2d,gvs[0],gvs[1],mouseX,y2,true);
		}
	}
	
	private GlyphVector[] createGVsForTime(Graphics2D g2d,Font f,double x) {
		int impliedSeconds = (int)((x / getWidth()) * targetSeconds);
		double percent = ((x / getWidth()) * 100);
		String hhmm = SecondsConverter.secondsToHHMM(impliedSeconds);
		GlyphVector gv1 = f.createGlyphVector(g2d.getFontRenderContext(),hhmm);
		GlyphVector gv2 = f.createGlyphVector(g2d.getFontRenderContext(),df.format(percent)+"%"); //$NON-NLS-1$
		GlyphVector[] gv = new GlyphVector[2];
		gv[0] = gv1;
		gv[1] = gv2;
		return gv;
		
	}
	
	private void drawText(Graphics2D g2d,GlyphVector v1,GlyphVector v2,float x,float y,boolean goUp) {		
		Rectangle2D vb1 = v1.getVisualBounds();
		double th = vb1.getHeight();
		double tw = vb1.getWidth();
		float tx = x + 15;
		if (getWidth() - tw*2 < x) {
			tx = (float) (x - tw - 5);
		}
		if (goUp) {
			g2d.drawGlyphVector(v1, tx, y - 5);
			g2d.drawGlyphVector(v2, tx, (float) (y - 7 - th));
		} else {			
			g2d.drawGlyphVector(v2, tx, (float) (y + th));
			g2d.drawGlyphVector(v1, tx, (float) (y + th + th + 5));
		}
	}
	

	private BufferedImage getBarImage(int phase) {
		int w = getWidth();
		int h = (int)digitHeight;
		if (h < 1) h = 1;
		String key = "sb-horiz-"+segments+"-"+phase; //$NON-NLS-1$ //$NON-NLS-2$
		BufferedImage image = GlobalImageCache.getInstance().getImage(key, w, h);
		if (image == null) {
			image = GraphicsUtil.createTranslucentCompatibleImage(w,h);
			Graphics2D g2d = image.createGraphics();
			GraphicsUtil.setRenderHints(g2d);
			GradientPaint painter;
			
			if (phase == 2) {
				painter = new GradientPaint(0f, 00f,
						(showRegistered ? Styling.GREY1 : DigitStyling.BAR_LED2),
						0.0f, (float)(2.0 * h),
						(showRegistered ? new Color(100,100,100) : new Color(255,0,0)));
			} else if (phase == 1) {
				painter = new GradientPaint(0f, 00f,
						(showRegistered ? Styling.GREY2 : Color.YELLOW),
						0.0f, (float)(1.5 * h),
						(showRegistered ? new Color(100,100,100) : new Color(0,255,0)));			
			} else {
				painter = new GradientPaint(0f, 00f,
						(showRegistered ? Styling.GREY3 : Color.RED),
			            0.0f, (float)(1.5 * h),
			            (showRegistered ? new Color(100,100,100) : new Color(255,255,0)));
			}
				
			g2d.setPaint(painter);
			g2d.fill(new Rectangle2D.Double(0, 0, w, h));
			g2d.setPaint(new Color(0f,0f,0f,0.3f));
			for (int i=0; i<segments; i++) {
				double x1 = w * (i / (double)segments);
				double x2 = w * (i / (double)segments);
				g2d.draw(new Line2D.Double(x1,0,x2,digitHeight));
			}
			GlobalImageCache.getInstance().storeImage(key, w, h,image);
			g2d.dispose();
		}
		return image;
	}

}
