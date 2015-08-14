package com.github.timeu.gwtlibs.ldviewer.client;


import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

@JsType
public interface LDData {

	@JsProperty int[] getPositions();
	@JsProperty float[][] getR2();
	@JsProperty int getStart();
	@JsProperty int getEnd();
}
