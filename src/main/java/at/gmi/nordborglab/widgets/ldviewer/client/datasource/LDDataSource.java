package at.gmi.nordborglab.widgets.ldviewer.client.datasource;


public interface LDDataSource {
	public void fetchLDValues(String prefix,String chr, int start, int end, FetchLDCallback callback);
	public void fetchLDValuesForSNP(String prefix,String chr,int position,FetchLDForSNPCallback callback);
	public void fetchExactLDValues(String prefix, String chr, int position,FetchExactLDCallback callback);
}
