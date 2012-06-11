package at.gmi.nordborglab.widgets.ldviewer.client;

import at.gmi.nordborglab.processingjs.client.ProcessingInstance;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl.LDDataPoint;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.shared.HasHandlers;

public class LDViewerInstance extends ProcessingInstance{

	protected LDViewerInstance() {}
	
	public final native void setLayoutSize(int width,boolean isDraw) /*-{
		this.api_setSize(width,isDraw);
	}-*/;
	
	public final native void setData(JsArrayInteger snps,JsArray<JsArrayNumber>r2Values,int viewStart,int viewEnd) /*-{
	   this.api_setData(snps,r2Values,viewStart,viewEnd);
	}-*/;
	
	public final native void setHighlightPosition(Integer position) /*-{
	   this.api_setHighlightPosition(position);
	}-*/;
	
	public final native void sinkNativeEvent(HasHandlers view, String callback) /*-{
		if (callback == 'highlightEvent') 
		{
			var callback_func = function(datapoint) {
				@at.gmi.nordborglab.widgets.ldviewer.client.event.HighlightLDEvent::fire(Lat/gmi/nordborglab/widgets/ldviewer/client/event/HasHighlightLDHandlers;Lat/gmi/nordborglab/widgets/ldviewer/client/datasource/impl/LDDataPoint;)(view,datapoint);
			}
		}
		else if (callback == 'unhighlightEvent')
		{
			var callback_func = function() {
				@at.gmi.nordborglab.widgets.ldviewer.client.event.UnhighlightLDEvent::fire(Lat/gmi/nordborglab/widgets/ldviewer/client/event/HasUnhighlightLDHandlers;)(view);
			}
		}
		else if (callback == 'middleMouseClickEvent') {
			var callback_func = function() {
				@at.gmi.nordborglab.widgets.ldviewer.client.event.MiddleMouseClickEvent::fire(Lat/gmi/nordborglab/widgets/ldviewer/client/event/HasMiddleMouseClickHandlers;)(view);
			}
		}
		this.api_addEventHandler(callback,callback_func);
	}-*/;

	public final native void clearNativeEvents() /*-{
		this.api_clearEventHandlers();
	}-*/;

	public final native LDDataPoint[] getHighlightedDataPoints() /*-{
		return this.api_getHighlightedDataPoints();
	}-*/;
	
}
