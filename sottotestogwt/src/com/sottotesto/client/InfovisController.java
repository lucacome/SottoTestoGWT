package com.sottotesto.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.Utility;

public class InfovisController {

	public static final class GRAPH_TYPE {
		public static final String FORCEDIRECTED = "forceDirected";
		public static final String HYPERTREE = "hyperTree";		  
	}

	//infovis container
	private HorizontalLayoutContainer infovisContainer;
	
	//log details container (on the right)
	private VerticalLayoutContainer logDetailsContainerVL;
	private FlowLayoutContainer logDetFlowCont;
	private HTML infovisDetailsHtml;
	private HTML infovisLogHtml;
	
	//data/graph container (on center)
	private VerticalLayoutContainer graphContainer;
	private HorizontalPanel checkBoxHC;
	private FlowLayoutContainer checkBoxFC;
	private List<CheckBox> checkBoxes;
	private HTML infovisGraphHtml;
	
	//services
	private final GraphServiceAsync GraphService = GWT.create(GraphService.class);
	private final GHTServiceAsync GHTService = GWT.create(GHTService.class);
	private List<String> listFD;
	private List<String> linkList;		//linksToXXX, hasXXXX, ...
	private List<String> linkNameList;	//only XXXX from linkList
	private List<String> entitiesEscaped;	//entity names with %XX escape chars

	public Widget init(){		
		Debug.printDbgLine("InfovisController.java: init()");

		checkBoxHC = new HorizontalPanel();
		
		checkBoxFC = new FlowLayoutContainer();
		checkBoxFC.setScrollMode(ScrollMode.AUTO);
		checkBoxFC.setId("checkBoxFlowContainer");
		checkBoxFC.add(checkBoxHC);
		
		infovisGraphHtml = new HTML();
		infovisGraphHtml.setHTML("<div id=\"infovis\"></div>");
		graphContainer = new VerticalLayoutContainer();
		graphContainer.setBorders(true);
		graphContainer.add(checkBoxFC);
		graphContainer.add(infovisGraphHtml);
		graphContainer.setId("graphContainer");

		infovisDetailsHtml = new HTML();
		infovisDetailsHtml.setHTML("<div id=\"inner-details\"></div>");
		infovisDetailsHtml.addStyleName("alignment=\"left\"");
		infovisDetailsHtml.setWordWrap(true);

		infovisLogHtml = new HTML();
		infovisLogHtml.setHTML("<div id=\"log\"></div>"); //disable log since it shows only first time

		logDetailsContainerVL = new VerticalLayoutContainer();
		logDetailsContainerVL.setBorders(true);
		logDetailsContainerVL.add(infovisLogHtml);
		logDetailsContainerVL.add(infovisDetailsHtml);
		logDetailsContainerVL.setId("logDetailsContainer");
		
		logDetFlowCont = new FlowLayoutContainer();
		logDetFlowCont.setScrollMode(ScrollMode.AUTO);		
		logDetFlowCont.add(logDetailsContainerVL);

		infovisContainer = new HorizontalLayoutContainer();
		infovisContainer.setBorders(true);
		infovisContainer.add(graphContainer, new HorizontalLayoutData(0.85, 1, new Margins(4)));
		infovisContainer.add(logDetFlowCont, new HorizontalLayoutData(0.15, 1, new Margins(4)));
		infovisContainer.setId("infovisContainer");

		return infovisContainer;
	}
	
	// legge linkList per creare linkNameList
	private void setLinkNames(){
		linkNameList = new ArrayList<String>();
		
		List<String> linksPossibilites = new ArrayList<String>();
		linksPossibilites.add("linksTo");
		linksPossibilites.add("has");
		
		for (String s : linkList){
			
			String sName = "";
			
			/*
			//METODO 1: PRENDI SOLO L'ULTIMA PAROLA CHE PARTE CON MAIUSCOLO
			String sSplit[] = s.split("(?=\\p{Upper})");
			sName = sSplit[sSplit.length-1];
			*/
			
			
			//METODO 2: USA UNA LISTA DI POSSIBILITA'
			sName = s;
			for(String prefix : linksPossibilites){
				if (sName.contains(prefix) && sName.charAt(0)==prefix.charAt(0)){
					sName = sName.replace(prefix, "");
					break;
				}
			}
			
			// add found name to list
			linkNameList.add(sName);			
		}
	}
	
