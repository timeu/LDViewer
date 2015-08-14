package com.github.timeu.gwtlibs.ldviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class MiddleMouseClickEvent extends GwtEvent<MiddleMouseClickEvent.Handler> {

	public interface Handler extends EventHandler {
		void onMiddleMouseClick(MiddleMouseClickEvent event);
	}


	private static final Type<Handler> TYPE = new Type<Handler>();
	
	
	public MiddleMouseClickEvent() {
	
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	public static Type<Handler> getType() {
		return TYPE;
	}
	

	@Override
	protected void dispatch(Handler handler) {
		handler.onMiddleMouseClick(this);
	}
}
