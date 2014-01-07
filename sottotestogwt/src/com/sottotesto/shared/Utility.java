package com.sottotesto.shared;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

public class Utility {
	
	 

	public static long calcTimeTookMs(long StartTimeMs){
		return (System.currentTimeMillis()-StartTimeMs);
	}
	
	public static void showTagmeDataDB(TagmeResponse tagmeResp){
		Debug.printDbgLine("Utility.java: showTagmeDataDB()");
		
		final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
		dialogBox.setText("Tagme Data");
		dialogBox.setAnimationEnabled(false);
		dialogBox.getElement().setId("tagmeDataDB");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Response time:</b> "+String.valueOf(tagmeResp.getTime())+"ms"));
		dialogVPanel.add(new HTML("<br><b>Code:</b> "+String.valueOf(tagmeResp.getCode())));
		dialogVPanel.add(new HTML("<br><b>Message:</b> "+tagmeResp.getMessage()));
		dialogVPanel.add(new HTML("<br><b>ContentType:</b> "+tagmeResp.getContentType()));
		
		if (tagmeResp.getCode() != 200)
			dialogVPanel.add(new HTML("<br><b>Error:</b> "+tagmeResp.getError()));
		else{ // got 200			
			dialogVPanel.add(new HTML("<br><b>Number of Total Json Resource:</b> "+tagmeResp.getResNum()));
			if (tagmeResp.getResNum()>0)
			{
				dialogVPanel.add(new HTML("<br><b>Number of Relevant Tags found:</b> "+tagmeResp.getTitleTag().size()));
				
				if (!tagmeResp.getTitleTag().isEmpty())
				{
					String taggedTotal="";
					Iterator<String> tagged = tagmeResp.getTitleTag().iterator();
					while (tagged.hasNext()){
						taggedTotal+=tagged.next()+"<br>";
					}
					dialogVPanel.add(new HTML("<br><b>Elements tagged:</b><br> "+taggedTotal));
					dialogVPanel.add(new HTML("<br><b>Json data:</b><br> "+tagmeResp.getJson().replaceAll(",",",<br>")));
				}
					
			}
			
			
		}
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		FlowLayoutContainer container = new FlowLayoutContainer();
		container.setScrollMode(ScrollMode.AUTO);		
		container.add(dialogVPanel);		
		dialogBox.setWidget(container);		
		dialogBox.center(); //must be done for updating data for resizing
		Debug.printDbgLine("Utility.java: showTagmeDataDB(): dialogHeight="+container.getOffsetHeight());
		if (container.getOffsetHeight()>getDbMaxHeight()) container.setHeight(getDbMaxHeight());
		if (container.getOffsetWidth()>getDbMaxWidth()) container.setWidth(getDbMaxWidth());
		
		dialogBox.center();
		dialogBox.show();
		
		
		
		
		
	}
	
	public static void showDBPediaDataDB(DBPediaResponse dbpediaResp){
		Debug.printDbgLine("Utility.java: showDBPediaDataDB()");
		
		final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
		dialogBox.setText("DBPedia Data");
		dialogBox.setAnimationEnabled(false);
		dialogBox.getElement().setId("dbPediaDataDB");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Response time:</b> "+String.valueOf(dbpediaResp.getTime())+"ms"));
		dialogVPanel.add(new HTML("<br><b>Code:</b> "+String.valueOf(dbpediaResp.getCode())));
		

		if (dbpediaResp.getCode()==200){
			dialogVPanel.add(new HTML("<br><b>Result Query:</b><br>"+dbpediaResp.getQueryResultXML()));
		}
		else{
			dialogVPanel.add(new HTML("<br><b>Error:</b><br>"+dbpediaResp.getError()));			
		}
		 

		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		FlowLayoutContainer container = new FlowLayoutContainer();
		container.setScrollMode(ScrollMode.AUTO);
		container.add(dialogVPanel);
		dialogBox.setWidget(container);
		dialogBox.center(); //must be done for updating data for resizing
		Debug.printDbgLine("Utility.java: showDBPediaDataDB(): dialogHeight="+container.getOffsetHeight());
		//if (container.getOffsetHeight()>getDbMaxHeight()) container.setHeight(getDbMaxHeight());
		//if (container.getOffsetWidth()>getDbMaxWidth()) container.setWidth(getDbMaxWidth());
		container.setHeight(getDbMaxHeight());
		container.setWidth(getDbMaxWidth());
		
		dialogBox.center();		
		dialogBox.show();
	}
	
