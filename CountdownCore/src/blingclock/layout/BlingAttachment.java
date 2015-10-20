package blingclock.layout;

import javax.swing.JComponent;

public class BlingAttachment {
	
	private int percent;
	private int offset;
	
	private JComponent component;
	private BlingAttachmentSide side;
	
	public BlingAttachment(int percent) {
		this.percent = percent;
	}

	public BlingAttachment(int percent, int offset) {
		this.percent = percent;
		this.offset = offset;
	}
	
	public BlingAttachment(JComponent component,int offset,BlingAttachmentSide side) {
		this.component = component;
		this.offset = offset;
		this.side = side;
	}

	public int compute(int v,BlingLayout layout) {
		if (component == null) {
			return offset + (int)(v * (percent / 100f));
		}
		BlingLayoutData data = layout.layoutFor(component);
		BlingAttachment attachment = data.getAttachment(side);
		if (attachment != null) {
			return offset + attachment.compute(v, layout);
		}
		return 0;
	}

}
