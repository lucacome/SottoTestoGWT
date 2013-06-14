package com.sottotesto.server;

import java.util.HashMap;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.ArrayListMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;







public class JData {
	//public static JsonObject jdata = new JsonObject();
	public String title = null;
	public static String type = null;
//	public Map<String, List<String>> linkmap = ArrayListMultimap.create();
	public Multimap<String, String> linkmap = ArrayListMultimap.create();
	public Map<String, Multimap<String,String>> tag; 
	
	public JData() {
		// TODO Auto-generated constructor stub
		//linkmap =
		tag = new HashMap<String,Multimap<String,String>>();
	}
	public void CleanLink() {
		linkmap = ArrayListMultimap.create();
	}
	public void setLink(Multimap<String,String> x) {linkmap = x;}
	public Multimap<String,String> getLink(){return linkmap;}
	public void setTag(Map<String,Multimap<String,String>> x) {tag = x;}
	public Map<String,Multimap<String,String>> getTag(){return tag;}	


}
