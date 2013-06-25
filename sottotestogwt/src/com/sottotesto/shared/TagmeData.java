package com.sottotesto.shared;

import java.util.List;



public class TagmeData implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5636155407078511943L;
	String timestamp = null;
	int time = 0;
	String api = null;
	public List<Annotation> annotations = null;
	String lang = null;

	public TagmeData(){}


}

