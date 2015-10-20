package blingclock.visualiser;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import blingclock.digits.DigitStyling;
import blingclock.util.GraphicsUtil;

public class TimeBarVisualiserPanel extends VisualiserPanel implements TimeSelectionListener {
		
	private long countDownSeconds = 60;
	private int days;
	private long targetSeconds = countDownSeconds;
	private int targetRows;
	private long fraction = 0;
	
	private int lightWidth;
	private int lightOffset;
	private int rowHeight;
	private int cw,ch;
	private static int ix = 2;
	private int topGap;
	private BufferedImage lightImage;
	
	private boolean flash = false;
	//private int mouseOverSecond = -1;
	
	private Font textFont;
	
	private int wholeHours;
	private int secondsBelowHourRemaining;
	
	private static final double DAY_GAP_SCALE = 0.3;
	private static final double HOUR_AREA_SCALE = 0.10;	
	
	private static final long SECONDS_PER_DAY = 24 * 60 * 60;
	private static final long SECONDS_PER_HOUR = 60 * 60;
	
	//private static boolean trackMouse = true;
	
	public TimeBarVisualiserPanel() {
//		addMouseListener(new MouseAdapter() { 
//			  public void mouseClicked(MouseEvent e) {
//				  if (!isEnabled() || !trackMouse) return;
//				  onMouseClicked(e.getX(),e.getY());
//			  }
//			  @Override
//			public void mouseExited(MouseEvent e) {
//				  if (!isEnabled() || !trackMouse) return;
//				  mouseOverSecond = -1;
//				  repaint();
//			}
//		});
//		addMouseMotionListener(new MouseMotionAdapter() {
//			public void mouseMoved(MouseEvent e) {
//				if (!isEnabled() || !trackMouse) return;
//				onMouseMoved(e.getX(),e.getY());
//			}
//		});
	}
	
	/* Stop prefs being repainted over by bar */
	public static void globalDisableMouseMonitoring() {
//		trackMouse = false;
	}

	public static void globalEnableMouseMonitoring() {
//		trackMouse = true;
	}
	
//	protected void onMouseClicked(int x, int y) {
//		mouseOverSecond = getSecondsFromPos(x,y);
//		if (listener != null) {
//			int mins = mouseOverSecond/60;
//			listener.onSelected(wholeHours*60+mins,mouseOverSecond - (mins*60));
//		}
//	}
//	
//	protected void onMouseMoved(int x,int y) {
//		mouseOverSecond = getSecondsFromPos(x,y);
//		repaint();
//	}

	public void setCountdownTimeRemaining(int days,long countDownTenths,long hundredths) {
		long oldCDS = countDownSeconds;
		this.countDownSeconds = countDownTenths/10;
		this.days = days;
		wholeHours = (int)(countDownSeconds / SECONDS_PER_HOUR);
		secondsBelowHourRemaining = (int) (countDownSeconds - (wholeHours * SECONDS_PER_HOUR));
		targetRows = (int)(Math.ceil(secondsBelowHourRemaining / 60.0));
		this.fraction = hundredths - countDownSeconds*100;
		
		if (days > 0) {
			makeRepaintDamageForDays();
			return;
		}
		
		if (countDownSeconds % 60 == 0) {
			repaint();
		} else if (countDownSeconds == oldCDS - 1) {
			int row = (int) (oldCDS / 60);
			int col = (int) (oldCDS - row * 60);
			Rectangle r = calcLightRectangle(row,col);
			if (r.width == 0) {
				repaint();
				return;
			}
			repaint(r);
			row = (int)countDownSeconds / 60;
			col = (int)countDownSeconds - row * 60;
			repaint(calcLightRectangle(row % 60,col));
			// System.out.println("1. row="+row+", col="+col);
		} else if (countDownSeconds == oldCDS) {
			int row = (int)countDownSeconds / 60;
			int col = (int)countDownSeconds - row * 60;
			repaint(calcLightRectangle(row % 60,col));
			// System.out.println("2. row="+row+", col="+col);
		} else {
			repaint();
		}
	}
	
