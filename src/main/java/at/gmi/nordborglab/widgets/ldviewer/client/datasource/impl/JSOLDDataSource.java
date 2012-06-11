package at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl;

import at.gmi.nordborglab.widgets.ldviewer.client.datasource.AbstractHTTPDataSource;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.LDDataSourceCallback;

import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

public class JSOLDDataSource extends AbstractHTTPDataSource {

	
	public JSOLDDataSource(String url) {
		super(url);
	}

	@Override
	public void fetchLDValues(String prefix,String chr, int start, int end,
			final LDDataSourceCallback callback) {
		
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"?"+prefix+"&chr="+chr+"&start=" + start+ "&end="+ end);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					String json = response.getText();
					LDData data = JsonUtils.safeEval(json);
					callback.onFetchLDValues(data.getSNPs(), data.getR2Values());
				}
			}
			
			@Override
			public void onError(Request request, Throwable exception) {
			}
		});
		try
		{
			request.send();
		}
		catch (Exception e) {
			
		}

	}

}
