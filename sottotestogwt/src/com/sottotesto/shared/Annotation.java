package com.sottotesto.shared;

import java.util.List;

public class Annotation implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6239601318060791164L;
	public int id = 0;
	public String title = null;
	public transient List<String> dbpedia_categories = null;
	public int start = 0;
	public double rho = 0.0;
	public int end = 0;
	public String spot = null;

	public Annotation(){}

}
