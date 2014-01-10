package com.sottotesto.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.maps.gwt.client.LatLng;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.STResources;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.TreeData;
import com.sottotesto.shared.TreeDataProperties;
import com.sottotesto.shared.Utility;


/* USAGE:
 * ResultController rc = new ResultController();
 * rc.init();
 */
public class ResultController {

	private int panelMaxWidth, panelMaxHeight;

	private ContentPanel panel, centerPanel;
	private BorderLayoutContainer border;
	private VBoxLayoutContainer lcwest;
	private BorderLayoutData west;
	private BoxLayoutData vBoxData;
	private HTML defaultCenterHTML;
	private String HTMLsearchSomethingString="<div class=\"result_searchSomethingString\">Effettua una ricerca!</div>";
	//private String HTMLloadIconString="<div class=\"result_loadingGifContainer\"><img class=\"result_loadingGif\" src='loading.gif'/></div>";
	private String HTMLerrorString="<div class=\"result_errorString\">E' avvenuto un errore, rieffettua la tua ricerca!</div>";

	//infovis data
	private InfovisController infovisC;
	private List<String> listFD;
	private final GraphServiceAsync GraphService = GWT.create(GraphService.class);
	private final GHTServiceAsync GHTService = GWT.create(GHTService.class);

	//map data
	private MapController mapC;
	List<DBPQueryResp> markerList; //Lista di marker ottenuti da dbpedia
	private boolean firstMapLoad;

	//tree data
	private TreeDataProperties treeProperties;
	private TreeStore<TreeData> treeStore;
	private Tree <TreeData, String> tree;
	private FlowLayoutContainer treeContainer;
	private ButtonBar treeButtonBar;
	private TextButton treeExpandButton, treeCollapseButton;
	private StoreFilterField<TreeData> treeFilter;

	//search phrase data
	String searchedPhrase;	
	
	private TagmeResponse tagmeResp;



	public void init(){
		Debug.printDbgLine("ResultController.java: init()");

		markerList = new ArrayList<DBPQueryResp>();
		listFD = new ArrayList<String>();

		panelMaxWidth = RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*3/100);
		int bottomMargin = 5;
		panelMaxHeight = RootPanel.get().getOffsetHeight()-
				RootPanel.get("searchContainer").getOffsetHeight()-
				RootPanel.get("servicesContainer").getOffsetHeight()-
				bottomMargin;

		if (panel != null) panel.clear();
		panel = new ContentPanel();
		panel.setHeadingText("Risultati");	
		panel.setId("resultPanel");
		panel.setWidth(panelMaxWidth);
		panel.setHeight(panelMaxHeight+"px");
		panel.setCollapsible(false);

		if (border != null) border.clear();
		border = new BorderLayoutContainer();
		panel.setWidget(border);

		if (lcwest != null) lcwest.clear();
		lcwest = new VBoxLayoutContainer();
		lcwest.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		west = new BorderLayoutData(150);
		west.setMargins(new Margins(5));

		border.setWestWidget(lcwest, west);

		if (centerPanel != null) centerPanel.clear();
		centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		centerPanel.setId("centerPanel");
		defaultCenterHTML = new HTML();
		defaultCenterHTML.setStyleName("result_htmlContainer");
		defaultCenterHTML.setHTML(HTMLsearchSomethingString);
		centerPanel.add(defaultCenterHTML);			


		MarginData center = new MarginData(new Margins(5));		
		border.setCenterWidget(centerPanel, center);
		vBoxData = new BoxLayoutData(new Margins(5, 5, 5, 5));
		vBoxData.setFlex(1);			

