package com.sottotesto.server;

import java.util.List;

public class TagmeDataWrapper {
public class TagmeData {
	String timestamp = null;
	int time = 0;
	String api = null;
	List<Annotation> annotations = null;
	String lang = null;

}
public class Annotation {
	int id = 0;
	String title = null;
	List<String> dbpedia_categories = null;
	int start = 0;
	double rho = 0.0;
	int end = 0;
	String spot = null;
	
}

}