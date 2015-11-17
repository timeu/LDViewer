package com.github.timeu.gwtlibs.ldviewer.client;


import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true,namespace = JsPackage.GLOBAL,name="Object")
public class LDDataPoint {

	public double r2;

	public int posX;

	public int posY;

	@JsOverlay
	public final int getR2Color(double threshold,int maxColor) {
		return (int)Math.round((1 - (r2 - threshold)/(1-threshold))*maxColor);
	}
}
