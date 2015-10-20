package blingclock.calendar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JPanel;

// want 3d effect of scrolling calendar
public class MonthCalendarPanel extends JPanel {
	
	private Font font1;
	private Font font2;
	
	private int month;
	private int year;
	
	String title = ""; //$NON-NLS-1$
	
	private String[] DAYS;
	private int startDay; // 0 = Sunday
	private int days;
	
	private boolean showYear = false;
	
	private float insetx;
	private float insety;
	private float dx;
	private float dy;

	private int hightlightCell = -1;
	
	private Color dayColor = new Color(210,210,210);
	private Color highColor = new Color(255,255,255);
	
	public MonthCalendarPanel(int month,int year,boolean showYear) {
		this.month = month;
		this.year = year;
		this.showYear = showYear;
		setBackground(new Color(0,0,0));
		setLayout(null);
		calculateMonthData();
		addMouseListener(new MouseAdapter() { 
			  public void mouseClicked(MouseEvent e) {
				  onMouseClicked(e.getX(),e.getY());
			  }
			  @Override
			public void mouseExited(MouseEvent e) {
				  hightlightCell = -1;
				  repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				onMouseMoved(e.getX(),e.getY());
			}
		});
	}
	
	public void setYear(int year) {
		this.year = year;
		calculateMonthData();
		repaint();
	}

	private void calculateMonthData() {
	    Calendar cal = new GregorianCalendar(year, month - 1, 1);
	    days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); 
	    startDay = cal.get(Calendar.DAY_OF_WEEK);	
	    if (showYear) {
	    	title = getMonth(month-1) + " " + year;	 //$NON-NLS-1$
	    } else {
	    	title = getMonth(month-1);
	    }
	    DAYS = daysOfWeek();
	}
	
	protected void onMouseMoved(int px, int py) {
		float xx = px - dx;
		float yy = py - insety;
		int col = 1 + (int)(xx / dx);
		int row = (int)(yy / dy) - 1;
		hightlightCell = row * 7 + col - startDay + 1;
		repaint();
	}

	protected void onMouseClicked(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
	private int cachedFontSize;
	private void buildGraphicsHelpers() {
		int fontSize1 = getWidth() / 16;
		int fontSize2 = getHeight() / 11;
		int fontSize = Math.min(fontSize1,fontSize2);
		if (font1 == null || cachedFontSize != fontSize) {
			font1 = new Font("Verdana", Font.BOLD,fontSize); //$NON-NLS-1$
			font2 = new Font("Verdana", Font.PLAIN,fontSize); //$NON-NLS-1$
			cachedFontSize = fontSize;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		buildGraphicsHelpers();
		Graphics2D g2d = (Graphics2D)g;
		GlyphVector gv = font1.createGlyphVector(g2d.getFontRenderContext(),"M"); //$NON-NLS-1$
		Rectangle2D rectangle = gv.getLogicalBounds();
		double w = rectangle.getWidth();
		float y = (float)w;
		dx = (float)(w*2);
		dy = (float)(w*1.5);
		insetx = (float)w*3;
		insety = (float)w;
		float x = insetx;

		g2d.setColor(Color.GREEN);
		GlyphVector titlev = font1.createGlyphVector(g2d.getFontRenderContext(),title);
		drawCenterAlign(g2d,titlev,insetx + 6*dx / 2, y);
		
		y += dy;
		
		g2d.setColor(Color.GRAY);
		for (String d : DAYS) {
			GlyphVector v = font1.createGlyphVector(g2d.getFontRenderContext(),d);
			drawRightAlign(g2d,v,x,y);
			x += dx;
		}
		
		int day = 1 - startDay;
		DONE:
		for (int row=1; row<7; row++) {
			for (int col=0; col<7; col++) {
				day++;
				if (day <= 0) continue;
				if (day > days) break DONE;
				x = insetx + col*dx;
				y = insety + (1+row)*dy;
				if (day == hightlightCell) {
					g2d.setColor(Color.GREEN);
				} else {
					if (hightlightCell == -1) {
						g2d.setColor(dayColor);
					} else {
						g2d.setColor(highColor);
					}
				}
				GlyphVector v = font2.createGlyphVector(g2d.getFontRenderContext(),String.valueOf(day));
				drawRightAlign(g2d,v,x,y);
			}
		}		
		
	}
	
	private void drawCenterAlign(Graphics2D g2d, GlyphVector v, double x,float y) {
		float w = (float)v.getLogicalBounds().getWidth();
		g2d.drawGlyphVector(v, (float)(x - w/2), y);
	}

	private void drawRightAlign(Graphics2D g2d,GlyphVector v,float x,float y) {
		float w = (float)v.getLogicalBounds().getWidth();
		g2d.drawGlyphVector(v, x - w, y);
	}
	
    private String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }
    
    private String[] daysOfWeek() { 
    	DateFormatSymbols symbols = new DateFormatSymbols();
    	String[] wd = symbols.getWeekdays();
    	String[] days = new String[7];
    	for (int i=0; i<7; i++) {
    		days[i] = wd[i+1].substring(0,1);
    	}
    	return days;
    }
    
}
