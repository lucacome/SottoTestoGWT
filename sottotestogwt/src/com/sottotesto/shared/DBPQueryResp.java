package com.sottotesto.shared;


public class DBPQueryResp implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 993578307350917897L;
	private String entity;
	private String link;
	private String relation;
	private String name;
	private String abstr;
	private double lat;
	private double lng;
	private boolean success;
	
	//for updating service interface
	private int call;
	private int callMax;



	public DBPQueryResp(){
		entity = "";
		link = "";
		name = "";
		relation = "";
		lat = 0.0;
		lng = 0.0;
		call=0;
		callMax=0;
		success=false;

	}
	public void setEntity(String e){entity = e;}
	public void setAbstract(String e){abstr = e;}
	public void setLink(String l){link = l;}
	public void setName(String n){name = n;}
	public void setRelation(String r){relation = r;}
	public void setGps(double lt, double lg){lat = lt; lng = lg;}
	public void setCallNum(int callNum){call=callNum;}
	public void setMaxCall(int maxCalls){callMax=maxCalls;}
	public String getEntity(){return entity;}
	public String getAbstract(){return abstr;}
	public String getLink(){return link;}
	public String getName(){return name;}
	public String getRelation(){return relation;}
	public double getLat(){return lat;}
	public double getLng(){return lng;}
	public int getCallNum(){return call;}
	public int getMaxCalls(){return callMax;}
	public void setSuccess(boolean x){success=x;}
	public boolean getSuccess(){return success;}


}