package at.gmi.nordborglab.widgets.ldviewer.client.event;

import com.google.gwt.event.shared.HasHandlers;
import com.google.web.bindery.event.shared.HandlerRegistration;

public interface HasHighlightLDHandlers extends HasHandlers {
	HandlerRegistration addHighlightLDHandler(HighlightLDHandler handler);
}
