package at.gmi.nordborglab.widgets.ldviewer.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MiddleMouseClickEvent extends GwtEvent<MiddleMouseClickHandler> {

	private static final Type<MiddleMouseClickHandler> TYPE = new Type<MiddleMouseClickHandler>();
	
	
	public MiddleMouseClickEvent() {
	
	}
	
	@Override
	public Type<MiddleMouseClickHandler> getAssociatedType() {
		return TYPE;
	}


	public static Type<MiddleMouseClickHandler> getType() {
		return TYPE;
	}
	

	@Override
	protected void dispatch(MiddleMouseClickHandler handler) {
		handler.onMiddleMouseClick(this);
		
	}
	
	 public static void fire(HasMiddleMouseClickHandlers handlers) {
	    	handlers.fireEvent(new MiddleMouseClickEvent());
		}

}
