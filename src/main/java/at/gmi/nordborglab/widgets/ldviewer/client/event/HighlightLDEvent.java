package at.gmi.nordborglab.widgets.ldviewer.client.event;


import at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl.LDDataPoint;

import com.google.gwt.event.shared.GwtEvent;

public class HighlightLDEvent extends GwtEvent<HighlightLDHandler>{

	
	private static final Type<HighlightLDHandler> TYPE = new Type<HighlightLDHandler>();
	private final LDDataPoint ldDataPoint;
	
	public HighlightLDEvent(LDDataPoint ldDatapoint) {
		this.ldDataPoint = ldDatapoint;
	}
	
	@Override
	public Type<HighlightLDHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HighlightLDHandler handler) {
		handler.onHighlight(this);
	}
	
	public static Type<HighlightLDHandler> getType() {
		return TYPE;
	}
	
    public static void fire(HasHighlightLDHandlers eventBus,final LDDataPoint ldDataPoint) {
    	eventBus.fireEvent(new HighlightLDEvent(ldDataPoint));
	}
	
	public LDDataPoint getLDDataPoint() {
		return ldDataPoint;
	}

}
