package com.sottotesto.client;


import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.maps.gwt.client.LatLng;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
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
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.STResources;
import com.sottotesto.shared.TreeData;
import com.sottotesto.shared.TreeDataProperties;


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
	private String HTMLloadIconString="<p style=\"padding:10px;color:#556677;font-size:11px;\"><img src='loading.gif'/></p>";
	private String HTMLselectSomethingString="<p style=\"padding:10px;color:#556677;font-size:11px;\">Seleziona una vista dall'elenco a sinistra</p>";
	private String HTMLerrorString="<p style=\"padding:10px;color:red;font-size:11px;\">E' avvenuto un errore, rieffettua la tua ricerca!</p>";
	
	//infovis data
	private InfovisController infovisC;
	private String jsonFD; //json string for forcedirected graph
	private String jsonHT;
	
	//map data
	private MapController mapC;
	
	//tree data
	private TreeDataProperties treeProperties;
	private SimpleSafeHtmlCell<String> cell;
	private TreeStore<TreeData> treeStore;
	private Tree <TreeData, String> tree;
	private FlowLayoutContainer treeContainer;
	private ButtonBar treeButtonBar;
	private TextButton treeExpandButton, treeCollapseButton;
	private StoreFilterField<TreeData> treeFilter;

	public void init(){
		Debug.printDbgLine("ResultController.java: init()");
		
		panelMaxWidth = RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*5/100);
		panelMaxHeight = (RootPanel.get().getOffsetHeight()) + (RootPanel.get().getOffsetHeight()*40/100);
		
		panel = new ContentPanel();
		panel.setHeadingText("Risultati");		
		panel.setPixelSize(panelMaxWidth, panelMaxHeight);
		panel.setCollapsible(false);

		border = new BorderLayoutContainer();
		panel.setWidget(border);

		lcwest = new VBoxLayoutContainer();
		lcwest.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		west = new BorderLayoutData(150);
		west.setMargins(new Margins(5));

		border.setWestWidget(lcwest, west);

		centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		centerPanel.setId("centerPanel");
		defaultCenterHTML = new HTML();
		defaultCenterHTML.setHTML("<p style=\"padding:10px;color:#556677;font-size:11px;\">Effettua Una ricerca!</p>");
		centerPanel.add(defaultCenterHTML);
				


		MarginData center = new MarginData(new Margins(5));

		border.setCenterWidget(centerPanel, center);

		vBoxData = new BoxLayoutData(new Margins(5, 5, 5, 5));
		vBoxData.setFlex(1);		
		
	}

	public void reInit(){
		lcwest.clear(); //remove tree
		showLoading(); //show loading gif
	}
	
	public void showError(){
		lcwest.clear(); //remove tree
		centerPanel.clear();	// clear centerpanel contents
		centerPanel.add(new HTML(HTMLerrorString));
	}
	
	public void showLoading(){
		centerPanel.clear();	// clear centerpanel contents
		centerPanel.add(new HTML(HTMLloadIconString));
	}
	
	public void loadTree(TreeStore<TreeData> ts){

		Debug.printDbgLine("ResultController.java: initTree()");
		
		treeContainer = new FlowLayoutContainer();
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
		/*
		treeStore = new TreeStore<TreeData>(treeProperties.id()); // Create the store that the contains the data to display in the tree
		TreeData r1 = new TreeData("1","ForceDirected Graph"); r1.setClickAction(TreeData.CLICK_ACTIONS.SHOWGRAPH_FD); r1.setJsonFD(jsonFD);
		treeStore.add(r1);
		treeStore.add(r1, new TreeData("3","Tag1"));
		treeStore.add(r1, new TreeData("4","Tag1"));
		TreeData r2 = new TreeData("2","Hypertree Graph"); r2.setClickAction(TreeData.CLICK_ACTIONS.SHOWGRAPH_HT); r2.setJsonHT(jsonHT);
		treeStore.add(r2);
		TreeData r3 = new TreeData("5","Maps");
		treeStore.add(r3);
		treeStore.add(r3, new TreeData("6","Tag3"));
		treeStore.add(r3, new TreeData("7","Tag4"));	
		*/
		treeStore = ts;
		tree = new Tree<TreeData, String>(treeStore, treeProperties.name());
		
		//TREE CLICK HANDLER		
		tree.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);
	    tree.getSelectionModel().addSelectionHandler(new SelectionHandler<TreeData>() {

	        public void onSelection(SelectionEvent<TreeData> event) {
	        	TreeData treeDataSelected = event.getSelectedItem();
	        	handleTreeClick(treeDataSelected);
	            //Info.display("Tree Handler", mnu.getName());
	        }
	    });
	    /*
	     * cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(), "click") {
			@Override
			public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event,
					ValueUpdater<String> valueUpdater) {
				super.onBrowserEvent(context, parent, value, event, valueUpdater);
				if ("click".equals(event.getType())) {
					//Info.display("Click", "You clicked \"" + value + "\"!");
					handleTreeClick(value);
				}
			}
		};
		tree.setCell(cell);
	     */
		
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
			            if ( model.getName().equals("Map") ) return STResources.INSTANCE.iconMap();
			            else return STResources.INSTANCE.iconTreeEntity();
			            //else return null;
			         }
			      } );
		
		//populate treecontainer
		treeContainer.add(treeFilter);    
		treeContainer.add(treeButtonBar);
		treeContainer.add(tree);
		
		lcwest.add(treeContainer);
		
		centerPanel.clear();	// clear centerpanel contents
		centerPanel.add(new HTML(HTMLselectSomethingString));
		
	}

	
	private void handleTreeClick(TreeData treeDataSelected){
		Debug.printDbgLine("ResultController.java: handleTreeClick("+treeDataSelected.getName()+")");
		
		if (!treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.NOTHING)){
			
			//show loading gif
			showLoading();
			
			if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWGRAPH_FD)) {
				infovisC = new InfovisController();	// initialize new infovis controller
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(infovisC.init()); // add the graph in centerpanel
				infovisC.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
				infovisC.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
				infovisC.showGraph(treeDataSelected.getJsonFD(), InfovisController.GRAPH_TYPE.FORCEDIRECTED);
			}
			else if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWGRAPH_HT)) {
				infovisC = new InfovisController();	// initialize new infovis controller
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(infovisC.init()); // add the graph in centerpanel
				infovisC.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
				infovisC.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
				infovisC.showGraph(treeDataSelected.getJsonHT(), InfovisController.GRAPH_TYPE.HYPERTREE);
			}
			else if (treeDataSelected.getClickAction().equals(TreeData.CLICK_ACTIONS.SHOWMAP)) {
				mapC = new MapController(); //initialize map controller				
				centerPanel.clear(); 				// clear centerpanel contents	
				centerPanel.setWidget(mapC.init()); // add the graph in centerpanel
				mapC.getMapContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt map size to centerpanel size
				mapC.getMapContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
				
				//BUGFIX - the map doesn't show correctly for the second time, this is the fix
				mapC.getMap().triggerResize();
				mapC.getMap().setCenter(LatLng.create(35,-40)); 
				//END BUGFIX
			}
			else {
				Info.display("WARNING", "ClickAction sconosciuta (non dovrebbe succedere ... )");
				showError(); //show red error on center page
			}
						
			tree.getSelectionModel().deselectAll(); //deselect to allow reclick of same treedata
		}		
	}

	
	public void setJsonFD(String jdata){

			jsonFD = jdata;

	//Debug.printDbgLine("JSONDF="+jsonFD);	
	}
	public void setJsonHT(String jdata){

			jsonHT = jdata;

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
}
