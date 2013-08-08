package com.sottotesto.shared;

public class TreeData {
	
	public static final class CLICK_ACTIONS {
		public static final String NOTHING = "nothing";
		  public static final String SHOWGRAPH_FD = "showGraphFD";
		  public static final String SHOWGRAPH_HT = "showGraphHT";	
		  public static final String SHOWMAP = "showMap";
		}
	
	private String id;
	private String name;
	private String clickAction;
	private String jsonFD;
	private String jsonHT;
	
	public TreeData(String id, String name){
		this.id = id;
		this.name = name;
		this.clickAction = CLICK_ACTIONS.NOTHING;
		this.jsonFD = "";
		this.jsonHT = "";
		
		Debug.printDbgLine("TreeData.java:new data: "+id+" - "+name);
	}
	
	public TreeData(String id, String name, String clickAction){
		this.id = id;
		this.name = name;
		this.clickAction = clickAction;
		this.jsonFD = "";
		this.jsonHT = "";
		
		Debug.printDbgLine("TreeData.java:new data: "+id+" - "+name+" ["+clickAction+"]");
	}
	
	

	
	public String getId() {return id;}
	public void setId(String id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getClickAction() {return clickAction;}
	public void setClickAction(String clickAction) {this.clickAction = clickAction;}
	public String getJsonFD() {return jsonFD;}
	public void setJsonFD(String jsonFD) {this.jsonFD = jsonFD;}
	public String getJsonHT() {return jsonHT;}
	public void setJsonHT(String jsonHT) {this.jsonHT = jsonHT;}
	
	
}


