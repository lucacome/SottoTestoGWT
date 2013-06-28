package com.sottotesto.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;


public class JData {
	//public static JsonObject jdata = new JsonObject();
	public String id = null;
	public String name = null;
	public transient String type = null;
	public List<Map<String,String>> data;
//	public Map<String, String> temp = new HashMap<String, String>();
	public List<Map<String,String>> adjacencies;
//	public Map<String, List<String>> linkmap = ArrayListMultimap.create();
	private transient Multimap<String, String> linkmap;
//	private Map<String, Multimap<String,String>> tag; 
	
	public JData() {
		// TODO Auto-generated constructor stub
		//linkmap =
		data = new ArrayList<Map<String, String>>();
		adjacencies = new ArrayList<Map<String, String>>();
		linkmap = HashMultimap.create();
		linkmap.clear();
//		tag = new HashMap<String,Multimap<String,String>>();
//		tag.clear();
		
	}
	
//	public static class adjacencies {
//		
//		public String[] nodeTo;
//		public String nodeFrom;
//		public String data;
//		
//		public adjacencies(){
//			nodeTo = new String[10];
//			nodeFrom = null;
//			data = null;
//			
//		}
//		//public void setNodeTo(String x) {nodeTo = x;}
//		
//	}

	public void setLink(Multimap<String,String> x) {linkmap = x;}
	public Multimap<String,String> getLink(){return linkmap;}
//	public void setTag(Map<String,Multimap<String,String>> x) {tag = x;}
//	public Map<String,Multimap<String,String>> getTag(){return tag;}	


}
