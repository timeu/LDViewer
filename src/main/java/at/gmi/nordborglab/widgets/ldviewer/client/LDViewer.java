package at.gmi.nordborglab.widgets.ldviewer.client;


import at.gmi.nordborglab.processingjs.client.Processing;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl.LDDataPoint;
import at.gmi.nordborglab.widgets.ldviewer.client.event.HasHighlightLDHandlers;
import at.gmi.nordborglab.widgets.ldviewer.client.event.HasMiddleMouseClickHandlers;
import at.gmi.nordborglab.widgets.ldviewer.client.event.HasUnhighlightLDHandlers;
import at.gmi.nordborglab.widgets.ldviewer.client.event.HighlightLDEvent;
import at.gmi.nordborglab.widgets.ldviewer.client.event.HighlightLDHandler;
import at.gmi.nordborglab.widgets.ldviewer.client.event.MiddleMouseClickEvent;
import at.gmi.nordborglab.widgets.ldviewer.client.event.MiddleMouseClickHandler;
import at.gmi.nordborglab.widgets.ldviewer.client.event.UnhighlightLDEvent;
import at.gmi.nordborglab.widgets.ldviewer.client.event.UnhighlightLDHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class LDViewer extends Composite implements  RequiresResize, 
													HasUnhighlightLDHandlers,HasHighlightLDHandlers,
													HasMiddleMouseClickHandlers{

	private static LDViewerUiBinder uiBinder = GWT
			.create(LDViewerUiBinder.class);

	interface LDViewerUiBinder extends UiBinder<Widget, LDViewer> {
	}
	interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);
		@Source("resources/LDViewer.pde")
		ExternalTextResource getCode();
	}
	
	@UiField Processing<LDViewerInstance> processing;
	
	private int start;
	private int end;
	private int zoomStart;
	private int zoomEnd;
	private int width=1024;
	private JsArrayInteger snps;
	private JsArray<JsArrayNumber> r2Values;
	
	private final ScheduledCommand layoutCmd = new ScheduledCommand() {
    	public void execute() {
    		layoutScheduled = false;
		    forceLayout();
		}
    };
	private boolean layoutScheduled = false;

	public LDViewer() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void load(final Runnable onLoad) throws ResourceException {
		Runnable onLoadCode = new Runnable() 
		{
			@Override
			public void run() {
				if (!processing.isLoaded())
					return;
				processing.getInstance().setLayoutSize(getElement().getClientWidth(), false);
				if (onLoad != null)
					onLoad.run();
			}
		};
		processing.load(Resources.INSTANCE.getCode(), onLoadCode);
	}

	
	public void showLDValues(JsArrayInteger SNPs,JsArray<JsArrayNumber> r2Values,int start, int end) {
		this.snps = SNPs;
		this.r2Values = r2Values;
		this.start = start;
		this.end = end;
		this.zoomStart = start;
		this.zoomEnd = end;
		showPlot();
	}

	public void setZoom(int start, int end) {
		zoomStart = zoomStart >= start ? zoomStart : start;
		zoomEnd = zoomEnd <= end ? zoomEnd :end;
		showPlot();
	}
	
	public int getZoomStart() {
		return zoomStart;
	}
	
	public int getZoomEnd() {
		return zoomEnd;
	}
	
	public void resetZoom() {
		zoomStart = start;
		zoomEnd = end;
		showPlot();
	}
	
	private void showPlot() {
		if (!processing.isLoaded())
			return;
		int[] indices = getFilteredIndices(snps);
		processing.getInstance().setData(filterSNPs(snps,indices),filterR2Values(r2Values,indices),zoomStart,zoomEnd);
	}
	
	private JsArray<JsArrayNumber> filterR2Values(JsArray<JsArrayNumber> r2Values,int[] indices) {
		if (zoomStart == start && zoomEnd == end) 
			return r2Values;
		JsArray<JsArrayNumber> filtered = JsArray.createArray().cast();
		for (int i = indices[0];i<indices[1];i++) {
			JsArrayNumber r2vals = r2Values.get(i);
			JsArrayNumber filteredR2Vals = JsArrayNumber.createArray().cast();
			for (int j=indices[0];j<r2vals.length();j++) {
				filteredR2Vals.push(r2vals.get(j));
			}
			filtered.push(filteredR2Vals);
		}
		return filtered;
	}
	
	private int[] getFilteredIndices(JsArrayInteger snps) {
		int[] indices = new int[2];
		indices[0] = 0;
		indices[1] = snps.length();
		if (zoomStart == start && zoomEnd == end) { 
			return indices;
 		}
		for (int i = 0;i<snps.length();i++) 
		{
			if (zoomStart > start && indices[0] == 0 && snps.get(i) > zoomStart)
				indices[0] = i;
			if (zoomEnd < end && indices[1] == snps.length() && snps.get(i) > zoomEnd)
				indices[1] = i;
		}
		return indices;
	}

	private JsArrayInteger filterSNPs(JsArrayInteger snps,int[] indices) {
		if (zoomStart == start && zoomEnd == end) 
			return snps;
		JsArrayInteger filtered = JsArrayInteger.createArray().cast();
		for (int i = indices[0];i<indices[1];i++) {
			filtered.push(snps.get(i));
		}
		return filtered;
	}

	public boolean isDataValid(int zoomStart,int zoomEnd) {
		return zoomStart >= start && zoomEnd < end;
	}
	
	public void forceLayout() {
		if (!isAttached())
			return;
		width = getElement().getClientWidth();
		if (processing.isLoaded()) {
			processing.getInstance().setLayoutSize(width,isVisible());
		}
	}
	
	private void scheduledLayout() {
	    if (isAttached() && !layoutScheduled) {
	      layoutScheduled = true;
	      Scheduler.get().scheduleDeferred(layoutCmd);
	    }
	}
	
	@Override
	public void onResize() {
		scheduledLayout();
	}

	public void setHighlightPosition(Integer position) {
		if (!processing.isLoaded()) 
			return;
		processing.getInstance().setHighlightPosition(position);
	}

	@Override
	public HandlerRegistration addHighlightLDHandler(HighlightLDHandler handler) {
		sinkEvents(HighlightLDEvent.getType());
		return addHandler(handler, HighlightLDEvent.getType());
	}

	@Override
	public HandlerRegistration addUnhighlightHandler(
			UnhighlightLDHandler handler) {
		sinkEvents(UnhighlightLDEvent.getType());
		return addHandler(handler, UnhighlightLDEvent.getType());
	}
	
	protected final void sinkEvents(GwtEvent.Type<?> type) {
		String callback = "";
		if (type == HighlightLDEvent.getType())
			callback = "highlightEvent";
		else if (type == UnhighlightLDEvent.getType())
			callback = "unhighlightEvent";
		else if (type == MiddleMouseClickEvent.getType())
			callback = "middleMouseClickEvent";
		processing.getInstance().sinkNativeEvent(this,callback);
	}

	public LDDataPoint[] getHighlightedDataPoints() {
		if (processing.getInstance() != null)
			return processing.getInstance().getHighlightedDataPoints();
		return null;
	}

	@Override
	public HandlerRegistration addMiddleMouseClickHandler(
			MiddleMouseClickHandler handler) {
		sinkEvents(MiddleMouseClickEvent.getType());
		return addHandler(handler, MiddleMouseClickEvent.getType());
	}
}
