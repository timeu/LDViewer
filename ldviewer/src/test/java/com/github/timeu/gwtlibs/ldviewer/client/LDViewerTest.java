package com.github.timeu.gwtlibs.ldviewer.client;

import com.github.timeu.gwtlibs.ldviewer.client.event.HighlightLDEvent;
import com.github.timeu.gwtlibs.ldviewer.client.event.MiddleMouseClickEvent;
import com.github.timeu.gwtlibs.ldviewer.client.event.UnhighlightLDEvent;
import com.github.timeu.gwtlibs.processingjsgwt.client.Processing;
import com.github.timeu.gwtlibs.processingjsgwt.client.ProcessingInstance;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.user.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import com.google.web.bindery.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * Created by uemit.seren on 8/12/15.
 */
@RunWith(GwtMockitoTestRunner.class)
public class LDViewerTest {

    LDViewer ldViewer;
    @Mock
    Processing<LDViewerInstance> processing;
    @Mock LDViewerInstance instance;
    @Mock JavaScriptObject jso;
    StubScheduler scheduler = new StubScheduler();
    @GwtMock
    Element element;


    @Before
    public void setUp() {
        given(processing.getInstance()).willReturn(instance);
        given(processing.isLoaded()).willReturn(true);
        ldViewer = new LDViewer(processing,scheduler);
    }

    @Test
    public void testLoadAndCallOnLoad() throws ResourceException {
        Runnable onLoad = mock(Runnable.class);
        doAnswer(invocationOnMock -> {
            onLoad.run();
            return null;
        }).when(processing).load(Matchers.<ExternalTextResource>anyObject(), any(Runnable.class));
        ldViewer.load(onLoad);
        verify(onLoad).run();
    }

    @Test
    public void testLoadData() throws ResourceException {
        LDData data = getFakeData();
        ldViewer.showLDValues(data);
        verify(ldViewer.getInstance()).api_setData(data.getPositions(), data.getR2(), data.getStart(), data.getEnd());
    }

    @Test(expected = Exception.class)
    public void testExceptionWhenBadData() {
        ldViewer.showLDValues(getBadFakeData());
    }


    @Test
    public void testSetData() {
        ArgumentCaptor<int[]> positionCaptor = ArgumentCaptor.forClass(int[].class);
        ArgumentCaptor<float[][]> r2ValuesCaptor = ArgumentCaptor.forClass(float[][].class);
        LDData data = getFakeData();
        ldViewer.showLDValues(data);
        assertEquals(1000,ldViewer.getZoomStart());
        assertEquals(2000,ldViewer.getZoomEnd());
        verify(ldViewer.getInstance()).api_setData(positionCaptor.capture(),r2ValuesCaptor.capture(),eq(1000),eq(2000));
        int[] capturedPositions = positionCaptor.getValue();
        float[][] capturedr2Values = r2ValuesCaptor.getValue();
        assertArrayEquals(data.getPositions(),capturedPositions);
        assertArrayEquals(data.getR2(), capturedr2Values);
    }

    @Test
    public void testResetZoom() {
        ArgumentCaptor<int[]> positionCaptor = ArgumentCaptor.forClass(int[].class);
        ArgumentCaptor<float[][]> r2ValuesCaptor = ArgumentCaptor.forClass(float[][].class);
        LDData data = getFakeData();
        ldViewer.showLDValues(data);
        ldViewer.setZoom(1100, 1700);
        ldViewer.resetZoom();
        assertEquals(1000, ldViewer.getZoomStart());
        assertEquals(2000, ldViewer.getZoomEnd());
        verify(ldViewer.getInstance(),times(2)).api_setData(positionCaptor.capture(), r2ValuesCaptor.capture(), eq(1000), eq(2000));
    }

    @Test
    public void testIsRangeValid() {
        LDData data = getFakeData();
        ldViewer.showLDValues(data);
        assertFalse(ldViewer.isRangeValid(0, 2));
        assertFalse(ldViewer.isRangeValid(0, 1200));
        assertTrue(ldViewer.isRangeValid(1000, 2000));
        assertFalse(ldViewer.isRangeValid(1500, 4000));
        assertFalse(ldViewer.isRangeValid(2100, 4000));
    }

    @Test
    public void testOnResize() {
        // Required otherwise not called
        LDViewer spy = spy(ldViewer);
        given(spy.isAttached()).willReturn(true);
        spy.onResize();
        assertFalse(scheduler.executeCommands());
    }

    @Test
    public void testForceLayout() {
        LDViewer spy = spy(ldViewer);
        given(spy.isAttached()).willReturn(true);
        given(spy.getElement()).willReturn(element);
        given(spy.isVisible()).willReturn(true);
        given(element.getParentElement()).willReturn(element);
        given(element.getClientWidth()).willReturn(1000);
        spy.forceLayout();
        verify(spy.getInstance()).api_setSize(1000, true);
    }

