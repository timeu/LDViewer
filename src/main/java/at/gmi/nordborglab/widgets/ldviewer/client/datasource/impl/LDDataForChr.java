package at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;

public class LDDataForChr extends JavaScriptObject {
	protected LDDataForChr() {
	}

	public final native JsArrayInteger getSNPs() /*-{
		return this.snps;
	}-*/;

	public final native JsArrayNumber getR2Values() /*-{
		return this.r2;
	}-*/;
}
