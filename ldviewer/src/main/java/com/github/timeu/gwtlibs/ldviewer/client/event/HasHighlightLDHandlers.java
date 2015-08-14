package com.github.timeu.gwtlibs.ldviewer.client.event;

import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * A widget that implements this interface provides registration for
 * {@link HighlightLDEvent.Handler} instances.
 */
public interface HasHighlightLDHandlers extends HasHandlers {
	/**
	 * Adds a {@link HighlightLDEvent.Handler} handler.
	 *
	 * @param handler the highlightld handler
	 * @return {@link com.google.gwt.event.shared.HandlerRegistration} used to remove this handler
	 */
	HandlerRegistration addHighlightLDHandler(HighlightLDEvent.Handler handler);
}
