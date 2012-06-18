package at.gmi.nordborglab.widgets.ldviewer.client.datasource;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsArrayNumber;

public interface FetchExactLDCallback {
	public void onFetchExactLDValues(JsArrayInteger snps,JsArray<JsArrayNumber> r2Values,int start,int end);
}
