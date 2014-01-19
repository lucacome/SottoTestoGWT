package com.sottotesto.shared;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class Utility {

	private static AutoProgressMessageBox loadingBox;
	private static String loadingText="";

	public static void showLoadingBar(String text){

		// create loading box if first time
		if(loadingBox == null){
			loadingBox = new AutoProgressMessageBox("WORKING", "Processing Request, please wait...");

			//add 'X' close button if first time
			HTML closeButton = new HTML("<div style=\"margin-left:-25px;margin-top: 2px; cursor:pointer;\"><img src=\"closeicon.gif\" height=\"20px\"/></div>");
			closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {	
					loadingBox.hide();
				}
			});

			//loadingBox.addTool(closeButton);

			ToolButton closeTool = new ToolButton(ToolButton.CLOSE);
			closeTool.addSelectHandler(new SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					// TODO Auto-generated method stub
					loadingBox.hide();
				}
			});
			loadingBox.addTool(closeTool);
		}

		// set working text (if null set default)
		loadingText=text;
		if (loadingText=="") loadingBox.setProgressText("... Working ...");
		else loadingBox.setProgressText("... "+loadingText+" ...");

		// add extra
		loadingBox.setShadow(true);

		// show loading box
		loadingBox.auto();
		loadingBox.show();
	}
	public static void hideLoadingBar(){
		if (loadingBox != null) loadingBox.hide();
		loadingText="";
	}


	public static List<String> clearDoubleEntries(List<String> list){
		List<String> clearedList = new ArrayList<String>();
		
		for(String curEntry : list){
			if(clearedList.indexOf(curEntry)==-1)
				clearedList.add(curEntry);
		}
		Debug.printDbgLine("New cleared list:");
		for(String s : clearedList) Debug.printDbgLine(s);
		return clearedList;
	}
	
 	public static long calcTimeTookMs(long StartTimeMs){
		return (System.currentTimeMillis()-StartTimeMs);
	}

	public static String toUTF8(String dirtyString){
		String cleanString = "";

		try {
			cleanString = new String(dirtyString.getBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {/*do nothing*/}		
		return cleanString;
	}

	public static String getErrorHtmlString (Throwable throwable) {
		String ret="";
		while (throwable!=null) {
			if (throwable instanceof com.google.gwt.event.shared.UmbrellaException){
				for (Throwable thr2 :((com.google.gwt.event.shared.UmbrellaException)throwable).getCauses()){
					if (ret != "")
						ret += thr2.toString();
					ret += "<br><b>Caused by:</b> ";
					ret += "<br>  at "+getErrorHtmlString(thr2);
				}
			} else if (throwable instanceof com.google.web.bindery.event.shared.UmbrellaException){
				for (Throwable thr2 :((com.google.web.bindery.event.shared.UmbrellaException)throwable).getCauses()){
					if (ret != "")
						ret += "<br><b>Caused by:</b> ";
					ret += thr2.toString();
					ret += "<br>  at "+getErrorHtmlString(thr2);
				}
			} else {
				if (ret != "")
					ret += "<br><b>Caused by:</b> ";
				ret += throwable.toString();
				for (StackTraceElement sTE : throwable.getStackTrace())
					ret += "<br>  at "+sTE;
			}
			throwable = throwable.getCause();
		}

		return ret;
	}

	public static int getDbMaxHeight(){
		int dbMaxHeight = (RootPanel.get("body").getOffsetHeight()*80)/100;
		Debug.printDbgLine("Utility.java: getDbMaxHeight(): "+dbMaxHeight);
		return dbMaxHeight;
	}


	public static int getDbMaxWidth(){
		int dbMaxWidth = RootPanel.get().getOffsetWidth()/3;
		Debug.printDbgLine("Utility.java: getDbMaxWidth(): "+dbMaxWidth);
		return dbMaxWidth;
	}

	public static int getPanelsMaxWidth(){
		return RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*3/100);
	}

}
