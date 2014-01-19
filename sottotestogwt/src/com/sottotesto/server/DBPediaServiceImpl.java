package com.sottotesto.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.sottotesto.client.DBPediaService;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.Utility;

public class DBPediaServiceImpl extends RemoteServiceServlet implements DBPediaService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static org.apache.log4j.Logger log= Logger.getLogger(DBPediaServiceImpl.class);

	public DBPediaResponse sendToServer(String tagmResp, List<String> dbprop, String type) throws IllegalArgumentException {
		Debug.printDbgLine("DBPediServiceImpl.java: sendToServer()");
		Debug.printDbgLine("DBPediServiceImpl.java: type="+type);

		DBPediaResponse responseQuery;
		ResultSet results = null;
		long StartTime = System.currentTimeMillis();
		responseQuery = new DBPediaResponse();


		try {
			responseQuery.setEntity(URLDecoder.decode(tagmResp, "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		responseQuery.setEntityType(type);
		String resultQueryXML = "";
		String resultQueryText = "";
		String prefix,prefixlink;
		prefix = "dbo:";
		prefixlink = "<http://dbpedia.org/ontology/>";
		//		List<String> titletagme = tagmResp.getTitleTag();
		//		Iterator<String> itertitle =  titletagme.iterator();
		try {


			//			while (itertitle.hasNext()){

			String titletag = tagmResp;	
			List <String> allTag = new ArrayList<String>();
			String tempDecode = "";

			tempDecode = URLDecoder.decode(titletag, "UTF-8");
			if (tempDecode.equals(titletag))
				Debug.printDbgLine("DBP Stringa uguale: "+titletag);
			else
				allTag.add(tempDecode);

			allTag.add(titletag);


			for (String s : allTag){

				String redirect = "PREFIX dbo: <http://dbpedia.org/ontology/> \n"+
						"SELECT ?uri WHERE {\n" +
						"<http://dbpedia.org/resource/"+ s +">  dbo:wikiPageRedirects ?uri .\n"+
						" }";

				Query query3 = QueryFactory.create(redirect); //s2 = the query above
				QueryExecution qExe3 = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query3 );
				ResultSet results3 = qExe3.execSelect();

				if (results3.hasNext()){
					QuerySolution sol = results3.nextSolution();
					titletag = sol.getResource("uri").toString().replace("http://dbpedia.org/resource/", "");
					Debug.printDbgLine("REDIRECT - DBPediaService= "+titletag);
					break;
				}
			}

			for (int j=0; j <= dbprop.size()-1; j++){
				String s2 = "PREFIX "+prefix+prefixlink+" \n" +
						"SELECT  ?"+dbprop.get(j)+"\n" +
						"WHERE {\n" +
						"<http://dbpedia.org/resource/" + titletag + "> "+prefix+dbprop.get(j)+" ?"+dbprop.get(j)+" .\n" +
						//"FILTER (LANG(?"+dbprop.get(j)+") = \"en\") .\n"+
						" }";
				//				Debug.printDbgLine("DBPediaServiceImpl.java: s2="+s2);
				Query query2 = QueryFactory.create(s2); //s2 = the query above
				QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query2 );
				results = qExe.execSelect();
				//TODO fix xmlresult
				Literal abs =  null;
				for (; results.hasNext() ;){
					QuerySolution sol = results.nextSolution();
					String langu = sol.getLiteral(dbprop.get(j)).getLanguage();

					if(langu.contains("en")){	
						abs = sol.getLiteral(dbprop.get(j));
						resultQueryXML +=  abs.getString();
						Debug.printDbgLine(abs.getString());
					}else
						abs = null;
				}


				//resultQueryXML += ResultSetFormatter.asXMLString(results);
				//resultQueryText += ResultSetFormatter.asText(results);

			}
			//TODO output in json


			responseQuery.setQueryResultXML(resultQueryXML);
			responseQuery.setQueryResultText(resultQueryText);

			Debug.printDbgLine("DBPediaServiceImpl.java: sendToServer(): END -> ["+responseQuery.getTime()+"ms]");
		} catch (Exception e) {			
			Debug.printDbgLine("DBPediaServiceImpl.java: Exception: "+e.getMessage());
			e.printStackTrace();
			//set error code
			responseQuery.setCode(500);

			//set error message
			if (e.getMessage().contains("imeout")){responseQuery.setError("Timeout contacting DBPedia");}
			if (e.getMessage().contains("IOException")){responseQuery.setError("Unable to fetch URL");}
			else responseQuery.setError("Error processing Request");

			responseQuery.setTime(Utility.calcTimeTookMs(StartTime));
			return responseQuery;			
		}

		responseQuery.setCode(200);
		responseQuery.setTime(Utility.calcTimeTookMs(StartTime));
		return responseQuery;

	}
}