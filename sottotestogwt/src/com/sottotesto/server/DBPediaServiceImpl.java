package com.sottotesto.server;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
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
	private static org.apache.log4j.Logger log= Logger.getLogger(DBPediaServiceImpl.class);

	public DBPediaResponse sendToServer(TagmeResponse tagmResp, List<String> dbprop) throws IllegalArgumentException {
		Debug.printDbgLine("DBPediServiceImpl.java: sendToServer()");

		long StartTime = System.currentTimeMillis();
		DBPediaResponse responseQuery = new DBPediaResponse();
		ResultSet results = null;
		String resultQueryXML = "";
		String resultQueryText = "";
		List<String> titletagme = tagmResp.getTitleTag();
		Iterator<String> itertitle =  titletagme.iterator();

		while (itertitle.hasNext()){

			String titletag = itertitle.next();	

			for (int j=0; j <= dbprop.size()-1; j++){
				String s2 = "PREFIX  dbpprop: <http://dbpedia.org/property/>\n" +
						"\n" +
						"SELECT  *\n" +
						"WHERE {\n" +
						"<http://dbpedia.org/resource/" + titletag + "> dbpprop:"+dbprop.get(j)+" ?"+dbprop.get(j)+" .\n" +
						"  }\n" +	            
						"";
				//   Debug.printDbgLine("DBPediaServiceImpl.java: s2="+s2);
				Query query2 = QueryFactory.create(s2); //s2 = the query above
				QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query2 );
				results = qExe.execSelect();
				//TODO fix xmlresult
				resultQueryXML += ResultSetFormatter.asXMLString(results);
				resultQueryText += ResultSetFormatter.asText(results);
			}
			//TODO output in json

		}

		responseQuery.setQueryResultXML(resultQueryXML);
		responseQuery.setQueryResultText(resultQueryText);

		responseQuery.setTime(Utility.calcTimeTookMs(StartTime));
		Debug.printDbgLine("DBPediaServiceImpl.java: sendToServer(): END -> ["+responseQuery.getTime()+"ms]");

		return responseQuery;

	}
}