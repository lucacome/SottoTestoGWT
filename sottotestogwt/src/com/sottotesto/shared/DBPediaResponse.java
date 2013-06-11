package com.sottotesto.shared;

public class DBPediaResponse implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private String error;
	private String contentType;
	private String queryResultText;
	private String queryResultJson;
	private String queryResultXML;
	private long time;
	
	public DBPediaResponse() {
		code = 0;
		message = "";
		contentType = "";
		queryResultText = "";
		queryResultJson = "";
		queryResultXML = "";
		time = 0;
		error = "";
	}
	
	
	
	public void setCode(int x) {code = x;}
	public int getCode(){return code;}	
	public void setMessage(String x) {message = x;}
	public String getMessage(){return message;}
	public void setContentType(String x) {contentType = x;}
	public String getContentType(){return contentType;}
	public void setQueryResultText(String x) {queryResultText = x;}
	public String getQueryResultText(){return queryResultText;}
	public void setQueryResultJson(String x) {queryResultJson = x;}
	public String getQueryResultJson(){return queryResultJson;}
	public void setQueryResultXML(String x) {queryResultXML = x;}
	public String getQueryResultXML(){return queryResultXML;}
	public void setTime(long x) {time = x;}
	public long getTime(){return time;}
	public String getError(){return error;}
	public void setError(String x) {error = x;}
}