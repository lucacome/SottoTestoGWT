package com.sottotesto.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;


public class JData {
	public String id = null;
	public String name = null;
	public transient String type = null;
	public Map<String,String> data;
	public List<Map<String,String>> adjacencies;
	private transient Multimap<String, String> linkmap;


	public JData() {
		data = new HashMap<String, String>();
		data.clear();
		adjacencies = new ArrayList<Map<String, String>>();
		linkmap = HashMultimap.create();
		linkmap.clear();
	}

	public void setLink(Multimap<String,String> x) {linkmap = x;}
	public Multimap<String,String> getLink(){return linkmap;}


}