    @Test
    public void testZoom() {
        ArgumentCaptor<int[]> positionCaptor = ArgumentCaptor.forClass(int[].class);
        ArgumentCaptor<float[][]> r2ValuesCaptor = ArgumentCaptor.forClass(float[][].class);


        LDData data = getFakeData();
        ldViewer.showLDValues(data);
        ldViewer.setZoom(1100, 1700);
        assertEquals(1100, ldViewer.getZoomStart());
        assertEquals(1700, ldViewer.getZoomEnd());
        verify(ldViewer.getInstance()).api_setData(positionCaptor.capture(), r2ValuesCaptor.capture(), eq(1100), eq(1700));
        int[] capturedPositions = positionCaptor.getValue();
        float[][] capturedr2Values = r2ValuesCaptor.getValue();
        assertArrayEquals(new int[]{1200, 1400, 1600}, capturedPositions);
        assertArrayEquals(new float[][]{new float[]{1}, new float[]{1, 2}, new float[]{1, 2, 3}}, capturedr2Values);
    }

    @Test
    public void testGetHighlightedPoints() {
        LDDataPoint[] points = getFakeHighlightedPoints();

        given(ldViewer.getInstance().api_getHighlightedDataPoints()).willReturn(points);
        LDDataPoint[] returnedPoints = ldViewer.getHighlightedDataPoints();
        verify(ldViewer.getInstance()).api_getHighlightedDataPoints();
        assertArrayEquals(returnedPoints,points);
    }

    @Test
    public void testReturnNullWhenNotLoadedAndGetHighlightedPoints() {
        given(processing.isLoaded()).willReturn(false);
        ldViewer.getHighlightedDataPoints();
        verify(ldViewer.getInstance(),never()).api_getHighlightedDataPoints();
    }

    @Test
    public void testSetHighlightedPoints() {
        ldViewer.setHighlightPosition(1);
        verify(ldViewer.getInstance()).api_setHighlightPosition(1);
    }

    @Test
    public void testNoOpWhenInstanceNotLoadedSetHighlightedPoints() {
        given(processing.isLoaded()).willReturn(false);
        ldViewer.setHighlightPosition(1);
        verify(ldViewer.getInstance(),never()).api_setHighlightPosition(anyInt());
    }

    @Test
    public void testAddMiddleMouseClickHandler() {
        MiddleMouseClickEvent.Handler handler = mock(MiddleMouseClickEvent.Handler.class);
        HandlerRegistration registration = ldViewer.addMiddleMouseClickHandler(handler);
        verify(ldViewer.getInstance()).api_addEventHandler(eq("middleMouseClickEvent"), any());
        assertNotNull(registration);
    }

    @Test
    public void testAddHighlightHandler() {
        HighlightLDEvent.Handler handler = mock(HighlightLDEvent.Handler.class);
        HandlerRegistration registration = ldViewer.addHighlightLDHandler(handler);
        verify(ldViewer.getInstance()).api_addEventHandler(eq("highlightEvent"),any());
        assertNotNull(registration);
    }

    @Test
    public void testAddUnHighlightHandler() {
        UnhighlightLDEvent.Handler handler = mock(UnhighlightLDEvent.Handler.class);
        HandlerRegistration registration = ldViewer.addUnhighlightHandler(handler);
        verify(ldViewer.getInstance()).api_addEventHandler(eq("unhighlightEvent"),any());
        assertNotNull(registration);
    }




    private LDDataPoint[] getFakeHighlightedPoints() {
        LDDataPoint[] points = new LDDataPoint[1];
        LDDataPoint point1 = new LDDataPoint();
        point1.posX = 1;
        point1.posY = 2;
        point1.r2 = 0.5;
        points[0] = point1;
        return points;
    }

    private LDData getBadFakeData() {
        LDData data = mock(LDData.class);
        given(data.getStart()).willReturn(1000);
        given(data.getEnd()).willReturn(2000);
        int[] positions = new int[]{1000,1100};
        given(data.getPositions()).willReturn(positions);
        float[][] r2Values = new float[1][];
        r2Values[0] = new float[]{1};
        given(data.getR2()).willReturn(r2Values);
        return data;
    }

    private LDData getFakeData() {
        LDData data = mock(LDData.class);
        given(data.getStart()).willReturn(1000);
        given(data.getEnd()).willReturn(2000);
        int[] positions = new int[]{1000,1200,1400,1600,1800};
        float[][] r2Values = new float[5][];
        for (int i=0;i<5;i++) {
            float[] row = new float[i+1];
            for (int j=1;j<=i;j++) {
                row[j] = j;
            }
            r2Values[i] = row;
        }
        given(data.getPositions()).willReturn(positions);
        given(data.getR2()).willReturn(r2Values);
        return data;
    }
}