	private long lastRepaintDaySeconds = 0;
	private void makeRepaintDamageForDays() {
		int w = dayWidth-dayGap;
		if (w == 0) w = 1;
		int s = 24 * 60 * 60;
		int d = s / w;
		if (Math.abs(countDownSeconds - lastRepaintDaySeconds) < d) return;
		repaint();
		lastRepaintDaySeconds = countDownSeconds;
	}

	public void setTargetSeconds(long targetSeconds) {
		this.targetSeconds = targetSeconds;
		updateDisplay();
	}
	
	public void reset() {
    	countDownSeconds = targetSeconds;
    	updateDisplay();
	}
	
	void updateDisplay() {
		repaint();
	}
	
	public void flash(boolean onOrOff) {
		this.flash = onOrOff;
		repaint();
	}

//	public void onSelected(int minute, int second) {
//		if (listener != null) {
//			listener.onSelected(minute,second);
//		}
//	}
	
	private void calcLightDimensions() {
		ix = 2;
		int h = getHeight();
		int w = getWidth();
		if (countDownSeconds > SECONDS_PER_HOUR) {
			if (h > w) {
				h *= (1.0 - HOUR_AREA_SCALE);
			} else {
				w *= (1.0 - HOUR_AREA_SCALE);
			}
		}
		double bw = w / 60.0;
		lightWidth = (int)bw;
		int err = (int)(bw * 60) - 60 * lightWidth;
		lightOffset = err /= 2;
		if (targetRows > 0) {
			rowHeight = h / targetRows;
		} else {
			rowHeight = 1;
		}
		if (rowHeight > 8 * lightWidth) {
			rowHeight = 8 * lightWidth;
		}
		topGap = (h - (targetRows*rowHeight)) /2;
		if (lightWidth < 4) ix = 1;		
		if (lightWidth < 1) {
			lightWidth = 1;
			ix = 0;
		}
	}
	
	public void paintComponent(Graphics g) {
		calcLightDimensions();
		Rectangle clip = g.getClipBounds();
		g.setColor(Color.BLACK);
		g.fillRect(clip.x, clip.y, clip.width,clip.height);
		if (days == 0) {
			if (countDownSeconds > SECONDS_PER_HOUR) {
				drawHours(g,clip);
			}
			for (int row=0; row<targetRows; row++) {
				drawRow(g,clip,row);
			}
		} else {
			paintDays(g,clip);
		}
	}

	private Rectangle calcLightRectangle(int row,int col) {
		boolean swap = getHeight() > getWidth();
		int hourGapX = wholeHours > 0 && !swap ? (int)(getWidth() * HOUR_AREA_SCALE) : 0;
		int hourGapY = wholeHours > 0 && swap  ? (int)(getHeight() * HOUR_AREA_SCALE) : 0;
		Rectangle r = new Rectangle(hourGapX + lightOffset + ix + (col*lightWidth), hourGapY + topGap + rowHeight * row,lightWidth-ix,rowHeight);
		return r;
	}
	
	private int getSecondsFromPos(int x,int y) {
		try {
			int hourGap = wholeHours > 0 ? (int)(getWidth() * HOUR_AREA_SCALE) : 0;
			int xx = x - lightOffset - ix - hourGap;
			int yy = y - topGap;
			return (yy/rowHeight) * 60 + (xx/lightWidth); 
		} catch (Exception e) {
			return -1;
		}
	}

