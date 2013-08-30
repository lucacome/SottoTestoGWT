package com.sottotesto.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.Animation;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.Geocoder.Callback;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.InfoWindowOptions;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerImage;
import com.google.maps.gwt.client.MouseEvent;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.Debug;

public class MapController {

	//Map objects
	SimplePanel mapContainer;
	MapOptions options;
	GoogleMap theMap;
	
	//lista links per i marker colorati da mettere sulla mappa
	List<String> markerColoredLinks;
	List<Marker> loadedMarkersOnMap; //lista dei marker gia' caricati nella mappa (obbligatoria per toglierli purtroppo)
	
	
	public void init(){
		Debug.printDbgLine("MapController.java: init()");
		
		//inizializza la lista di colori per i markers
		markerColoredLinks = new ArrayList<String>();
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker.png"); //default color not named (red)
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_purple.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_yellow.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_green.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_orange.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_brown.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_white.png");
		markerColoredLinks.add("http://maps.google.com/mapfiles/marker_black.png");
		
		loadedMarkersOnMap = new ArrayList<Marker>();
		
		//inizializza la mappa google
		mapContainer = new SimplePanel() ;
		options  = MapOptions.create();
		options.setCenter(LatLng.create(35,-40)); 		   //REQUIRED! -- settato cosi' per centrare la mappa
		options.setZoom(2);								   //REQUIRED! -- settato cosi' per vedere tutto il mondo
		options.setMapTypeId(MapTypeId.TERRAIN);		   //REQUIRED! -- settato cosi' per la vista satellite
		options.setDraggable(true);
		options.setMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);
		theMap = GoogleMap.create(mapContainer.getElement(), options);
		mapContainer.setVisible(true);
		//load markers on map
		//loadMarkers(); 
		
		//TEST GOOGLE GEOCODING
		//String retjs = geocodeJS("via treves 3, bologna");
		//Debug.printDbgLine("MapController.java: init(): geocode:" + retjs);
		
	}
	
	
	public void loadSingleDBPQmarkerOnMap(DBPQueryResp dbqMarker){
		String htmlInfo = "";
		htmlInfo =  "Place: "+dbqMarker.getName()+
				"<br>relation: "+dbqMarker.getLink()+
				"<br>entity: "+dbqMarker.getEntity();
		createMarker(LatLng.create(dbqMarker.getLat(), dbqMarker.getLng()), dbqMarker.getName()+"\n("+dbqMarker.getEntity()+")", htmlInfo, 0);
	}
	
	public void loadMarkers(List<DBPQueryResp> mList){
		
		//test markers
		//createMarker(LatLng.create(42.8333, 12.8333), "My \nMarker", "<b>ENTITY</b><br>bla bla",0);
		//createMarker(LatLng.create(52.8333, 22.8333), "My \nMarker\nthe revenge", "<b>ENTITY2</b><br>bla bla",1);
		
		Iterator<DBPQueryResp> mListIterator = mList.iterator();
		DBPQueryResp curMark = new DBPQueryResp();
		String htmlInfo = "";
		String oldEntity = "";
		int colorIndex = -1;
		
		while (mListIterator.hasNext()){
			curMark = mListIterator.next();
			
			if (!curMark.getEntity().equals(oldEntity)){
				oldEntity = curMark.getEntity();
				colorIndex++;
				if (colorIndex >= markerColoredLinks.size()) colorIndex=0;
			}
			
			htmlInfo =  "Place: "+curMark.getName()+
						"<br>relation: "+curMark.getLink()+
						"<br>entity: "+curMark.getEntity();
			createMarker(LatLng.create(curMark.getLat(), curMark.getLng()), curMark.getName()+"\n("+curMark.getEntity()+")", htmlInfo, colorIndex);
		}

	}
	
	//Crea un singolo marker con le opzioni date sulla mappa
	private void createMarker(final LatLng position, String title, final String htmlInfo, int colorIndex){
		Marker m = Marker.create();
		m.setPosition(position);
		m.setTitle(title);
		m.setMap(theMap);		
		m.setAnimation(Animation.DROP);
		m.setFlat(false); //false to show marker shadow? -- No, non va
		m.setIcon(MarkerImage.create(markerColoredLinks.get(colorIndex)));
		m.setShadow(MarkerImage.create("http://maps.gstatic.com/mapfiles/shadow50.png"));
		
		loadedMarkersOnMap.add(m); //add marker to loaded list
		
		m.addClickListener(new Marker.ClickHandler() {			
			@Override
			public void handle(MouseEvent event) {
				InfoWindow info = InfoWindow.create();				
				 final HTMLPanel html = new HTMLPanel((htmlInfo));
		            info.setContent(html.getElement());
		            info.setPosition(position);
		            info.open(theMap);
			}
		});
	}
	
	//restituisce il mapcontainer -- usato da ResultController.java per ridimensionare
	public Widget getMapContainer(){
		if (mapContainer!=null) return mapContainer;
		else return null;
	}
	
	//restituisce la mappa -- usato da ResultController.java per il bugfix della seconda visualizzazione
	public GoogleMap getMap(){
		if (theMap!=null) return theMap;
		else return null;
	}
	
	public void clearMarkerFromMap(){
		Debug.printDbgLine("ResultController.java: clearMarkerFromMap(): Clearing old markers from map...");	
		Iterator<Marker> mListIterator = loadedMarkersOnMap.iterator();
		Marker curMark = Marker.create();
		SimplePanel nullContainerPanel = new SimplePanel();
		GoogleMap gMapNull = GoogleMap.create(nullContainerPanel.getElement());
		
		while (mListIterator.hasNext()){
			curMark = mListIterator.next();
			curMark.setMap(gMapNull);
			}
		
		Debug.printDbgLine("ResultController.java: clearMarkerFromMap(): Cleared "+loadedMarkersOnMap.size()+" markers");
		loadedMarkersOnMap.clear();
		
		theMap.getOverlayMapTypes().setAt(0, null);
	}
	
	//GOOGLE GEOCODING -- currently not used
	private native String geocodeJS(String place)/*-{
		var geocoder;
		var latlong;
		geocoder = new $wnd.google.maps.Geocoder();

		 geocoder.geocode( { 'address': place}, function(results, status) {
    		if (status == $wnd.google.maps.GeocoderStatus.OK) {
    			alert('ok, return: ' + results[0].geometry.location);
    			//latlong = results[0].geometry.location.lat() + '-' + results[0].geometry.location.lng();
    			latlong="prova2";
    			alert('latlong: ' + latlong);
    			return latlong;

    		} else {
      			alert('Geocode was not successful for the following reason: ' + status);
      			latlong="shit";
    		}
  		});
  		
  		return latlong;
	}-*/;

}