		mapC = new MapController(); //initialize map controller	
		mapC.init();	
		firstMapLoad=true;
	}

	public void reInit(){
		lcwest.clear(); //remove tree
		showLoading(true); //show loading gif
	}

	public void showError(){
		lcwest.clear(); //remove tree
		centerPanel.clear();	// clear centerpanel contents
		centerPanel.add(new HTML(HTMLerrorString));
	}

	public void showLoading(boolean visible){
		//centerPanel.clear();	// clear centerpanel contents
		//centerPanel.add(new HTML(HTMLloadIconString));
		
		if(visible)	Utility.showLoadingBar();
		else Utility.hideLoadingBar();
		
	}

	public void loadTree(TreeStore<TreeData> ts){

		Debug.printDbgLine("ResultController.java: initTree()");

		treeContainer = new FlowLayoutContainer();
		treeContainer.setId("treeContainer");
		treeContainer.addStyleName("margin-10");

		//TREE EXPAND/COLLAPSE BUTTONS
		treeButtonBar = new ButtonBar();
		treeExpandButton = new TextButton();
		treeCollapseButton = new TextButton();
		treeExpandButton.setText("Expand All");
		treeExpandButton.addSelectHandler(new SelectHandler() {	 
			@Override
			public void onSelect(SelectEvent event) {tree.expandAll();}
		});
		treeCollapseButton.setText("Collapse All");
		treeCollapseButton.addSelectHandler(new SelectHandler() {	 
			@Override
			public void onSelect(SelectEvent event) {tree.collapseAll();}
		});    
		treeButtonBar.add(treeExpandButton); 
		treeButtonBar.add(treeCollapseButton);
		treeButtonBar.setLayoutData(new MarginData(4));


		//TREE DATA LOADER
		treeProperties = GWT.create(TreeDataProperties.class); // Generate the key provider and value provider for the Data class
		treeStore = ts;
		tree = new Tree<TreeData, String>(treeStore, treeProperties.name());
		tree.setId("tree");
		tree.setStylePrimaryName("treePrimStyle");

		//TREE CLICK HANDLER		
		tree.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);
		tree.getSelectionModel().addSelectionHandler(new SelectionHandler<TreeData>() {
			public void onSelection(SelectionEvent<TreeData> event) {
				TreeData treeDataSelected = event.getSelectedItem();
				handleTreeClick(treeDataSelected);
				//Info.display("Tree Handler", mnu.getName());
			}
		});

		//TREE FILTERING
		treeFilter = new StoreFilterField<TreeData>() {			 
			@Override
			protected boolean doSelect(Store<TreeData> store, TreeData parent, TreeData item, String filter) {	 
				String name = item.getName();
				name = name.toLowerCase();
				if (name.startsWith(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		treeFilter.bind(treeStore);


		//TREE ICON PROVIDER
		tree.setIconProvider( new IconProvider<TreeData>()
				{
			@Override
			public ImageResource getIcon( TreeData model )
			{
				if ( model.getName().equals("ForceDirected Graph") ) return STResources.INSTANCE.iconHyperTree();
				if ( model.getName().equals("Hypertree Graph") ) return STResources.INSTANCE.iconHyperTree();
				if ( model.getName().equals("Knowledge Map") ) return STResources.INSTANCE.iconMap();
				else return STResources.INSTANCE.iconTreeEntity();
				//else return null;
			}
				} );

		//populate treecontainer
		treeContainer.add(treeFilter);    
		treeContainer.add(treeButtonBar);
		treeContainer.add(tree);

		lcwest.add(treeContainer);

		showMap("Mappa completa");
	}


	private void handleTreeClick(TreeData treeDataSelected){
		Debug.printDbgLine("ResultController.java: handleTreeClick("+treeDataSelected.getName()+")");

		if (!treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.NOTHING)){
	
			if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWGRAPH_FD)) {
				showLoading(true);
				infovisC = new InfovisController();	// initialize new infovis controller
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(infovisC.init()); // add the graph in centerpanel
				infovisC.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
				infovisC.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
				infovisC.showGraph(treeDataSelected.getJsonFD(), InfovisController.GRAPH_TYPE.FORCEDIRECTED);
				showLoading(false);
			}
			else if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWJOINEDGRAPH_FD)) {				
				GraphEntityChooser.showDialog(tagmeResp.getTitleTag(), this); //mostra il dialog per scegliere le entita' da confrontare
			}
			else if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWGRAPH_HT)) {
				GraphLinksChooser.showDialog(treeDataSelected.getJsonHT(), this);
			}
			else if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWMAP)) {
				showLoading(true);
				showMap(treeDataSelected.getName());
			}
			else {
				Info.display("WARNING", "ClickAction sconosciuta (non dovrebbe succedere ... )");
				showError(); //show red error on center page
			}

			tree.getSelectionModel().deselectAll(); //deselect to allow reclick of same treedata
		}		
	}

	public void showMap(String mapName){
		centerPanel.clear(); 				// clear centerpanel contents	
		centerPanel.setWidget(mapC.getMapContainer()); // add the graph in centerpanel
		mapC.getMapContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt map size to centerpanel size
		mapC.getMapContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");

		//BUGFIX - the map doesn't show correctly for the second time, this is the fix (by lollo... fuck stackoverflow!)
		mapC.getMap().triggerResize();
		if(firstMapLoad){
			mapC.getMap().setCenter(LatLng.create(35,-40));
			firstMapLoad=false;
		}
		//END BUGFIX	
		
		if (mapC != null) mapC.clearMarkerFromMap();  //hide all markers from map
		showLoading(false); //hide loading
		//mapC.loadMarkers(mapName); //load only right markers, depending on selected map
		mapC.loadMarkersDelayed(mapName, 0);
	}


