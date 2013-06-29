package com.sottotesto.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sottotesto.shared.Debug;

public class InfovisController {

	//infovis container
	private HorizontalLayoutContainer infovisContainer;
	private VerticalLayoutContainer graphlogContainer;
	private HTML infovisGraphHtml;
	private HTML infovisDetailsHtml;
	private HTML infovisLogHtml;
	
	public Widget init(){		
		Debug.printDbgLine("InfovisController.java: init()");
		
		infovisGraphHtml = new HTML();
		infovisGraphHtml.setHTML("<div id=\"infovis\"></div>");	
		infovisGraphHtml.addStyleName("background-color:black");
		
		infovisDetailsHtml = new HTML();
		infovisDetailsHtml.setHTML("<div id=\"inner-details\"></div>");
		infovisDetailsHtml.addStyleName("alignment=\"left\"");
		
		infovisLogHtml = new HTML();
		infovisLogHtml.setHTML("<div id=\"log\"></div>");
		
		graphlogContainer = new VerticalLayoutContainer();
		graphlogContainer.setBorders(true);
		graphlogContainer.add(infovisLogHtml);
		graphlogContainer.add(infovisGraphHtml);
		
		infovisContainer = new HorizontalLayoutContainer();
		infovisContainer.setBorders(true);
		infovisContainer.add(graphlogContainer, new HorizontalLayoutData(0.85, 1, new Margins(4)));
		infovisContainer.add(infovisDetailsHtml, new HorizontalLayoutData(0.15, 1, new Margins(4)));
		
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
	
	
	public void showGraph(String json){
		Debug.printDbgLine("InfovisController.java: showGraph()");
		callJit(json);
	}

	private native void callJit(String jdata)/*-{
  		var myObject = eval('(' + jdata + ')');
		var json = myObject;
  		console.log(json);
  		new $wnd.init(json);
	}-*/;

}
