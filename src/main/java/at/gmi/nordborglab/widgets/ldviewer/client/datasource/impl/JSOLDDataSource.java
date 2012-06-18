package at.gmi.nordborglab.widgets.ldviewer.client.datasource.impl;

import at.gmi.nordborglab.widgets.ldviewer.client.datasource.AbstractHTTPDataSource;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.FetchExactLDCallback;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.FetchLDCallback;
import at.gmi.nordborglab.widgets.ldviewer.client.datasource.FetchLDForSNPCallback;

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
			final FetchLDCallback callback) {
		
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

	@Override
	public void fetchLDValuesForSNP(String prefix, String chr, int position,
			final FetchLDForSNPCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"?"+prefix+"&chr="+chr+"&position=" + position);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					String json = response.getText();
					LDDataForSNP data = JsonUtils.safeEval(json);
					callback.onFetchLDForSNP(data);
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

	@Override
	public void fetchExactLDValues(String prefix, String chr, int position,	final FetchExactLDCallback callback) {
		RequestBuilder request = new RequestBuilder(RequestBuilder.GET,url+"?"+prefix+"&chr="+chr+"&position=" + position);
		request.setCallback(new RequestCallback() {
			
			@Override
			public void onResponseReceived(Request request, Response response) {
				if (response.getStatusCode() == Response.SC_OK) {
					String json = response.getText();
					LDData data = JsonUtils.safeEval(json);
					callback.onFetchExactLDValues(data.getSNPs(), data.getR2Values(),data.getStart(),data.getEnd());
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
