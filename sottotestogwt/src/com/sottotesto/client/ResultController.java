package com.sottotesto.client;


import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.text.shared.SimpleSafeHtmlRenderer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
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


/* USAGE:
 * ResultController rc = new ResultController();
 * rc.init();
 */
public class ResultController {

	private ContentPanel panel, centerPanel;
	private BorderLayoutContainer border;
	private VBoxLayoutContainer lcwest;
	private BorderLayoutData west;
	private BoxLayoutData vBoxData;
	private HTML defaultCenterHTML;

	//infovis data
	private InfovisController infovis;
	String jsonFD; //json string for forcedirected graph

	//tree data
	DataProperties dp;
	SimpleSafeHtmlCell<String> cell;
	TreeStore<Data> treeStore;
	Tree<Data, String> tree;
	FlowLayoutContainer treeContainer;
	ButtonBar treeButtonBar;
	TextButton treeExpandButton, treeCollapseButton;
	StoreFilterField<Data> treeFilter;

	public void init(){
		Debug.printDbgLine("ResultController.java: init()");
		panel = new ContentPanel();
		panel.setHeadingText("Risultati");		
		panel.setPixelSize(RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*5/100), 700);
		panel.setCollapsible(true);

		border = new BorderLayoutContainer();
		panel.setWidget(border);

		lcwest = new VBoxLayoutContainer();
		lcwest.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCH);

		west = new BorderLayoutData(150);
		west.setMargins(new Margins(5));

		border.setWestWidget(lcwest, west);

		centerPanel = new ContentPanel();
		centerPanel.setHeaderVisible(false);
		defaultCenterHTML = new HTML();
		defaultCenterHTML.setHTML("<p style=\"padding:10px;color:#556677;font-size:11px;\">Seleziona una vista dall'elenco a sinistra</p>");
		centerPanel.add(defaultCenterHTML);
				


		MarginData center = new MarginData(new Margins(5));

		border.setCenterWidget(centerPanel, center);

		vBoxData = new BoxLayoutData(new Margins(5, 5, 5, 5));
		vBoxData.setFlex(1);

		initTree();
		lcwest.add(treeContainer);

		// Add the panel
		RootPanel.get("results").add(panel);
	}

	public void initTree(){

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
		dp = GWT.create(DataProperties.class); // Generate the key provider and value provider for the Data class
		treeStore = new TreeStore<Data>(dp.key()); // Create the store that the contains the data to display in the tree
		Data r1 = new Data(0, "ForceDirected Graph", "value1");
		treeStore.add(r1);
		treeStore.add(r1, new Data(1, "Tag1", "value2"));
		treeStore.add(r1, new Data(2, "Tag2", "value3"));
		Data r2 = new Data(3, "Hypertree Graph", "valueboh");
		treeStore.add(r2);
		Data r3 = new Data(4, "Maps", "value4");
		treeStore.add(r3);
		treeStore.add(r3, new Data(5, "Tag3", "value5"));
		treeStore.add(r3, new Data(6, "Tag4", "value6"));	
		tree = new Tree<Data, String>(treeStore, dp.name());
		
		//TREE CLICK HANDLER
		cell = new SimpleSafeHtmlCell<String>(SimpleSafeHtmlRenderer.getInstance(), "click") {
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
		
		//TREE FILTERING
		treeFilter = new StoreFilterField<Data>() {			 
		      @Override
		      protected boolean doSelect(Store<Data> store, Data parent, Data item, String filter) {	 
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
		tree.setIconProvider( new IconProvider<Data>()
			      {
			         @Override
			         public ImageResource getIcon( Data model )
			         {
			            if ( model.getName().equals("ForceDirected Graph") ) return STResources.INSTANCE.iconHyperTree();
			            if ( model.getName().equals("Hypertree Graph") ) return STResources.INSTANCE.iconHyperTree();
			            if ( model.getName().equals("Maps") ) return STResources.INSTANCE.iconMap();
			            else return STResources.INSTANCE.iconTreeEntity();
			            //else return null;
			         }
			      } );
		
		//populate treecontainer
		treeContainer.add(treeFilter);    
		treeContainer.add(treeButtonBar);
		treeContainer.add(tree);
	}

	
	public void handleTreeClick(String value){
		Debug.printDbgLine("ResultController.java: handleTreeClick("+value+")");
		
		if (value.equals("ForceDirected Graph")) {
			Debug.printDbgLine("ResultController.java: handleTreeClick(): showing fd graph...");
			infovis = new InfovisController();	// initialize new infovis controller
			centerPanel.clear(); 				// clear centerpanel contents
			centerPanel.setWidget(infovis.init()); // add the graph in centerpanel
			infovis.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
			infovis.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
			infovis.showGraph(jsonFD, "forcedirected"); //load json to graph
		}
		else if (value.equals("Hypertree Graph")) {
			Debug.printDbgLine("ResultController.java: handleTreeClick(): showing ht graph...");
			infovis = new InfovisController();	// initialize new infovis controller
			centerPanel.clear(); 				// clear centerpanel contents
			centerPanel.setWidget(infovis.init()); // add the graph in centerpanel
			infovis.getInfovisContainer().setWidth(String.valueOf(centerPanel.getOffsetWidth())+"px");   //adapt graph size to centerpanel size
			infovis.getInfovisContainer().setHeight(String.valueOf(centerPanel.getOffsetHeight())+"px");
			infovis.showGraph(jsonFD,"hypertree"); //load json to graph
		}
				
	}
	

	public interface DataProperties extends PropertyAccess<Data> {
		@Path("name")
		ModelKeyProvider<Data> key();
		ValueProvider<Data, int[]> id();
		ValueProvider<Data, String> name();
		ValueProvider<Data, String> value();
	}

	public class Data {
		private int id;
		private String name;
		private String value;

		public Data(int id, String name, String value) {
			super();
			this.id = id;
			this.name = name;
			this.value = value;
		}
		public int getId() {return id;}
		public void setId(int givenId) {this.id=givenId;}
		public String getName() {return name;}
		public void setName(String name) {this.name = name;}
		public String getValue() {return value;}
		public void setValue(String value) {this.value = value;}
		
		
	}
	
	public void setJsonFD(String jdata){
		jsonFD = jdata;
	}
}
