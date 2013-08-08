package com.sottotesto.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.sottotesto.shared.Debug;

public class MapController {

	SimplePanel mapContainer;
	MapOptions options;
	GoogleMap theMap;
	
	public Widget init(){
		Debug.printDbgLine("InfovisController.java: init()");
		
		mapContainer = new SimplePanel() ;
		//mapContainer.setSize("1000px", "500px");
		options  = MapOptions.create();
		options.setCenter(LatLng.create(39.509, -98.434)); 
		options.setZoom(6);
		options.setMapTypeId(MapTypeId.SATELLITE);
		options.setDraggable(true);
		options.setMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		theMap = GoogleMap.create(mapContainer.getElement(), options) ;
		mapContainer.setVisible(true);
		Marker marker = Marker.create();
		marker.setPosition(LatLng.create(42.8333, 12.8333));
		marker.setMap(theMap);
		
		return mapContainer;
	}
	
	public Widget getMapContainer(){
		if (mapContainer!=null) return mapContainer;
		else return null;
	}

}