	public static void showEkpDataDB(List<EkpResponse> ekpResp){
		Debug.printDbgLine("Utility.java: showEkpDataDB()");
		
		final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
		dialogBox.setText("Ekp Data");
		dialogBox.setAnimationEnabled(false);
		dialogBox.getElement().setId("ekpDataDB");
		
		
		TabPanel folder = new TabPanel();		 
		int tabNum = ekpResp.size();

		for (int i=0; i<tabNum; i++){
			VerticalPanel dialogVPanel = new VerticalPanel();
			dialogVPanel.addStyleName("dialogVPanel");
			dialogVPanel.add(new HTML("<b>Response time:</b> "+String.valueOf(ekpResp.get(i).getTime())+"ms"));
			dialogVPanel.add(new HTML("<br><b>Code:</b> "+String.valueOf(ekpResp.get(i).getCode())));
			dialogVPanel.add(new HTML("<br><b>Message:</b> "+ekpResp.get(i).getMessage()));
			dialogVPanel.add(new HTML("<br><b>ContentType:</b> "+ekpResp.get(i).getContentType()));
		
			if (ekpResp.get(i).getCode() != 200)
				dialogVPanel.add(new HTML("<br><b>Error:</b> "+ekpResp.get(i).getError()));
			else{
				dialogVPanel.add(new HTML("<br><b>Response:</b> "));
				HTML rdfHtml = new HTML(); rdfHtml.setText(ekpResp.get(i).jdataHT);
				dialogVPanel.add(rdfHtml);
			}
			dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
			folder.add(dialogVPanel, ekpResp.get(i).getTag());
		}
		
		folder.selectTab(0);
		FlowLayoutContainer container = new FlowLayoutContainer();
		container.setScrollMode(ScrollMode.AUTO);
		container.add(folder);
		dialogBox.setWidget(container);
		dialogBox.center(); //must be done for updating data for resizing
		Debug.printDbgLine("Utility.java: showEKPDataDB(): dialogHeight="+container.getOffsetHeight());
		//if (container.getOffsetHeight()>getDbMaxHeight()) container.setHeight(getDbMaxHeight());
		//if (container.getOffsetWidth()>getDbMaxWidth()) container.setWidth(getDbMaxWidth());
		container.setHeight(getDbMaxHeight());
		container.setWidth(getDbMaxWidth());
		
		dialogBox.center();		
		dialogBox.show();	
	}
	
	
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
	
