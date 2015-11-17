/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package sample.client;

import com.github.timeu.gwtlibs.ldviewer.client.LDViewer;
import com.github.timeu.gwtlibs.ldviewer.client.LDData;
import com.github.timeu.gwtlibs.ldviewer.client.LDDataPoint;
import com.github.timeu.gwtlibs.ldviewer.client.event.HighlightLDEvent;
import com.github.timeu.gwtlibs.ldviewer.client.event.MiddleMouseClickEvent;
import com.github.timeu.gwtlibs.ldviewer.client.event.UnhighlightLDEvent;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;


/**
 * Initializes the application. Nothing to see here: everything interesting
 * happens in the presenters.
 */
public class SampleEntryPoint implements EntryPoint {

    public interface DataBundle extends ClientBundle {
        DataBundle INSTANCE = GWT.create(DataBundle.class);

        @Source("data/ld_sample_data.json")
        TextResource data1();

        @Source("data/ld_sample_exact.json")
        TextResource exact();
    }

    public interface DataFactory extends AutoBeanFactory {
        AutoBean<LDData> data();
    }

    final LDViewer ldviewer = new LDViewer();
    HTML eventPanel = new HTML();
    HTML zoomLabel = new HTML();
    int regionSize = 0;
    final int sparseZoomAmount = 1000000;
    final int denseZoomAmount = 10000;
    RadioButton sparseRd = new RadioButton("type","Sparse");
    RadioButton denseRd = new RadioButton("type","Dense");
    DataFactory dataFactory = GWT.create(DataFactory.class);

    @Override
    public void onModuleLoad() {
        FlowPanel panel = new FlowPanel();

        Button highlightBtn = new Button("Highlight position 10976308");

        highlightBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ldviewer.setHighlightPosition(10976308);
                String eventMsg = "Highlighted datapoints: [";
                for (LDDataPoint point : ldviewer.getHighlightedDataPoints()) {
                    eventMsg += "{" + getMsgFromDataPoint(point) + "}";
                }
                eventMsg += "]";
                logEvent(eventMsg);
            }
        });
        sparseRd.setValue(true);
        sparseRd.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                changeType(false);
            }
        });

        denseRd.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                changeType(true);
            }
        });

        Button zoomInBtn = new Button("Zoom in");
        zoomInBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                zoom(true);
            }
        });
        Button zoomOutBtn = new Button("Zoom out");
        zoomOutBtn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                zoom(false);
            }
        });
        panel.add(sparseRd);
        panel.add(denseRd);
        panel.add(zoomLabel);
        panel.add(zoomInBtn);
        panel.add(zoomOutBtn);
        panel.add(highlightBtn);
        panel.add(ldviewer);
        panel.add(new HTML("Events:"));
        panel.add(eventPanel);


        final LDData data = getData(false);
        RootPanel.get().add(panel);
        try {
            ldviewer.load(new Runnable() {
                @Override
                public void run() {
                    sinkEvents();
                    changeType(false);
                }
            });
        } catch (Exception e) {
            GWT.log("Error loading LDViewer", e);
        }
    }

    private void zoom(boolean isZoomIn) {
        int zoomAmount = sparseRd.getValue() ? sparseZoomAmount:denseZoomAmount;
        int start = ldviewer.getZoomStart();
        int end = ldviewer.getZoomEnd();
        if (isZoomIn) {
            start = start + zoomAmount;
            end= end - zoomAmount;
        }
        else {
            start = start - zoomAmount;
            end = end + zoomAmount;
        }
        ldviewer.setZoom(start,end);
        updateZoomLabel();
    }

    private String getMsgFromDataPoint(LDDataPoint data) {
        return "x:" + data.posX + ", y:" + data.posY + ",r2:" + data.r2;
    }

    private void sinkEvents() {
        ldviewer.addMiddleMouseClickHandler(new MiddleMouseClickEvent.Handler() {
            @Override
            public void onMiddleMouseClick(MiddleMouseClickEvent event) {
                logEvent("MiddleMouseClickEvent fired");
            }
        });
        ldviewer.addUnhighlightHandler(new UnhighlightLDEvent.Handler() {
            @Override
            public void onUnhighlight(UnhighlightLDEvent event) {
                logEvent("UnhighlightEvent fired");
            }
        });
        ldviewer.addHighlightLDHandler(new HighlightLDEvent.Handler() {
            @Override
            public void onHighlight(HighlightLDEvent event) {
                LDDataPoint data = event.getLDDataPoint();
                logEvent("HighlightEvent fired. Data:" + getMsgFromDataPoint(data));
            }
        });
    }

    private void changeType(boolean isDense)
    {
        ldviewer.resetZoom();
        LDData data = getData(isDense);
        ldviewer.showLDValues(data);
        regionSize = data.getEnd()-data.getStart();
        updateZoomLabel();
    }

    private LDData getData(boolean isDense) {
        final String jsonData = isDense ? DataBundle.INSTANCE.exact().getText() : DataBundle.INSTANCE.data1().getText();
        LDData data = JsonUtils.safeEval(jsonData);
        return data;
    }

    private void updateZoomLabel() {
        int currentZoomAmount = regionSize - (ldviewer.getZoomEnd()-ldviewer.getZoomStart());
        zoomLabel.setHTML("Zoom: " + String.valueOf(currentZoomAmount));
    }

    private void logEvent(String event) {
        eventPanel.setHTML(eventPanel.getHTML()+"<div>"+event+"</div>");
    }
}
