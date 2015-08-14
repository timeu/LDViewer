package com.github.timeu.gwtlibs.ldviewer.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class UnhighlightLDEvent extends GwtEvent<UnhighlightLDEvent.Handler> {

	public interface Handler extends EventHandler {

		void onUnhighlight(UnhighlightLDEvent event);
	}
	
	private static final Type<Handler> TYPE = new Type<>();
	
	public UnhighlightLDEvent() {
	}
	
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(Handler handler) {
		handler.onUnhighlight(this);
	}
	
	public static Type<Handler> getType() {
		return TYPE;
	}

}
