package com.sottotesto.shared;

import com.sottotesto.shared.TagmeData;

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
	private TagmeData jsonData;
	private int resNum;
	private long time;
	
	public TagmeResponse() {
		code = 0;
		message = "";
		contentType = "";
		json = "";
		jsonNL = "";
		error = "";
		jsonData = new TagmeData();
		resNum = 0;
		time = 0;
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
	public void setJsonData(TagmeData x) {jsonData = x;}
	public TagmeData getJsonData(){return jsonData;}
	public void setResNum(int x) {resNum = x;}
	public int getResNum(){return resNum;}	
	public void setTime(long x) {time = x;}
	public long getTime(){return time;}	
}
