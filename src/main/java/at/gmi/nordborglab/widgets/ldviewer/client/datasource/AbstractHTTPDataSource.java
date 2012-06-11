package at.gmi.nordborglab.widgets.ldviewer.client.datasource;


public abstract class AbstractHTTPDataSource implements LDDataSource{

	protected String url;
	
	public AbstractHTTPDataSource(String url) {
		this.url = url;
	}

}
