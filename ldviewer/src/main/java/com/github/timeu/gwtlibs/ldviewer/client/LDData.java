package com.github.timeu.gwtlibs.ldviewer.client;


import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL,name="Object")
public interface LDData {

	@JsProperty
	int[] getPositions();
	@JsProperty float[][] getR2();
	@JsProperty int getStart();
	@JsProperty int getEnd();
}
