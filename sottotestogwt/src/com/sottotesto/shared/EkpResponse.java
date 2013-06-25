package com.sottotesto.shared;


public class EkpResponse implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private String contentType;
	private String rdf;
	private String error;
	private String type;
	private String tag;
	private long time;
	public String jdata;

	public EkpResponse() {
		code = 0;
		message = "";
		contentType = "";
		tag = "";
		error = "";
		time = 0;
		type = "";
		jdata = "";
	}



	public void setCode(int x) {code = x;}
	public int getCode(){return code;}	
	public void setMessage(String x) {message = x;}
	public String getMessage(){return message;}
	public void setContentType(String x) {contentType = x;}
	public String getContentType(){return contentType;}
	public void setRDF(String x) {rdf = x;}
	public String getRDF(){return rdf;}
	public void setError(String x) {error = x;}
	public String getError(){return error;}
	public void setType(String x) {type = x;}
	public String getType(){return type;}
	public void setTag(String x) {tag = x;}
	public String getTag(){return tag;}
	public void setTime(long x) {time = x;}
	public long getTime(){return time;}
}