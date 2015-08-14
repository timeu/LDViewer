package com.github.timeu.gwtlibs.ldviewer.client;

import com.github.timeu.gwtlibs.ldviewer.client.event.EventCallback;
import com.github.timeu.gwtlibs.processingjsgwt.client.ProcessingInstance;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.js.JsType;

import java.util.List;

/**
 * Created by uemit.seren on 8/12/15.
 */
@JsType
interface LDViewerInstance extends ProcessingInstance {

    void api_setSize(int width, boolean isDraw);

    void api_setData(int[] positions, float[][] r2Values, int viewStart, int viewEnd);

    void api_setHighlightPosition(Integer position);

    void api_addEventHandler(String callback, EventCallback handler);

    void api_clearEventHandlers();

    LDDataPoint[] api_getHighlightedDataPoints();
}
