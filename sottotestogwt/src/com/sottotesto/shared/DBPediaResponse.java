package com.sottotesto.shared;

public class DBPediaResponse implements java.io.Serializable{
	private int code;
	private String message;
	private String contentType;
	private String json;
	private String jsonHTML;
	
	public DBPediaResponse() {
		code = 0;
		message = "";
		contentType = "";
		json = "";
		jsonHTML = "";
	}
	
	
	
	public void setCode(int x) {code = x;}
	public int getCode(){return code;}	
	public void setMessage(String x) {message = x;}
	public String getMessage(){return message;}
	public void setContentType(String x) {contentType = x;}
	public String getContentType(){return contentType;}
	public void setJson(String x) {json = x;}
	public String getJson(){return json;}
	public void setJsonHTML(String x) {jsonHTML = x;}
	public String getJsonHTML(){return jsonHTML;}
}
