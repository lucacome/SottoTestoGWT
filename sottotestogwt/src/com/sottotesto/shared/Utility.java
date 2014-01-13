package com.sottotesto.shared;

import java.io.UnsupportedEncodingException;

import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

public class Utility {

	static AutoProgressMessageBox loadingBox;
	
	public static void showLoadingBar(){
		if(loadingBox == null) loadingBox = new AutoProgressMessageBox("WORKING", "Processing Request, please wait...");
		loadingBox.setProgressText("... Working ...");
		loadingBox.setShadow(true);
		loadingBox.auto();
		loadingBox.show();
	}
	public static void hideLoadingBar(){
		if (loadingBox != null) loadingBox.hide();
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
