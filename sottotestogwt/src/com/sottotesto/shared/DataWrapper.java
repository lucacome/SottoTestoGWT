package com.sottotesto.shared;

import java.util.List;

public class DataWrapper {
	
	
public class TagmeData {
	String timestamp = null;
	int time = 0;
	String api = null;
	public List<Annotation> annotations = null;
	String lang = null;

}
public class Annotation {
	public int id = 0;
	public String title = null;
	public List<String> dbpedia_categories = null;
	public int start = 0;
	public double rho = 0.0;
	public int end = 0;
	public String spot = null;
	
}


}