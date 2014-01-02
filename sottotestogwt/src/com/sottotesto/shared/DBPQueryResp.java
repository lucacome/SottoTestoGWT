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



public DBPQueryResp(){
	entity = "";
	link = "";
	name = "";
	relation = "";
	lat = 0.0;
	lng = 0.0;
	
}
public void setEntity(String e){entity = e;}
public void setAbstract(String e){abstr = e;}
public void setLink(String l){link = l;}
public void setName(String n){name = n;}
public void setRelation(String r){relation = r;}
public void setGps(double lt, double lg){lat = lt; lng = lg;}
public String getEntity(){return entity;}
public String getAbstract(){return abstr;}
public String getLink(){return link;}
public String getName(){return name;}
public String getRelation(){return relation;}
public double getLat(){return lat;}
public double getLng(){return lng;}


}