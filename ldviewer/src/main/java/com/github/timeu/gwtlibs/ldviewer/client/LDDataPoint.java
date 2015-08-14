package com.github.timeu.gwtlibs.ldviewer.client;


import com.google.gwt.core.client.js.JsProperty;
import com.google.gwt.core.client.js.JsType;

@JsType
public interface LDDataPoint {


	@JsProperty double getR2();

	@JsProperty void setR2(double r2);
	
	@JsProperty void setPosX(int posX);
	
	@JsProperty int getPosX();

	@JsProperty int getPosY();
	
	@JsProperty void setPosY(int posY);
	
	int getR2Color(double threshold,int maxColor);
	
}
