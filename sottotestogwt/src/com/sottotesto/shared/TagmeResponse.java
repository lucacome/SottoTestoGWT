package com.sottotesto.shared;

import java.util.List;

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
	private double maxrho;
	private long time;
	private List<String> titleTag;
	private List<String> spotTag;
	private List<String> titleSkipped;
	private List<String> spotSkipped;

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
		titleTag = null;
		maxrho = 0.1;
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
	public void setRho(double x) {maxrho = x;}
	public double getRho(){return maxrho;}
	public void setTime(long x) {time = x;}
	public long getTime(){return time;}	
	public void setTitleTag(List<String> x) {titleTag = x;}
	public List<String> getTitleTag(){return titleTag;}
	public void setSpotTag(List<String> x) {spotTag = x;}
	public List<String> getSpotTag(){return spotTag;}

	public void setTitleSkipped(List<String> x) {titleSkipped = x;}
	public List<String> getTitleSkipped(){return titleSkipped;}
	public void setSpotSkipped(List<String> x) {spotSkipped = x;}
	public List<String> getSpotSkipped(){return spotSkipped;}
}
