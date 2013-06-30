package com.sottotesto.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sottotesto.shared.Debug;

public class InfovisController {

	//infovis container
	private HorizontalLayoutContainer infovisContainer;
	private VerticalLayoutContainer logDetailsContainer;
	private VerticalLayoutContainer graphContainer;
	private HTML infovisGraphHtml;
	private HTML infovisDetailsHtml;
	private HTML infovisLogHtml;
	
	public Widget init(){		
		Debug.printDbgLine("InfovisController.java: init()");
		
		infovisGraphHtml = new HTML();
		infovisGraphHtml.setHTML("<div id=\"infovis\"></div>");
		graphContainer = new VerticalLayoutContainer();
		graphContainer.setBorders(true);
		graphContainer.add(infovisGraphHtml);
		graphContainer.setId("graphContainer");
		
		infovisDetailsHtml = new HTML();
		infovisDetailsHtml.setHTML("<div id=\"inner-details\"></div>");
		infovisDetailsHtml.addStyleName("alignment=\"left\"");
		infovisDetailsHtml.setWordWrap(true);
		
		infovisLogHtml = new HTML();
		infovisLogHtml.setHTML("<div id=\"log\"></div>");
		
		logDetailsContainer = new VerticalLayoutContainer();
		logDetailsContainer.setBorders(true);
		logDetailsContainer.add(infovisLogHtml);
		logDetailsContainer.add(infovisDetailsHtml);
		logDetailsContainer.setId("logDetailsContainer");
		
		infovisContainer = new HorizontalLayoutContainer();
		infovisContainer.setBorders(true);
		infovisContainer.add(graphContainer, new HorizontalLayoutData(0.85, 1, new Margins(4)));
		infovisContainer.add(logDetailsContainer, new HorizontalLayoutData(0.15, 1, new Margins(4)));
		infovisContainer.setId("infovisContainer");
		
		return infovisContainer;
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
		infovisDetailsHtml.setHTML("<div id=\"inner-details\" style=\"width:"+logDetailsContainer.getOffsetWidth()+"; height:"+logDetailsContainer.getOffsetHeight()+"px; borders:1px;\"></div>");
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
  		
  		if (type=="forcedirected") new $wnd.init_fd(json);
  		else if (type=="hypertree") new $wnd.init_ht(json);
  		else console.log("callJit(): tipo di grafo richiesto sconosciuto");
	}-*/;

}
