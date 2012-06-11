package at.gmi.nordborglab.widgets.ldviewer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UnhighlightLDEvent extends GwtEvent<UnhighlightLDHandler>{
	
	private static final Type<UnhighlightLDHandler> TYPE = new Type<UnhighlightLDHandler>();
	
	public UnhighlightLDEvent() {
	}
	
	@Override
	public Type<UnhighlightLDHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(UnhighlightLDHandler handler) {
		handler.onUnhighlight(this);
	}
	
	public static Type<UnhighlightLDHandler> getType() {
		return TYPE;
	}
	
    public static void fire(HasUnhighlightLDHandlers handlers) {
    	handlers.fireEvent(new UnhighlightLDEvent());
	}

}
