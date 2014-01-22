package com.sottotesto.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.maps.gwt.client.Animation;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MarkerImage;
import com.google.maps.gwt.client.MouseEvent;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.Global;

public class MapController {

	//Map objects
	SimplePanel mapContainer;
	MapOptions options;
	GoogleMap theMap;
	InfoWindow info;

	//lista links per i marker colorati da mettere sulla mappa
	List<String> markerColoredLinks;
	List<Marker> allMarkers; //lista dei marker gia' caricati nella mappa (obbligatoria per toglierli purtroppo)
	List<String> entities;			 //stringhe con tutte le entita sulla mappa (usato per i colori)

	//Altro
	String curEntityMap;			//che mappa viene visualizzata al momento ("full map, "entita 1", "entita 2" ecc)
	String fullMap = "Full Map"; //come si chiama il nome di "full map" (cosi se lo cambiamo basta modificare qui)

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

		allMarkers = new ArrayList<Marker>();
		entities = new ArrayList<String>();
		curEntityMap = "";

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
	}


	public void loadSingleDBPQmarkerOnMap(DBPQueryResp dbqMarker){

		String entityName = dbqMarker.getEntity().replace("[","").replace("]", ""); //clear entity name
		entityName = entityName.replaceAll("_", " ");

		//resize abstract
		String sAbstract = dbqMarker.getAbstract();
		int maxAbstractLen = 500;
		if (sAbstract.length()>=maxAbstractLen) sAbstract = sAbstract.substring(0, maxAbstractLen-1) + " [...]";

		//obtain wikipedia link
		String wikiLink="http://en.wikipedia.org/wiki/";
		String dbLink=dbqMarker.getLink();
		wikiLink=wikiLink+dbLink.subSequence(dbLink.lastIndexOf("/")+1, dbLink.length());
		wikiLink="<a href="+wikiLink+" target=\"_blank\">"+wikiLink+"</a>";

		//setup info showed on mapMarker mouse Click
		String htmlInfo =  "<b>Place: </b>"+dbqMarker.getName()+
				"<br><b>Description: </b>"+sAbstract+
				"<br><b>Relation: </b>"+dbqMarker.getRelation()+
				"<br><b>Entity: </b>"+entityName+
				"<br><b>Link: </b>"+wikiLink;

		//find right marker color ---------------------------------
		int colorIndex=0;
		if (entities.indexOf(entityName)>=0){ //se era gia in lista questa entit�
			colorIndex = entities.indexOf(entityName); //prendi il colore corrispondente
		}
		else{
			entities.add(entityName);
			colorIndex = entities.size()-1;
		}
		if (colorIndex >= markerColoredLinks.size()){ //if colorIndex > maxColorNumber
			colorIndex = colorIndex % markerColoredLinks.size(); //pick an already used color
		}
		//---------------------------------------------------------

		//finally create marker with data
		createMarker(LatLng.create(dbqMarker.getLat(), dbqMarker.getLng()), dbqMarker.getName()+"\n("+entityName+")", htmlInfo, colorIndex);
	}

	public void loadMarkers(String entityName){
		Debug.printDbgLine("ResultController.java: loadMarkers(): "+entityName);

		if (info != null) info.close();

		curEntityMap=entityName; //update curren map showed
		int loaded=0;

		//cycle all markers
		for(Marker m : allMarkers){

			if (entityName.equals(fullMap)){ //LOAD ALL MARKERS
				m.setAnimation(Animation.DROP);
				m.setMap(theMap); loaded++;
			}
			else if(m.getTitle().contains(entityName)){ //LOAD MARKERS ONLY FOR SELECTED ENTITY
				m.setAnimation(Animation.DROP);
				m.setMap(theMap); loaded++;
			}
		}		
		Debug.printDbgLine("ResultController.java: loadMarkers(): "+loaded+" markers loaded");
	}
	public void loadMarkersDelayed(final String entityName, final int index){

		if (info != null) info.close();

		curEntityMap=entityName; //update curren map showed

		Timer nextCallTimer = new Timer() { 
		    public void run() { 
		        loadMarkersDelayed(entityName, index+1); 
		    } 
		}; 

		//cycle all markers recursively and delayed
		if (index<allMarkers.size()){
			Debug.printDbgLine("ResultController.java: loadMarkers(): load="+entityName+"; curMarker= "+allMarkers.get(index).getTitle());
			if (entityName.equals(fullMap)){ //LOAD ALL MARKERS
				allMarkers.get(index).setAnimation(Animation.DROP);
				allMarkers.get(index).setMap(theMap);
				nextCallTimer.schedule(Global.getMapMarkerDelay());
			}
			else if(allMarkers.get(index).getTitle().contains("("+entityName+")")){ //LOAD MARKERS ONLY FOR SELECTED ENTITY
				allMarkers.get(index).setAnimation(Animation.DROP);
				allMarkers.get(index).setMap(theMap);
				nextCallTimer.schedule(Global.getMapMarkerDelay());
			}
			else{ //skip marker
				nextCallTimer.schedule(0);
			}
		}		
	}

	//Crea un singolo marker con le opzioni date sulla mappa
	private void createMarker(final LatLng position, String title, final String htmlInfo, int colorIndex){

		//to hide markers
		SimplePanel nullContainerPanel = new SimplePanel();
		GoogleMap gMapNull = GoogleMap.create(nullContainerPanel.getElement());

		Marker m = Marker.create();
		m.setPosition(position);
		m.setTitle(title);
		if (curEntityMap.equals(fullMap)) m.setMap(theMap); //if i've selected "full map" -> show right away
		else if(m.getTitle().contains(curEntityMap)) m.setMap(theMap); //if i've selected "map for x" and this is an x marker -> show right away
		else m.setMap(gMapNull); // if it's a marker for a different entity from the one visualized right now -> don't show
		m.setAnimation(Animation.DROP);
		m.setFlat(false); //false to show marker shadow? -- No, non va
		m.setIcon(MarkerImage.create(markerColoredLinks.get(colorIndex)));
		m.setShadow(MarkerImage.create("http://maps.gstatic.com/mapfiles/shadow50.png"));
		allMarkers.add(m); //add marker to loaded list

		m.addClickListener(new Marker.ClickHandler() {			
			@Override
			public void handle(MouseEvent event) {
				if (info != null) info.close();
				info = InfoWindow.create();				
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
		Iterator<Marker> mListIterator = allMarkers.iterator();
		Marker curMark = Marker.create();
		SimplePanel nullContainerPanel = new SimplePanel();
		GoogleMap gMapNull = GoogleMap.create(nullContainerPanel.getElement());

		while (mListIterator.hasNext()){
			curMark = mListIterator.next();
			curMark.setMap(gMapNull);
		}

		Debug.printDbgLine("ResultController.java: clearMarkerFromMap(): Cleared "+allMarkers.size()+" markers");		
		theMap.getOverlayMapTypes().setAt(0, null);
	}

}
