package at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl;

import com.google.gwt.core.client.JavaScriptObject;

public class LDDataPoint extends JavaScriptObject {

	protected LDDataPoint() {}
	
	public final native float getR2() /*-{
		return this.r2;
	}-*/;
	
	public final native int getPosX() /*-{
		return this.posX;
	}-*/;

	public final native int getPosY() /*-{
		return this.posY;
	}-*/;
	
	public final int getR2Color(double threshold,int maxColor) {
		return (int)Math.round((1 - (getR2() - 0.3)/(1-0.3))*255);
	}
	
}