	private void drawRow(Graphics g, Rectangle clip,int row) {
		int baseSecondDrawn = row * 60;
		for (int i=0; i<60; i++) {
			int currentSecondDrawn = baseSecondDrawn + i;
			Rectangle r = calcLightRectangle(row, i);
			if (clip.intersects(r) || r.width == 0) {
				if (flash) {
					g.setColor(DigitStyling.BAR_HIGHLIGHT);
				} else {
//					if (currentSecondDrawn == mouseOverSecond) {
//						g.setColor(DigitStyling.BAR_HIGHLIGHT);
//						g.fillRect(r.x, r.y, r.width, r.height);
//						continue;
//					//} else if (currentSecondDrawn < countDownSeconds) {
					/* } else*/
					if (currentSecondDrawn < secondsBelowHourRemaining) {
						if (r.width > 1) {
							createLightImage(r.width, r.height);
							g.drawImage(lightImage,r.x,r.y,null);
						} else if (r.width == 1) {
							g.setColor(DigitStyling.BAR_LED1);
							g.fillRect(r.x, r.y, r.width, r.height);
						} else { // width 0
							g.setColor(i % 2 == 0 ? DigitStyling.BAR_LED0 : DigitStyling.BAR_LED2);
							g.fillRect(lightOffset + ix + i, r.y, 1, r.height);
						}
						continue;
					//} else if (currentSecondDrawn == countDownSeconds) {
					} else if (currentSecondDrawn == secondsBelowHourRemaining) { 
						g.setColor(DigitStyling.getBarFadeColour(fraction));
						g.fillRect(r.x+1, r.y+1, r.width-1, r.height-1);
						continue;
					}
					g.setColor(DigitStyling.BAR_GRAYED);						
				}
				if (r.width == 0) {
					g.fillRect(lightOffset + ix + i, r.y, 1, r.height);
				} else {
					g.fillRect(r.x, r.y, r.width, r.height);
				}
			}
		}
	}
	private static final int inset = 1;
	private BufferedImage createLightImage(int w,int h) {
		if (lightImage == null || cw != w || ch != h) {
			BufferedImage image = GraphicsUtil.createCompatibleImage(w, h);
			Graphics2D g = image.createGraphics();
			g.setColor(DigitStyling.BAR_LED1);
			int w1 = w - inset;
			int h1 = h - inset;
			if (w1 <= 0) w1 = 1;
			if (h1 <= 0) h1 = 1;
			Rectangle r = new Rectangle(inset,inset,w1,h1);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(DigitStyling.BAR_LED2);
			int dx = 2;
			int dy = 2;
			int w2 = r.width-(2*dx);
			int h2 = r.height-(2*dy);
			if (w2 <= 0) w2 = 1;
			if (h2 <= 0) h2 = 1;
			g.fillRect(r.x+dx, r.y+dy, w2, h2);
			lightImage = image;
			g.dispose();
			cw = w;
			ch = h;
		}
		return lightImage;
	}
	
	private int textCutoff = Integer.MAX_VALUE;
	private int dayWidth = 0;
	private int dayHeight = 0;
	private int hoffset;
	private int voffset;
	private int dayGap;
	private void paintDays(Graphics g, Rectangle clip) {
		Graphics2D g2d = (Graphics2D)g;	
		int squares = 1 + days;
		double droot = Math.sqrt(squares);
		int rows = (int)(squares / droot);
		int cols = (int)Math.ceil(1.0 * squares / rows);
		if (getHeight() > getWidth()) { // swap if taller than higher
			int tmp = rows;
			rows = cols;
			cols = tmp;
		}
		dayWidth = getWidth() / cols;
		dayHeight = getHeight() / rows;
		int minSide = Math.min(dayWidth,dayHeight);
		dayGap = (int)(minSide * DAY_GAP_SCALE);
		if (dayGap < 1) dayGap = 1;
		buildGraphicsHelpers(g2d,dayWidth,dayHeight);	
		int c = 0;
		int r = 0;
		int drawnWidthGap = Math.max(0,getWidth() - (cols + 1) * dayWidth);
		int drawnHeightGap = Math.max(0,getHeight() - (rows + 1) * dayHeight);
		hoffset = drawnWidthGap/2 + dayGap/2;
		voffset = drawnHeightGap/2 + dayGap/2;
		for (int i=0; i<squares; i++) {
			int x = hoffset + c*dayWidth;
			int y = voffset + r*dayHeight;
			g.setColor(DigitStyling.BAR_LED1);
			if (i == days) {
				double dayUsedUp = 1.0 * countDownSeconds / SECONDS_PER_DAY; 
				int barWidth = (int)(dayUsedUp * (dayWidth-dayGap));
				g.fillRect(x,y,barWidth,dayHeight-dayGap);
				g.setColor(DigitStyling.BAR_GRAYED);
				g.fillRect(x+barWidth,y,dayWidth-dayGap-barWidth,dayHeight-dayGap);
				g.setColor(DigitStyling.darkenColor(DigitStyling.BAR_LED2,Math.max(dayUsedUp,0.1)));
			} else {
				g.fillRect(x,y,dayWidth-dayGap,dayHeight-dayGap);
				g.setColor(DigitStyling.BAR_LED2);
			}
			if (i < textCutoff) {
				GlyphVector gv = textFont.createGlyphVector(g2d.getFontRenderContext(),String.valueOf(i+1));
				Rectangle2D bounds = gv.getVisualBounds();
				float gapX = (float) ((dayWidth - dayGap - bounds.getWidth()) / 2);
				float gapY = (float) ((dayHeight - dayGap - bounds.getHeight()) / 2);
				if (bounds.getWidth() < dayWidth && bounds.getHeight() < dayHeight) {
					g2d.drawGlyphVector(gv,x+gapX-2,(float)(y+gapY+bounds.getHeight()-2));
				} else {
					textCutoff = i;
				}
			}
			c++;
			if (c == cols) {
				c = 0;
				r++;
			}
		}
	}
	