	//dato un json crea le checkbox con i vari linksTo (usato per il Graph HT)
	public void setCheckBoxes(final String json){
		
		//set checkboxes change handler
		ValueChangeHandler<Boolean> checkBoxLinksHandler = new ValueChangeHandler<Boolean>() {		       
		      @Override
		      public void onValueChange(ValueChangeEvent<Boolean> event) {
		    	  
		    	//check selected checkboxes
		    	List<String> selectedLinksTo = new ArrayList<String>();
		    	selectedLinksTo = getSelectedLinks();
		    	
		    	Utility.showLoadingBar("Preparing Graph");
		    	callGHTService(json, selectedLinksTo);		    	
		      }
		    };

		//create list with links Names for chebox labels
		 setLinkNames();	
		
		//togli i vecchi checkbox, se presenti
		checkBoxHC.clear();
		
		//add refresh button
		ToolButton refreshTool = new ToolButton(ToolButton.REFRESH);
		refreshTool.setTitle("Reload current Graph");
		refreshTool.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				//check selected checkboxes
		    	List<String> selectedLinksTo = new ArrayList<String>();
		    	selectedLinksTo = getSelectedLinks();		    	
		    	Utility.showLoadingBar("Preparing Graph");
		    	callGHTService(json, selectedLinksTo);				
			}
		});
		checkBoxHC.add(refreshTool);
		checkBoxHC.add(new HTML(" &nbsp;"));
		
		if (linkNameList.size()<1) return;
		
		//aggiungi un label
		HTML checkBoxLabel = new HTML();
		checkBoxLabel.setHTML("<span id=\"checkBoxLabel\"><b>Filter data: </b></span>");		
		checkBoxHC.add(checkBoxLabel);

		//crea ed aggiungi i vari checkBox
		checkBoxes = new ArrayList<CheckBox>();
		for (String curLink : linkNameList){
			CheckBox cb = new CheckBox();
			cb.setId("graphCheckBox");
			cb.setValue(true);
			cb.setBoxLabel(curLink);
			cb.addValueChangeHandler(checkBoxLinksHandler);
			cb.setAllowTextSelection(false);
			checkBoxes.add(cb);
			checkBoxHC.add(cb);
		}
		
	}
	
	
	//dato un elenco di entita' crea le checkbox corrispondenti (usato per il Graph FD)
	public void setCheckBoxes(final List<EkpResponse> ekpResps){
		
		//get entities to show
		List<String> entities = new ArrayList<String>();
		entitiesEscaped = new ArrayList<String>();
		for (EkpResponse curResp : ekpResps){
			entities.add(curResp.getTag());
			entitiesEscaped.add(curResp.getEncodedTag());
		}

		//set checkboxes change handler
		ValueChangeHandler<Boolean> checkBoxLinksHandler = new ValueChangeHandler<Boolean>() {		       
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {

				//check selected checkboxes
				List<String> selectedEntities = new ArrayList<String>();
				selectedEntities = getSelectedEntityDBPlinks();
				Utility.showLoadingBar("Preparing Graph");
				callGraphService(listFD, selectedEntities);
			}
		};


		//togli i vecchi checkbox, se presenti
		checkBoxHC.clear();
		
		//add refresh button
		ToolButton refreshTool = new ToolButton(ToolButton.REFRESH);
		refreshTool.setTitle("Reload current Graph");
		refreshTool.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				//check selected checkboxes
				List<String> selectedEntities = new ArrayList<String>();
				selectedEntities = getSelectedEntityDBPlinks();
				Utility.showLoadingBar("Preparing Graph");
				callGraphService(listFD, selectedEntities);			
			}
		});
		checkBoxHC.add(refreshTool);
		checkBoxHC.add(new HTML(" &nbsp;"));

		//aggiungi un label
		HTML checkBoxLabel = new HTML();
		checkBoxLabel.setHTML("<span id=\"checkBoxLabel\"><b>Entities to compare: </b></span>");		
		checkBoxHC.add(checkBoxLabel);

		//crea ed aggiungi i vari checkBox
		checkBoxes = new ArrayList<CheckBox>();
		for (String curEntity : entities){
			CheckBox cb = new CheckBox();
			cb.setId("graphCheckBox");
			cb.setValue(true);
			curEntity=curEntity.replace("_", " ");
			cb.setBoxLabel(curEntity);
			cb.addValueChangeHandler(checkBoxLinksHandler);
			cb.setAllowTextSelection(false);				
			checkBoxes.add(cb);
			checkBoxHC.add(cb);
		}

	}
	
	 
	
	private List<String> getSelectedLinks(){
		List<String> selectedLinks = new ArrayList<String>();
		
		for(CheckBox cb : checkBoxes){
			if (cb.getValue()){
				selectedLinks.add(linkList.get(checkBoxes.indexOf(cb)));
			}
		}		
		return selectedLinks;
	}
	
	//usato nel graphHT
	private List<String> getSelectedDBPlinks(){
		List<String> selectedDBPlinks = new ArrayList<String>();
		
		for(CheckBox cb : checkBoxes){
			if (cb.getValue()){
				selectedDBPlinks.add("http://dbpedia.org/resource/"+cb.getBoxLabel().replace(" ", "_"));
			}
		}
		return selectedDBPlinks;
	}
	
	//usato nel graphFD
	private List<String> getSelectedEntityDBPlinks(){
		List<String> selectedDBPlinks = new ArrayList<String>();

		int curCbIndex=0;
		for(CheckBox cb : checkBoxes){
			if (cb.getValue()){
				selectedDBPlinks.add("http://dbpedia.org/resource/"+entitiesEscaped.get(curCbIndex));
			}
			curCbIndex++;
		}
		return selectedDBPlinks;
	}

	
	private void callGraphService(List<String> listFD, List<String> selectedDbpLinks){
		Debug.printDbgLine("InfovisController.java: callGraphService()");
		Debug.printDbgLine("InfovisController.java: callGraphService(): selected confrontation links:");
		for (String s : selectedDbpLinks){
			Debug.printDbgLine(s);
		}
		GraphService.sendToServer(listFD, selectedDbpLinks, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printErrLine("InfovisController.java: callGraphService(): onFailure()");
				Utility.hideLoadingBar();
			}

			public void onSuccess(String result) {
				Debug.printDbgLine("InfovisController.java: callGraphService(): onSuccess()");
				Debug.printDbgLine(result);

				// show the graph with the result json	
				showGraph(result, InfovisController.GRAPH_TYPE.FORCEDIRECTED);		
				Utility.hideLoadingBar();
			}
		});

	}
	
	private void callGHTService(String jsonHT, List<String> selectedLinks){			
		GHTService.sendToServer(jsonHT, selectedLinks, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printErrLine("InfovisController.java: callGHTService(): onFailure()");
				Utility.hideLoadingBar();
			}

			public void onSuccess(String result) {
				Debug.printDbgLine("InfovisController.java: callGHTService(): onSuccess()");
				Debug.printDbgLine(result);
				
				// show the graph with the result json				
				showGraph(result, InfovisController.GRAPH_TYPE.HYPERTREE);
				Utility.hideLoadingBar();
			}
			});
		
	}
	
	public Widget getInfovisContainer(){
		if (infovisContainer!=null) return infovisContainer;
		else return null;
	}

	public void removeAll(){
		Debug.printDbgLine("InfovisController.java: removeAll()");
		if (infovisContainer!=null) infovisContainer.removeFromParent();
	}

	public void updateDivSize(){
		String graphWidthString = String.valueOf(infovisContainer.getParent().getOffsetWidth()*83/100);
		String graphHeightString = String.valueOf(infovisContainer.getParent().getOffsetHeight()*97/100);
		Debug.printDbgLine("InfovisController.java: updateDivSize(): graph width="+graphWidthString);
		Debug.printDbgLine("InfovisController.java: updateDivSize(): graph height="+graphHeightString);
		infovisGraphHtml.setHTML("<div id=\"infovis\" style=\"width:"+graphWidthString+"px; height:"+graphHeightString+"px;\"></div>");
		infovisDetailsHtml.setHTML("<div id=\"inner-details\" style=\"width:"+logDetailsContainerVL.getOffsetWidth()+"; height:"+logDetailsContainerVL.getOffsetHeight()+"px; borders:1px;\"></div>");
	}

	public void showFullConfrontationGraph(){
		List<String> selectedEntities = new ArrayList<String>();
    	selectedEntities = getSelectedEntityDBPlinks();
    	
    	callGraphService(listFD, selectedEntities);
	}
	
	public void showGraph(String json, String type){
		Debug.printDbgLine("InfovisController.java: showGraph()");
		updateDivSize();
		callJit(json, type);
	}

	private native void callJit(String jdata, String type)/*-{
  		var myObject = eval('(' + jdata + ')');
		var json = myObject;
  		console.log(json);

  		if (type=="forceDirected") new $wnd.init_fd(json);
  		else if (type=="hyperTree") new $wnd.init_ht(json);
  		else console.log("callJit(): tipo di grafo richiesto sconosciuto");
	}-*/;

	public void setListFD(List<String> listFDtoSet){listFD=new ArrayList<String>(); listFD=listFDtoSet;}
	public void setLinks(List<String> listLinks){linkList=new ArrayList<String>(); linkList=listLinks;}
}
