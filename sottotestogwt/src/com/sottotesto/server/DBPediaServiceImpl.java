package com.sottotesto.server;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.sottotesto.client.DBPediaService;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.TagmeData;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.Utility;

public class DBPediaServiceImpl extends RemoteServiceServlet implements DBPediaService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static org.apache.log4j.Logger log= Logger.getLogger(DBPediaServiceImpl.class);

	public DBPediaResponse sendToServer(TagmeData data) throws IllegalArgumentException {
		Debug.printDbgLine("DBPediServiceImpl.java: sendToServer()");
		
		long StartTime = System.currentTimeMillis();

		DBPediaResponse resultquery2 = new DBPediaResponse();
		
		/*
	
		TagmeData data = new TagmeData();
		Gson gson = new Gson();
		String responsetag = input;
		
				//request.getAttribute("responsetag").toString();
		data = gson.fromJson(responsetag, TagmeData.class);
		Debug.printDbgLine("DBPediServiceImpl.java: dopo gson");
		ResultSet results = null;
		String resultquery = "";
		for (int i=0; i<=data.annotations.size()-1; i++){
			String titletag = data.annotations.get(i).title;	
		
		
		titletag = titletag.replaceAll(" ", "_");
		
	    String s2 = "PREFIX  dbpprop: <http://dbpedia.org/property/>\n" +
	    		"\n" +
	    		"SELECT  *\n" +
	            "WHERE {\n" +
	            "<http://dbpedia.org/resource/" + titletag + "> dbpprop:placeOfBirth ?nat .\n" +
	            "  }\n" +
	            "";

	    Query query2 = QueryFactory.create(s2); //s2 = the query above
	    QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query2 );
	    results = qExe.execSelect(); 
	    resultquery += ResultSetFormatter.asXMLString(results);
//	    ResultSetFormatter.outputAsJSON(results);
	        
//	    resultquery += results;
		}
//	    String temp = String.valueOf(data.annotations.size());
		
		//request.setAttribute("dbpOutput", resultquery);  
//		request.setAttribute("hello_string", titletag);  

		//request.getRequestDispatcher("/dbpedia.jsp").forward(request, response);
		resultquery2.setQueryResult(resultquery);
		
		*/
		resultquery2.setTime(Utility.calcTimeTookMs(StartTime));
		Debug.printDbgLine("DBPediaServiceImpl.java: sendToServer(): END -> ["+resultquery2.getTime()+"ms]");
				
		return resultquery2;
		
	}
}
