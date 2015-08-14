package com.github.timeu.gwtlibs.ldviewer.client.event;

import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public interface HasMiddleMouseClickHandlers extends HasHandlers {
	HandlerRegistration addMiddleMouseClickHandler(MiddleMouseClickEvent.Handler handler);
}
