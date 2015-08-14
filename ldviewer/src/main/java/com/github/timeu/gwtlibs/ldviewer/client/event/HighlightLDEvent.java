package com.github.timeu.gwtlibs.ldviewer.client.event;


import com.github.timeu.gwtlibs.ldviewer.client.LDDataPoint;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class HighlightLDEvent extends GwtEvent<HighlightLDEvent.Handler> {

	public interface Handler extends EventHandler {
		void onHighlight(HighlightLDEvent event);
	}

	private static final Type<Handler> TYPE = new Type<Handler>();
	private final LDDataPoint ldDataPoint;
	
	public HighlightLDEvent(LDDataPoint ldDatapoint) {
		this.ldDataPoint = ldDatapoint;
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onHighlight(this);
	}
	
	public static Type<Handler> getType() {
		return TYPE;
	}
	
	public LDDataPoint getLDDataPoint() {
		return ldDataPoint;
	}

}
