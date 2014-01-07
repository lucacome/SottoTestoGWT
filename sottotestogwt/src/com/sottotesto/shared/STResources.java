package com.sottotesto.shared;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface STResources extends ClientBundle {
	public static final STResources INSTANCE =  GWT.create(STResources.class);


	@Source("../resources/icon_hypertree.png")
	public ImageResource iconHyperTree();

	@Source("../resources/icon_treeEntity.png")
	public ImageResource iconTreeEntity();

	@Source("../resources/icon_map.png")
	public ImageResource iconMap();

}