package at.gmi.nordborglab.widgets.ldviewer.client.datasource;


public interface LDDataSource {
	public void fetchLDValues(String prefix,String chr, int start, int end, LDDataSourceCallback callback);
}