	private void drawHours(Graphics g, Rectangle clip) {
		Graphics2D g2d = (Graphics2D)g;
		int rows = wholeHours;
		if (rows == 0) return;
		int cols = 1;
		if (rows > 12) {
			cols = 2;
		}
		boolean swap = false;
		if (getHeight() > getWidth()) { // swap if taller than higher
			swap = true;
		}
		
		int r = 0;
		int c = 0;
		
		int w,h,gap;
		if (swap) {
			w = (lightWidth * 60) / Math.min(12,rows);
			h = (int) ((getHeight() * HOUR_AREA_SCALE) / cols);
			gap = h / 10;
		} else { 	
			w = (int) ((getWidth() * HOUR_AREA_SCALE) / cols);
			h = getHeight() / Math.min(12,rows);
			gap = h / 10;
		}
		buildGraphicsHelpers(g2d,w,h-2);
		for (int i=0; i<wholeHours; i++) {
			int x,y;
			if (swap) {
				x = + lightOffset + ix + r*w;
				y = c*h;
			} else {
				x = c*w;
				y = r*h;		
			}
			g.setColor(DigitStyling.BAR_LED0);
			g.fillRect(x,y,w-gap,h-gap);			
			g.setColor(DigitStyling.BAR_LED1);		

			GlyphVector gv = textFont.createGlyphVector(g2d.getFontRenderContext(),String.valueOf(i+1));
			Rectangle2D bounds = gv.getVisualBounds();
			float gapX = (float) ((w - gap - bounds.getWidth()) / 2);
			float gapY = (float) ((h - gap - bounds.getHeight()) / 2);
			g2d.drawGlyphVector(gv,x+gapX-2,(float)(y+gapY+bounds.getHeight()-2));

			r++;
			if (r == 12) {
				r = 0;
				c++;
			}
		}
	}
	
	// Note this is re-used by painting days and hours
	//      needs to change if both occur at same time
	private int cachedW,cachedH;
	private void buildGraphicsHelpers(Graphics2D g2d,int w,int h) {
		if (textFont != null && cachedW == w && cachedH == h) return;
		for (int i=250; i>5; i--) {
			Font f = new Font(DigitStyling.FONT, Font.BOLD,i);
			GlyphVector gv = f.createGlyphVector(g2d.getFontRenderContext(),"9999"); //$NON-NLS-1$
			Rectangle2D bounds = gv.getVisualBounds();
			if (bounds.getWidth() + 2 < w * (1-DAY_GAP_SCALE) && bounds.getHeight() < h * (1-DAY_GAP_SCALE) - 1) {
				textFont = f;
				break;
			}
		}
		if (textFont == null) {
			textFont = new Font(DigitStyling.FONT, Font.BOLD,5);
		}
		cachedW = w;
		cachedH = h;
		textCutoff = Integer.MAX_VALUE;
	}


	
//	public static void main(String[] args) {
//		for (int days=0; days<10; days++) {
//			double droot = Math.sqrt(days);
//			int cols = (int)(days / droot);
//			int rows = (int)Math.ceil(1.0 * days / cols);
//			System.out.println("Days="+days+", cols="+cols+", rows="+rows);
//		}
//	}

}
