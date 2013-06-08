package com.sottotesto.shared;

public class TagmeResponse implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int code;
	private String message;
	private String contentType;
	private String json;
	private String jsonNL;
	private String error;
	
	public TagmeResponse() {
		code = 0;
		message = "";
		contentType = "";
		json = "";
		jsonNL = "";
		error = "";
	}
	
	
	
	public void setCode(int x) {code = x;}
	public int getCode(){return code;}	
	public void setMessage(String x) {message = x;}
	public String getMessage(){return message;}
	public void setContentType(String x) {contentType = x;}
	public String getContentType(){return contentType;}
	public void setJson(String x) {json = x;}
	public String getJson(){return json;}
	public void setJsonNL(String x) {jsonNL = x;}
	public String getJsonNL(){return jsonNL;}
	public void setError(String x) {error = x;}
	public String getError(){return error;}
}
