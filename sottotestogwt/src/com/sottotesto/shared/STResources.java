package com.sottotesto.shared;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.Strict;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

public interface STResources extends ClientBundle {
	  public static final STResources INSTANCE =  GWT.create(STResources.class);



	  @Source("../resources/prova.html")
	  public TextResource HTMLprova();
	  
	  @Source("../resources/map.html")
	  public TextResource HTMLmap();
	  	  
	  @Source("../resources/map.html")
	  public DataResource HTMLmapData();
	  
	  @Source("../resources/icon_hypertree.png")
	  public ImageResource iconHyperTree();
	  
	  @Source("../resources/icon_treeEntity.png")
	  public ImageResource iconTreeEntity();
	  
	  @Source("../resources/icon_map.png")
	  public ImageResource iconMap();

	}