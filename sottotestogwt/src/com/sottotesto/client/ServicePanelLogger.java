package com.sottotesto.client;

import java.util.Iterator;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.Utility;

public class ServicePanelLogger {
	
	//TAGME ITEMS
	private ExtendedDialogBox tagmeDialogBox;
	private TabPanel tagmeTabPanel;
	private FlowLayoutContainer tagmeFlowContainer;
	
	
	//EKP ITEMS
	private ExtendedDialogBox ekpDialogBox;
	private TabPanel ekpTabPanel;
	private FlowLayoutContainer ekpFlowContainer;
	
	//DBPEDIA ITEMS
	private ExtendedDialogBox dbpDialogBox;
	private TabPanel dbpTabPanel;
	private FlowLayoutContainer dbpFlowContainer;
	
	public ServicePanelLogger(){
		initTAGME();
		initEKP();
		initDBP();
	}
	
	
	private void initTAGME(){		
		tagmeTabPanel = new TabPanel();
		
		tagmeFlowContainer = new FlowLayoutContainer();
		tagmeFlowContainer.setScrollMode(ScrollMode.AUTO);
		tagmeFlowContainer.add(tagmeTabPanel);
		tagmeFlowContainer.setHeight(Utility.getDbMaxHeight());
		tagmeFlowContainer.setWidth(Utility.getDbMaxWidth());
		
		tagmeDialogBox = new ExtendedDialogBox();
		tagmeDialogBox.setText("TAGME Data");
		tagmeDialogBox.setAnimationEnabled(false);
		tagmeDialogBox.getElement().setId("tagmeDataDB");
		tagmeDialogBox.setWidget(tagmeFlowContainer);
	}	
	private void initEKP(){		
		ekpTabPanel = new TabPanel();
		
		ekpFlowContainer = new FlowLayoutContainer();
		ekpFlowContainer.setScrollMode(ScrollMode.AUTO);
		ekpFlowContainer.add(ekpTabPanel);
		ekpFlowContainer.setHeight(Utility.getDbMaxHeight());
		ekpFlowContainer.setWidth(Utility.getDbMaxWidth());
		
		ekpDialogBox = new ExtendedDialogBox();
		ekpDialogBox.setText("EKP Data");
		ekpDialogBox.setAnimationEnabled(false);
		ekpDialogBox.getElement().setId("ekpDataDB");
		ekpDialogBox.setWidget(ekpFlowContainer);
	}	
	private void initDBP(){		
		dbpTabPanel = new TabPanel();
		
		dbpFlowContainer = new FlowLayoutContainer();
		dbpFlowContainer.setScrollMode(ScrollMode.AUTO);
		dbpFlowContainer.add(dbpTabPanel);
		dbpFlowContainer.setHeight(Utility.getDbMaxHeight());
		dbpFlowContainer.setWidth(Utility.getDbMaxWidth());
		
		dbpDialogBox = new ExtendedDialogBox();
		dbpDialogBox.setText("DBPedia Data");
		dbpDialogBox.setAnimationEnabled(false);
		dbpDialogBox.getElement().setId("dbPediaDataDB");
		dbpDialogBox.setWidget(dbpFlowContainer);
	}
	
	
	public void showTagmeDataDB(){
		tagmeDialogBox.center();		
		tagmeDialogBox.show();
		tagmeTabPanel.selectTab(0);	
	}
	public void showEkpDataDB(){
		ekpDialogBox.center();		
		ekpDialogBox.show();
		ekpTabPanel.selectTab(0);	
	}	
	public void showDBPediaDataDB(){
		dbpDialogBox.center();		
		dbpDialogBox.show();
		dbpTabPanel.selectTab(0);	
	}	
	
	
	public void addTAGMElog(TagmeResponse tagmeResp){
		HTML html = new HTML("");		
		
		html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(tagmeResp.getTime())+"ms");
		html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(tagmeResp.getCode()));
		html.setHTML(html.getHTML()+"<br><br><b>Message:</b> "+tagmeResp.getMessage());
		html.setHTML(html.getHTML()+"<br><br><b>ContentType:</b> "+tagmeResp.getContentType());
		
		
		if (tagmeResp.getCode()==200){
			html.setHTML(html.getHTML()+"<br><br><b>Number of Total Json Resource:</b> "+tagmeResp.getResNum());
			if (tagmeResp.getResNum()>0)
			{
				html.setHTML(html.getHTML()+"<br><br><b>Number of Relevant Tags found:</b> "+tagmeResp.getTitleTag().size());

				if (!tagmeResp.getTitleTag().isEmpty())
				{
					String taggedTotal="";
					Iterator<String> tagged = tagmeResp.getTitleTag().iterator();
					while (tagged.hasNext()){
						taggedTotal+=tagged.next()+"<br>";
					}
					html.setHTML(html.getHTML()+"<br><br><b>Elements tagged:</b><br> "+taggedTotal);
					html.setHTML(html.getHTML()+"<br><br><b>Json data:</b><br> "+tagmeResp.getJson().replaceAll(",",",<br>"));
				}
			}
		}
		else{
			html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+tagmeResp.getError());	
		}	
		
		html.setWidth(Utility.getDbMaxWidth()-(Utility.getDbMaxWidth()*8/100)+"px");
		tagmeTabPanel.add(html, "TAGME Result");
	}
	public void addEKPlog(EkpResponse ekpResp){
		HTML html = new HTML("");		
		html.setTitle(ekpResp.getTag());
		html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(ekpResp.getTime())+"ms");
		html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(ekpResp.getCode()));
		html.setHTML(html.getHTML()+"<br><br><b>Message:</b> "+ekpResp.getMessage());
		html.setHTML(html.getHTML()+"<br><br><b>ContentType:</b> "+ekpResp.getContentType());
		
		
		if (ekpResp.getCode()==200){
			html.setHTML(html.getHTML()+"<br><br><b>Response:</b> ");
			html.setHTML(html.getHTML()+"<br>"+ekpResp.jdataHT);
		}
		else{
			html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+ekpResp.getError());	
		}		
		
		html.setWidth(Utility.getDbMaxWidth()-(Utility.getDbMaxWidth()*8/100)+"px");
		ekpTabPanel.add(html, ekpResp.getTag());
	}
	public void addDBPlog(DBPediaResponse dbpediaResp){
		HTML html = new HTML("");		
		html.setTitle("Description");
		html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(dbpediaResp.getTime())+"ms");
		html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(dbpediaResp.getCode())+"ms");
		if (dbpediaResp.getCode()==200){
			html.setHTML(html.getHTML()+"<br><br><b>Result Query:</b><br>"+dbpediaResp.getQueryResultXML());
		}
		else{
			html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+dbpediaResp.getError());	
		}		
		
		html.setWidth(Utility.getDbMaxWidth()-(Utility.getDbMaxWidth()*8/100)+"px");
		dbpTabPanel.add(html, "Description");
	}
	public void addDBPlog(DBPQueryResp dbpQueryResp){
		HTML html = new HTML("");
		html.setTitle(dbpQueryResp.getEntity());
		
		html.setWidth(Utility.getDbMaxWidth()-(Utility.getDbMaxWidth()*8/100)+"px");
		dbpTabPanel.add(html, dbpQueryResp.getEntity());
	}
	public void updateDBPQlog(DBPQueryResp dbpQueryResp){
		Iterator<Widget> iterHtml = dbpTabPanel.iterator();
		
		while (iterHtml.hasNext()){
			HTML html = (HTML)iterHtml.next();
			if(html.getTitle().equals(dbpQueryResp.getEntity())){
				html.setHTML(html.getHTML()+"<br>call n."+dbpQueryResp.getCallNum()+"/"+dbpQueryResp.getMaxCalls()+" - "+dbpQueryResp.getName()+" -> "+dbpQueryResp.getLat()+","+dbpQueryResp.getLng());
				break;
			}
		}
	}
	
	
	
	
	// MODIFIED DIALOGBOX CLASS (to add X button and 'ESC' support)
	private static class ExtendedDialogBox extends DialogBox {

		private Node closeEventTarget = null;

		public ExtendedDialogBox() {
			// get the "dialogTopRight" class td
			Element dialogTopRight = getCellElement(0, 2);

			// close button image html
			dialogTopRight.setInnerHTML(
					"<div style=\"margin-left:-25px;margin-top: 2px; cursor:pointer;\">" + 
							"<img src=\"closeicon.gif\" height=\"20px\"/>" + 
					"</div>");

			// set the event target
			closeEventTarget = dialogTopRight.getChild(0).getChild(0);
		}

		@Override
		protected void onPreviewNativeEvent(NativePreviewEvent event) {
			super.onPreviewNativeEvent(event);

			NativeEvent nativeEvent = event.getNativeEvent();

			if (!event.isCanceled() 
					&& (event.getTypeInt() == Event.ONCLICK)
					&& isCloseEvent(nativeEvent))
			{
				this.hide();
				this.removeFromParent();
			}

			switch (event.getTypeInt()) {
			case Event.ONKEYDOWN:
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ||
				event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					hide();
					removeFromParent();
				}
				break;
			}
		}

		// see if the click target is the close button
		private boolean isCloseEvent(NativeEvent event) {
			return event.getEventTarget().equals(closeEventTarget); //compares equality of the underlying DOM elements
		} 
	}

}
