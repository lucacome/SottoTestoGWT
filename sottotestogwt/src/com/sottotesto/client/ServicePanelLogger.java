package com.sottotesto.client;

import java.util.Iterator;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.Utility;

public class ServicePanelLogger {
	
	//TAGME ITEMS
	private Dialog tagmeDialogBox;
	private TabPanel tagmeTabPanel;
	
	//EKP ITEMS
	private Dialog ekpDialogBox;
	private TabPanel ekpTabPanel;
	
	//DBPEDIA ITEMS	
	private Dialog dbpDialogBox;
	private TabPanel dbpTabPanel;
	
	public ServicePanelLogger(){
		initTAGME();
		initEKP();
		initDBP();
	}
	

	private void initTAGME(){		
		tagmeTabPanel = new TabPanel();
		tagmeTabPanel.setAnimScroll(true);
		tagmeTabPanel.setTabScroll(true);
		tagmeTabPanel.setId("tagmeTabPanel");	
		
		tagmeDialogBox = new Dialog();
		tagmeDialogBox.setHeadingText("TAGME Data");
		tagmeDialogBox.setId("tagmeDataDB");
		tagmeDialogBox.setHideOnButtonClick(true);
		tagmeDialogBox.setHeight(Utility.getDbMaxHeight());
		tagmeDialogBox.setWidth(Utility.getDbMaxWidth());
		tagmeDialogBox.setWidget(tagmeTabPanel);
	}	
	private void initEKP(){		
		ekpTabPanel = new TabPanel();
		ekpTabPanel.setAnimScroll(true);
		ekpTabPanel.setTabScroll(true);
		ekpTabPanel.setId("ekpTabPanel");
		
		ekpDialogBox = new Dialog();
		ekpDialogBox.setHeadingText("EKP Data");
		ekpDialogBox.setId("ekpDataDB");
		ekpDialogBox.setHideOnButtonClick(true);
		ekpDialogBox.setHeight(Utility.getDbMaxHeight());
		ekpDialogBox.setWidth(Utility.getDbMaxWidth());
		ekpDialogBox.setWidget(ekpTabPanel);
	}	
	private void initDBP(){		
		dbpTabPanel = new TabPanel();
		dbpTabPanel.setAnimScroll(true);
		dbpTabPanel.setTabScroll(true);
		dbpTabPanel.setId("dbpTabPanel");		
		
		dbpDialogBox = new Dialog();
		dbpDialogBox.setHeadingText("DBPedia Data");
		dbpDialogBox.setId("dbPediaDataDB");
		dbpDialogBox.setHideOnButtonClick(true);
		dbpDialogBox.setHeight(Utility.getDbMaxHeight());
		dbpDialogBox.setWidth(Utility.getDbMaxWidth());
		dbpDialogBox.setWidget(dbpTabPanel);		
	}
	
	
	public void showTagmeDataDB(){	
		tagmeDialogBox.show();
		tagmeDialogBox.center();
	}
	public void showEkpDataDB(){
		ekpDialogBox.show();
		ekpDialogBox.center();		
	}	
	public void showDBPediaDataDB(){	
		dbpDialogBox.show();
		dbpDialogBox.center();		
	}	
	
	
	public void addTAGMElog(TagmeResponse tagmeResp){
		HTML html = new HTML("");		
		
		html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(tagmeResp.getTime())+"ms");
		html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(tagmeResp.getCode()));
		
		if (tagmeResp.getCode()==200){
			html.setHTML(html.getHTML()+"<br><br><b>Message:</b> "+tagmeResp.getMessage());
			html.setHTML(html.getHTML()+"<br><br><b>ContentType:</b> "+tagmeResp.getContentType());
			html.setHTML(html.getHTML()+"<br><br><b>Number of Total Resource:</b> "+tagmeResp.getResNum());
			if (tagmeResp.getResNum()>0)
			{
				html.setHTML(html.getHTML()+"<br><br><b>Number of Relevant Tags found:</b> "+tagmeResp.getTitleTagClean().size());

				if (!tagmeResp.getTitleTagClean().isEmpty())
				{
					String taggedTotal="";
					Iterator<String> tagged = tagmeResp.getTitleTagClean().iterator();
					while (tagged.hasNext()){
						taggedTotal+=tagged.next()+"<br>";
					}
					html.setHTML(html.getHTML()+"<br><br><b>Elements tagged:</b><br> "+taggedTotal);					
				}
				
				if(!tagmeResp.getSpotSkipped().isEmpty()){
					String skippedTotal="";
					Iterator<String> skipped = tagmeResp.getSpotSkipped().iterator();
					while (skipped.hasNext()){
						skippedTotal+=skipped.next()+"<br>";
					}
					html.setHTML(html.getHTML()+"<br><b>Elements skipped:</b><br> "+skippedTotal);
				}
				
				if (!tagmeResp.getTitleTagClean().isEmpty())
				{
					html.setHTML(html.getHTML()+"<br><b>Json data:</b><br> "+tagmeResp.getJson().replaceAll(",",",<br>"));
				}
			}
		}
		else{
			html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+tagmeResp.getError());	
		}			
		
		FlowLayoutContainer tabFC = new FlowLayoutContainer();
		tabFC.setScrollMode(ScrollMode.AUTO);
		tabFC.add(html);
		tagmeTabPanel.add(tabFC, new TabItemConfig("TAGME Result"));
	}
	public void addEKPlog(EkpResponse ekpResp){
		HTML html = new HTML("");		
		html.setTitle(ekpResp.getTag());
		html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(ekpResp.getTime())+"ms");
		html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(ekpResp.getCode()));	
		
		if (ekpResp.getCode()==200){
			html.setHTML(html.getHTML()+"<br><br><b>Message:</b> "+ekpResp.getMessage());
			html.setHTML(html.getHTML()+"<br><br><b>ContentType:</b> "+ekpResp.getContentType());
			html.setHTML(html.getHTML()+"<br><br><b>Response:</b> ");
			html.setHTML(html.getHTML()+"<br>"+ekpResp.jdataHT);
		}
		else{			
			html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+ekpResp.getError());	
		}				
		
		FlowLayoutContainer tabFC = new FlowLayoutContainer();
		tabFC.setScrollMode(ScrollMode.AUTO);
		tabFC.add(html);
		ekpTabPanel.add(tabFC, new TabItemConfig(ekpResp.getTag()));
	}
	public void addDBPlog(DBPediaResponse dbpediaResp){
		boolean tabAlreadyPresent = false;
		
		// CHECK IF TAB WAS ALREADY PRESENT
		Iterator<Widget> iterHtml = dbpTabPanel.iterator();
		String entity = "";
		entity = dbpediaResp.getEntity();
		while (iterHtml.hasNext()){
			FlowLayoutContainer tabFC = (FlowLayoutContainer)iterHtml.next();
			HTML html = (HTML)tabFC.getWidget(0);
			if(html.getTitle().equals(entity)){
				tabAlreadyPresent=true;
				break;
			}
		}
		
		
		// IF TAB WASN'T PRESENT, ADD ONE
		if (!tabAlreadyPresent){
			HTML html = new HTML("");		
			html.setTitle(dbpediaResp.getEntity());
			html.setHTML(html.getHTML()+"<b>Response time:</b> "+String.valueOf(dbpediaResp.getTime())+"ms");
			html.setHTML(html.getHTML()+"<br><br><b>Code:</b> "+String.valueOf(dbpediaResp.getCode()));
			if (dbpediaResp.getCode()==200){
				html.setHTML(html.getHTML()+"<br><br><b>Entity Type: </b>"+dbpediaResp.getEntityType()+"</b><br>");
				html.setHTML(html.getHTML()+"<br><br><b>Query output for "+dbpediaResp.getEntity()+":</b><br>"+dbpediaResp.getQueryResultXML());
			}
			else{
				html.setHTML(html.getHTML()+"<br><br><b>Error:</b><br>"+dbpediaResp.getError());	
			}
			html.setHTML(html.getHTML()+"<br><br><b>Gps Coordinates:</b>");

			FlowLayoutContainer tabFC = new FlowLayoutContainer();
			tabFC.setScrollMode(ScrollMode.AUTO);
			tabFC.add(html);
			dbpTabPanel.add(tabFC, new TabItemConfig(dbpediaResp.getEntity()));
		}
	}

	public void updateDBPQlog(DBPQueryResp dbpQueryResp){
		
		Iterator<Widget> iterHtml = dbpTabPanel.iterator();
		String entity = "";
		entity = dbpQueryResp.getEntity();
		entity = entity.replace("[", "");
		entity = entity.replace("]", "");
		entity = entity.replace(" ", "_");
		while (iterHtml.hasNext()){ //find correct tab to append gps log
			FlowLayoutContainer tabFC = (FlowLayoutContainer)iterHtml.next();
			HTML html = (HTML)tabFC.getWidget(0);
			if(html.getTitle().equals(entity)){
				if (dbpQueryResp.getSuccess()) html.setHTML(html.getHTML()+"<br>call n.<b>"+dbpQueryResp.getCallNum()+"</b>/"+dbpQueryResp.getMaxCalls()+" - "+dbpQueryResp.getName()+" -> "+dbpQueryResp.getLat()+","+dbpQueryResp.getLng());
				else  html.setHTML(html.getHTML()+"<br>call n.<b>"+dbpQueryResp.getCallNum()+"</b>/"+dbpQueryResp.getMaxCalls()+" - "+dbpQueryResp.getName()+" -> FAILED: "+dbpQueryResp.getError());
				break;
			}
		}
	}
}
