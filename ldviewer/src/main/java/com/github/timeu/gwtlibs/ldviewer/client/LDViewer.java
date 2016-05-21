package com.github.timeu.gwtlibs.ldviewer.client;


import com.github.timeu.gwtlibs.ldviewer.client.event.EventCallback;
import com.github.timeu.gwtlibs.ldviewer.client.event.HasHighlightLDHandlers;
import com.github.timeu.gwtlibs.ldviewer.client.event.HasMiddleMouseClickHandlers;
import com.github.timeu.gwtlibs.ldviewer.client.event.HasUnhighlightLDHandlers;
import com.github.timeu.gwtlibs.ldviewer.client.event.MiddleMouseClickEvent;
import com.github.timeu.gwtlibs.ldviewer.client.event.UnhighlightLDEvent;
import com.github.timeu.gwtlibs.processingjsgwt.client.Processing;
import com.github.timeu.gwtlibs.ldviewer.client.event.HighlightLDEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class LDViewer extends Composite implements  RequiresResize,
        HasUnhighlightLDHandlers,HasHighlightLDHandlers,
        HasMiddleMouseClickHandlers {

    interface Resources extends ClientBundle
	{
		Resources INSTANCE = GWT.create(Resources.class);
		@Source("resources/LDViewer.pde")
		ExternalTextResource getCode();
	}
	
	private Processing<LDViewerInstance> processing;
	private int start;
	private int end;
	private int zoomStart;
	private int zoomEnd;
	private int width=1024;
	private int[] positions;
	private float[][] r2Values;
	private Scheduler scheduler;
	
	private final ScheduledCommand layoutCmd = new ScheduledCommand() {
    	public void execute() {
    		layoutScheduled = false;
		    forceLayout();
		}
    };
	private boolean layoutScheduled = false;

    /**
     * Creates a new LDViewer visualization widget
     */
	public LDViewer() {
        this(new Processing<>(),null);
	}

	// Constrcutor for testing
    LDViewer(Processing<LDViewerInstance> processing,Scheduler scheduler) {
		this.scheduler = scheduler;
		if (scheduler == null) {
			this.scheduler = Scheduler.get();
		}
        this.processing = processing;
        initWidget(processing);
    }


	/**
     * Loads the ProcessingJS LDViewer sketch.
     * If you need to interact with the visualization wait for the execution of the onLoad runnable
     *
     * @param onLoad pass a callback that is executed when the visualization is finished loading
     * @throws ResourceException
     */
	public void load(final Runnable onLoad) throws ResourceException {
        Runnable onLoadCode = new Runnable()
	    {
			@Override
			public void run() {
				if (!processing.isLoaded())
					return;
                onResize();
				if (onLoad != null)
					onLoad.run();
			}
		};
		processing.load(Resources.INSTANCE.getCode(), onLoadCode);
	}

    /**
     * Pass data as a {@link LDData} to the LDViewer.
     * This will pass through to the {@link #showLDValues(int[], float[][], int, int)} method
     *
     * @param data the data that should be rendered
     */
    public void showLDValues(LDData data) {
        showLDValues(data.getSnps(), data.getR2(), data.getStart(), data.getEnd());
    }

    /**
     * Pass data to the LDViewer.
     *
     * @param positions list of positions
     * @param r2Values lower-triangle form of r2 values
     * @param start of the region
     * @param end of the region
     */
	public void showLDValues(int[] positions,float[][] r2Values,int start, int end) {
        if (positions.length != r2Values.length)
            throw new RuntimeException("Number of positions must match number of r2values");
		this.positions = positions;
		this.r2Values = r2Values;
		this.start = start;
		this.end = end;
		this.zoomStart = start;
		this.zoomEnd = end;
		showPlot();
	}

    /**
     * Zooms into a specific region
     *
     * @param zoomStart of the region
     * @param zoomEnd of the region
     */
	public void setZoom(int zoomStart, int zoomEnd) {
		this.zoomStart = zoomStart >= start ? zoomStart : start;
		this.zoomEnd = zoomEnd <= end ? zoomEnd :end;
		showPlot();
	}

    /**
     * returns the start of the zoomed-in region
     * @return the start of the zoomed-in region
     */
	public int getZoomStart() {
		return zoomStart;
	}

    /**
     * returns the end of the zoomed-in region
     * @return the end of the zoomed-in region
     */
	public int getZoomEnd() {
		return zoomEnd;
	}

    /**
     * resets the zoom
     */
	public void resetZoom() {
		setZoom(start,end);
	}
	
	private void showPlot() {
		if (!processing.isLoaded() || positions == null || r2Values == null)
			return;
		int[] indices = getFilteredIndices(positions);
		processing.getInstance().api_setData(filterPositions(positions, indices), filterR2Values(r2Values, indices), zoomStart, zoomEnd);
	}
	
	private float[][] filterR2Values(float[][] r2Values,int[] indices) {
		if (zoomStart == start && zoomEnd == end) 
			return r2Values;
		int newSize = indices[1]-indices[0];
		float[][] filtered = new float[newSize][];
		for (int i = 0;i<newSize;i++) {
			float[] r2vals = r2Values[(indices[0]+i)];
			int newRowSize = (r2vals.length - indices[0]);
			float [] filteredR2Vals = new float[newRowSize];
			for (int j=0;j<newRowSize;j++) {
				filteredR2Vals[j] = r2vals[(j+indices[0])];
			}
			filtered[i] = filteredR2Vals;
		}
		return filtered;
	}
	
	private int[] getFilteredIndices(int[] positions) {
		int[] indices = new int[2];
		indices[0] = 0;
		indices[1] = positions.length;
		if (zoomStart == start && zoomEnd == end) { 
			return indices;
 		}
		for (int i = 0;i<positions.length;i++)
		{
			if (zoomStart > start && indices[0] == 0 && positions[i] > zoomStart)
				indices[0] = i;
			if (zoomEnd < end && indices[1] == positions.length && positions[i] > zoomEnd)
				indices[1] = i;
		}
		return indices;
	}

	private int[] filterPositions(int[] positions, int[] indices) {
		if (zoomStart == start && zoomEnd == end) 
			return positions;
		int newSize = indices[1] - indices[0];
		int[] filtered = new int[newSize];
		for (int i = 0;i<newSize;i++) {
			filtered[i] = positions[(i+indices[0])];
		}
		return filtered;
	}

    /**
     * checks if the passed in range is valid
     *
     * @param startRange
     * @param endRange
     * @return
     */
	public boolean isRangeValid(int startRange,int endRange) {
		return startRange >= start && endRange <= zoomEnd ;
	}
	
	void forceLayout() {
		if (!isAttached())
			return;
        int newWidth = getElement().getParentElement().getClientWidth();
        if (newWidth == 0)
            return;
		width = newWidth;
		if (processing.isLoaded()) {
			processing.getInstance().api_setSize(width, isVisible());
		}
	}
	
	private void scheduledLayout() {
	    if (isAttached() && !layoutScheduled) {
	      layoutScheduled = true;
	      scheduler.scheduleDeferred(layoutCmd);
	    }
	}
	
	@Override
	public void onResize() {
		scheduledLayout();
	}

    /**
     * Highlights all LD values for a specific position
     *
     * @param position for which position the LD values should be highlighted
     */
	public void setHighlightPosition(Integer position) {
		if (!processing.isLoaded())
			return;
		processing.getInstance().api_setHighlightPosition(position);
	}

	protected final void sinkEvents(Event.Type<?> type,EventHandler handler) {
		String callback = "";
        EventCallback eventCallback = null;
		if (type == HighlightLDEvent.getType()) {
            eventCallback = new EventCallback<LDDataPoint>() {
                @Override
                public void onCall(LDDataPoint data) {
                    fireEvent(new HighlightLDEvent(data));
                }
            };
            callback = "highlightEvent";
        }
		else if (type == UnhighlightLDEvent.getType()) {
            eventCallback = new EventCallback<Void>() {
                @Override
                public void onCall(Void data) {
                    fireEvent(new UnhighlightLDEvent());
                }
            };
            callback = "unhighlightEvent";
        }
		else if (type == MiddleMouseClickEvent.getType()) {
            eventCallback = new EventCallback<Void>() {
                @Override
                public void onCall(Void data) {
                    fireEvent(new MiddleMouseClickEvent());
                }
            };
            callback = "middleMouseClickEvent";
        }
		processing.getInstance().api_addEventHandler(callback, eventCallback);
	}

    /**
     * Returns the highlighted LD values
     *
     * @return the highlighted LD values
     */
	public LDDataPoint[] getHighlightedDataPoints() {
		if (processing.isLoaded())
			return processing.getInstance().api_getHighlightedDataPoints();
		return null;
	}

	@Override
	public HandlerRegistration addMiddleMouseClickHandler(
			MiddleMouseClickEvent.Handler handler) {
		sinkEvents(MiddleMouseClickEvent.getType(),handler);
		return addHandler(handler, MiddleMouseClickEvent.getType());
	}

    @Override
    public HandlerRegistration addHighlightLDHandler(HighlightLDEvent.Handler handler) {
        sinkEvents(HighlightLDEvent.getType(), handler);
        return addHandler(handler, HighlightLDEvent.getType());
    }

    @Override
    public HandlerRegistration addUnhighlightHandler(
            UnhighlightLDEvent.Handler handler) {
        sinkEvents(UnhighlightLDEvent.getType(),handler);
        return addHandler(handler, UnhighlightLDEvent.getType());
    }

    LDViewerInstance getInstance() {
        return processing.getInstance();
    }
}
