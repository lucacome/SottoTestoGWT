package com.sottotesto.server;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.sottotesto.client.DBPediaService;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.Utility;

public class DBPediaServiceImpl extends RemoteServiceServlet implements DBPediaService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static org.apache.log4j.Logger log= Logger.getLogger(DBPediaServiceImpl.class);

	public DBPediaResponse sendToServer(TagmeResponse tagmResp, List<String> dbprop) throws IllegalArgumentException {
		Debug.printDbgLine("DBPediServiceImpl.java: sendToServer()");


		DBPediaResponse responseQuery;
		ResultSet results = null;
		long StartTime = System.currentTimeMillis();
		responseQuery = new DBPediaResponse();

		String resultQueryXML = "";
		String resultQueryText = "";
		String prefix,prefixlink;
		prefix = "dbo:";
		prefixlink = "<http://dbpedia.org/ontology/>";
		List<String> titletagme = tagmResp.getTitleTag();
		Iterator<String> itertitle =  titletagme.iterator();
		try {


			while (itertitle.hasNext()){

				String titletag = itertitle.next();	

				for (int j=0; j <= dbprop.size()-1; j++){
					String s2 = "PREFIX "+prefix+prefixlink+" \n" +
							"SELECT  ?"+dbprop.get(j)+"\n" +
							"WHERE {\n" +
							"<http://dbpedia.org/resource/" + titletag + "> "+prefix+dbprop.get(j)+" ?"+dbprop.get(j)+" .\n" +
							//"FILTER (LANG(?"+dbprop.get(j)+") = \"en\") .\n"+
							" }";
					Debug.printDbgLine("DBPediaServiceImpl.java: s2="+s2);
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

			}
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