//questa viene chiamata da GraphEntityChooser quando l'utente seleziona piu entita per un confronto
public void prepareToCallGraphService(List<String> selectedEntities){
	if (selectedEntities.size()<2){
		Info.display("WARNING", "Hai selezionato troppe poche entita' per fare un confronto!");
	}
	else{
		showLoading(true);
		List<String> selectedDBPlinks = new ArrayList<String>();
		for (String s : selectedEntities){
			selectedDBPlinks.add("http://dbpedia.org/resource/"+s);
		}
		Debug.printDbgLine("Sottotestogwt.java: prepareToCallGraphService(): calling graphService...");
		callGraphService(listFD, selectedDBPlinks);
	}
}	
private void callGraphService(List<String> listFD, List<String> selectedDbpLinks){
		
		GraphService.sendToServer(listFD, selectedDbpLinks, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printDbgLine("Sottotestogwt.java: callGraphService(): onFailure()");
				showLoading(false);
			}

			public void onSuccess(String result) {
				Debug.printDbgLine("Sottotestogwt.java: callGraphService(): onSuccess()");
				Debug.printDbgLine(result);
				
				// show the graph with the result json
				infovisC = new InfovisController();	// initialize new infovis controller
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(infovisC.init()); // add the graph in centerpanel
				infovisC.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
				infovisC.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");				
				infovisC.showGraph(result, InfovisController.GRAPH_TYPE.FORCEDIRECTED);		
				showLoading(false); //hide loading
			}
			});
		
	}
	

	//questa viene chiamata da GraphLinkChooser quando l'utente seleziona piu entita per un confronto
	public void prepareToCallGHTService(String jsonHT, List<String> selectedLinks){
		if (selectedLinks.size()==0){
			Info.display("WARNING", "Non hai selezionato entit� da visualizzare!");
		}
		else{
			showLoading(true);
			List<String> selectedlinksToXXX = new ArrayList<String>();
			for (String s : selectedLinks){
				selectedlinksToXXX.add("linksTo"+s);
			}
			Debug.printDbgLine("Sottotestogwt.java: prepareToCallGraphService(): calling graphService...");
			callGHTService(jsonHT, selectedlinksToXXX);
		}
	}
	private void callGHTService(String jsonHT, List<String> selectedLinks){			
		GHTService.sendToServer(jsonHT, selectedLinks, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printDbgLine("Sottotestogwt.java: callGHTService(): onFailure()");
				showLoading(false);
			}

			public void onSuccess(String result) {
				Debug.printDbgLine("Sottotestogwt.java: callGHTService(): onSuccess()");
				Debug.printDbgLine(result);
				
				// show the graph with the result json
				infovisC = new InfovisController();	// initialize new infovis controller
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(infovisC.init()); // add the graph in centerpanel
				infovisC.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
				infovisC.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
				infovisC.showGraph(result, InfovisController.GRAPH_TYPE.HYPERTREE);
				showLoading(false); //hide loading
			}
			});
		
	}

	public void setJsonFD(String jdata){

		//Debug.printDbgLine("JSONDF="+jsonFD);	
	}
	public void setJsonHT(String jdata){

		//Debug.printDbgLine("JSONHT="+jsonHT);	
	}


	public Tree<TreeData, String> getTree(){
		if (tree!=null) return tree;
		else return null;
	}

	public ContentPanel getPanel(){
		if (panel!=null) return panel;
		else return null;
	}


	public void addMarkerToList(DBPQueryResp m){
		markerList.add(m);
	}
	public void addDBpediaMarkerSingleToMap(DBPQueryResp dbpqMarker ){
		if (mapC != null) mapC.loadSingleDBPQmarkerOnMap(dbpqMarker);
	}
	
	public void setListFD(List<String> listFDnew){
		listFD = new ArrayList<String>();
		listFD = listFDnew;
	}
	
	public void setTagmeResp(TagmeResponse tgmRsp){
		tagmeResp = new TagmeResponse();
		tagmeResp = tgmRsp;
	}
}
