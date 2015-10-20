package blingclock.layout;

import javax.swing.JComponent;

public class BlingLayoutData {
	
	BlingAttachment top;
	BlingAttachment left;
	BlingAttachment right;
	BlingAttachment bottom;
	
	public BlingLayoutData setTop(int percent) { return setTop(percent,0); } 
	public BlingLayoutData setLeft(int percent) { return setLeft(percent,0); }
	public BlingLayoutData setRight(int percent) { return setRight(percent,0); }
	public BlingLayoutData setBottom(int percent) { return setBottom(percent,0); }
	
	public BlingLayoutData setTop(int percent,int offset) { 
		top = new BlingAttachment(percent,offset);
		return this;
	}
	public BlingLayoutData setLeft(int percent,int offset) {
		left = new BlingAttachment(percent,offset);
		return this; 
	}
	public BlingLayoutData setRight(int percent,int offset) { 
		right = new BlingAttachment(percent,offset);
		return this; 
	}
	public BlingLayoutData setBottom(int percent,int offset) { 
		bottom = new BlingAttachment(percent,offset);
		return this; 
	}
	
	public BlingLayoutData setTop(JComponent component,int offset,BlingAttachmentSide side) { 
		top = new BlingAttachment(component,offset,side);	
		return this;
	}
	public BlingLayoutData setLeft(JComponent component,int offset,BlingAttachmentSide side) { 
		left = new BlingAttachment(component,offset,side);	
		return this;
	}
	public BlingLayoutData setRight(JComponent component,int offset,BlingAttachmentSide side) { 
		right = new BlingAttachment(component,offset,side);	
		return this;
	}	
	public BlingLayoutData setBottom(JComponent component,int offset,BlingAttachmentSide side) { 
		bottom = new BlingAttachment(component,offset,side);	
		return this;
	}
	public BlingAttachment getAttachment(BlingAttachmentSide side) {
		switch (side) {
			case TOP: return top;
			case LEFT: return left;
			case RIGHT: return right;
			case BOTTOM: return bottom;
		}
		return null;
	}

}