	public static String getFakeEkpResponse(){
		String resp = "";		
		resp = "<rdf:RDF			    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"			    xmlns:j.0=\"http://www.ontologydesignpatterns.org/ekp/owl/Person.owl#\"			    xmlns:j.1=\"http://dbpedia.org/ontology/\"			    xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" > 			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/University_of_Padua\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			    <rdfs:label xml:lang=\"en\">University of Padua</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Queen_%28band%29\">			    <rdfs:label xml:lang=\"en\">Queen (band)</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/University_of_Pisa\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			    <rdfs:label xml:lang=\"en\">University of Pisa</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Siena\">			    <rdfs:label xml:lang=\"en\">Siena</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Rice_University\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			    <rdfs:label xml:lang=\"en\">Rice University</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Kim_Stanley_Robinson\">			    <rdfs:label xml:lang=\"en\">Kim Stanley Robinson</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Artist\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/International_Astronomical_Union\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			    <rdfs:label xml:lang=\"en\">International Astronomical Union</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Bertolt_Brecht\">			    <rdfs:label xml:lang=\"en\">Bertolt Brecht</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Artist\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Barrie_Stavis\">			    <rdfs:label xml:lang=\"en\">Barrie Stavis</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Artist\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Pisa\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			    <rdfs:label xml:lang=\"en\">Pisa</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/ontology/Person\">			    <rdfs:label xml:lang=\"pt\">pessoa</rdfs:label>			    <rdfs:label xml:lang=\"fr\">personne</rdfs:label>			    <rdfs:label xml:lang=\"en\">person</rdfs:label>			    <rdfs:label xml:lang=\"de\">Person</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Sapienza_University_of_Rome\">			    <rdfs:label xml:lang=\"en\">Sapienza University of Rome</rdfs:label>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Organisation\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Arthur_Koestler\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Artist\"/>			    <rdfs:label xml:lang=\"en\">Arthur Koestler</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Duchy_of_Florence\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			    <rdfs:label xml:lang=\"en\">Duchy of Florence</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/ontology/Artist\">			    <rdfs:label xml:lang=\"fr\">artiste</rdfs:label>			    <rdfs:label xml:lang=\"en\">artist</rdfs:label>			    <rdfs:label xml:lang=\"de\">KÃ¼nstler</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/ontology/Organisation\">			    <rdfs:label xml:lang=\"pt\">organizaÃ§Ã£o</rdfs:label>			    <rdfs:label xml:lang=\"fr\">organisation</rdfs:label>			    <rdfs:label xml:lang=\"en\">organisation</rdfs:label>			    <rdfs:label xml:lang=\"de\">Organisation</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Galileo_Galilei\">			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Grand_Duchy_of_Tuscany\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/University_of_Padua\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/Sapienza_University_of_Rome\"/>			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Florence\"/>			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Duchy_of_Florence\"/>			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Person\"/>			    <j.0:linksToArtist rdf:resource=\"http://dbpedia.org/resource/Bertolt_Brecht\"/>			    <rdfs:label xml:lang=\"en\">Galileo Galilei</rdfs:label>			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Siena\"/>			    <j.0:linksToArtist rdf:resource=\"http://dbpedia.org/resource/Barrie_Stavis\"/>			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Pisa\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/Queen_%28band%29\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/Rice_University\"/>			    <j.0:linksToPlace rdf:resource=\"http://dbpedia.org/resource/Venice\"/>			    <j.0:linksToArtist rdf:resource=\"http://dbpedia.org/resource/Kim_Stanley_Robinson\"/>			    <j.0:linksToArtist rdf:resource=\"http://dbpedia.org/resource/Arthur_Koestler\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/University_of_Pisa\"/>			    <j.0:linksToOrganisation rdf:resource=\"http://dbpedia.org/resource/International_Astronomical_Union\"/>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Grand_Duchy_of_Tuscany\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			    <rdfs:label xml:lang=\"en\">Grand Duchy of Tuscany</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/ontology/Place\">			    <rdfs:label xml:lang=\"el\">Ï€ÎµÏ�Î¹Î¿Ï‡Î®</rdfs:label>			    <rdfs:label xml:lang=\"en\">place</rdfs:label>			    <rdfs:label xml:lang=\"pt\">lugar</rdfs:label>			    <rdfs:label xml:lang=\"fr\">lieu</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Venice\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			    <rdfs:label xml:lang=\"en\">Venice</rdfs:label>			  </rdf:Description>			  <rdf:Description rdf:about=\"http://dbpedia.org/resource/Florence\">			    <rdf:type rdf:resource=\"http://dbpedia.org/ontology/Place\"/>			    <rdfs:label xml:lang=\"en\">Florence</rdfs:label>			  </rdf:Description>			</rdf:RDF>";		
		return resp;
	}
	
	public static int getDbMaxHeight(){
		int dbMaxHeight = (RootPanel.get("body").getOffsetHeight()*80)/100;
		Debug.printDbgLine("Utility.java: getDbMaxHeight(): "+dbMaxHeight);
		 return dbMaxHeight;
	}
	
	
	public static int getDbMaxWidth(){
		 int dbMaxWidth = RootPanel.get().getOffsetWidth()/3;
		 return dbMaxWidth;
	}
	
}
