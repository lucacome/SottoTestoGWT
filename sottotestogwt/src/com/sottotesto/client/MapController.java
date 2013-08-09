package com.sottotesto.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerOptions;
import com.google.maps.gwt.client.MarkerShape;
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
		options.setCenter(LatLng.create(35,-40)); 		   //REQUIRED!
		options.setZoom(2);								   //REQUIRED!
		options.setMapTypeId(MapTypeId.TERRAIN);		   //REQUIRED!
		options.setDraggable(true);
		options.setMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		theMap = GoogleMap.create(mapContainer.getElement(), options) ;
		mapContainer.setVisible(true);
		Marker marker = Marker.create();
		marker.setPosition(LatLng.create(42.8333, 12.8333));
		marker.setTitle("Marker");
		marker.setMap(theMap);
		
		return mapContainer;
	}
	
	public Widget getMapContainer(){
		if (mapContainer!=null) return mapContainer;
		else return null;
	}
	
	private void geocode (String place){
		
	}

	private native void geocodeJS(String place)/*-{
		var geocoder;
		geocoder = new google.maps.Geocoder();
		
		 geocoder.geocode( { 'address': address}, function(results, status) {
    		if (status == google.maps.GeocoderStatus.OK) {
      			map.setCenter(results[0].geometry.location);
      			var marker = new google.maps.Marker({
          		map: map,
          		position: results[0].geometry.location});
    } else {
      alert('Geocode was not successful for the following reason: ' + status);
    }
  });
}-*/;
}
