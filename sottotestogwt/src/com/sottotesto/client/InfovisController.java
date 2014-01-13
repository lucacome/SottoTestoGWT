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
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sottotesto.shared.Debug;
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
	private List<String> linkList;
	private List<String> linkNameList;

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
	
	//dato un json crea le checkbox con i vari linksTo
	public void setCheckBoxes(final String json){
		
		//set checkboxes change handler
		ValueChangeHandler<Boolean> checkBoxLinksHandler = new ValueChangeHandler<Boolean>() {		       
		      @Override
		      public void onValueChange(ValueChangeEvent<Boolean> event) {
		    	  
		    	//check selected checkboxes
		    	List<String> selectedLinksTo = new ArrayList<String>();
		    	selectedLinksTo = getSelectedLinks();
		    	
		    	Utility.showLoadingBar();
		    	callGHTService(json, selectedLinksTo);		    	
		      }
		    };

		linkNameList = new ArrayList<String>();
		for (String s : linkList){
			String sSplit[] = s.split("(?=\\p{Upper})");
			linkNameList.add(sSplit[sSplit.length-1]);
			Debug.printDbgLine("InfovisController.java: setCheckBoxes(): added->"+linkNameList.get(linkNameList.size()-1));
		}
		
		//togli i vecchi checkbox, se presenti
		checkBoxHC.clear();
		
		//aggiungi un label
		HTML checkBoxLabel = new HTML();
		checkBoxLabel.setHTML("<span id=\"checkBoxLabel\"><b>Visualizza: </b></span>");		
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
	
	
	//dato un elenco di entita' crea le checkbox corrispondenti
	public void setCheckBoxes(final List<String> entities){

		//set checkboxes change handler
		ValueChangeHandler<Boolean> checkBoxLinksHandler = new ValueChangeHandler<Boolean>() {		       
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {

				//check selected checkboxes
				List<String> selectedEntities = new ArrayList<String>();
				selectedEntities = getSelectedDBPlinks();

				//if selected 0?
				//callGHTService(json, selectedLinksTo);
				Utility.showLoadingBar();
				callGraphService(listFD, selectedEntities);
			}
		};


		//togli i vecchi checkbox, se presenti
		checkBoxHC.clear();

		//aggiungi un label
		HTML checkBoxLabel = new HTML();
		checkBoxLabel.setHTML("<span id=\"checkBoxLabel\"><b>Confronta: </b></span>");		
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
	
	private List<String> getSelectedDBPlinks(){
		List<String> selectedDBPlinks = new ArrayList<String>();
		
		for(CheckBox cb : checkBoxes){
			if (cb.getValue()){
				selectedDBPlinks.add("http://dbpedia.org/resource/"+cb.getBoxLabel().replace(" ", "_"));
			}
		}
		return selectedDBPlinks;
	}

	
	private void callGraphService(List<String> listFD, List<String> selectedDbpLinks){

		GraphService.sendToServer(listFD, selectedDbpLinks, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printDbgLine("InfovisController.java: callGraphService(): onFailure()");
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
				Debug.printDbgLine("InfovisController.java: callGHTService(): onFailure()");
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
    	selectedEntities = getSelectedDBPlinks();
    	
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
