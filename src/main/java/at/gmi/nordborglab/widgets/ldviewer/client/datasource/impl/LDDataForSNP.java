package at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class LDDataForSNP extends JavaScriptObject{
	
	protected LDDataForSNP() {}
	
	public final native JsArray<LDDataForChr> getData() /*-{
		return this.data;
	}-*/;

}
