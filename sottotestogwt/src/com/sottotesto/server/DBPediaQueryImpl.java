package com.sottotesto.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.sottotesto.client.DBPediaQuery;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.Debug;

public class DBPediaQueryImpl extends RemoteServiceServlet implements DBPediaQuery{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5721089738641161752L;

	@Override
	public DBPQueryResp sendToServer(DBPQueryResp resp)	throws IllegalArgumentException {



		ResultSet results = null;

		String gps = "";
		String spq = "PREFIX grs: <http://www.georss.org/georss/point>\n"+
				"\n"+
				"select ?grs\n"+
				"where {\n"+ 
				"<"+resp.getLink()+"> grs: ?grs\n"+
				"}\n"+
				"";
		
		String spqabs = "PREFIX dbo: <http://dbpedia.org/ontology/>\n"+
				"\n"+
				"select ?abstract\n"+
				"where {\n"+ 
				"<"+resp.getLink()+"> dbo:abstract ?abstract .\n"+
				"\n"+
				"FILTER ( lang(?abstract) = \"en\" )\n"+
				"}\n"+
				"";

		try {
			Query query = QueryFactory.create(spq); //s2 = the query above
			QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
			results = qExe.execSelect();

			if (results.hasNext()){
				QuerySolution sol = results.nextSolution();
				Literal l = sol.getLiteral("grs");
				gps = l.toString().replace("@en", "");
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.printDbgLine("ERRORE DBPEDIA!");
		}

		
		//Debug.printDbgLine("6");
		if (! gps.isEmpty()){
			String arraygps[] = gps.split(" ");
			resp.setGps(Double.parseDouble(arraygps[0]), Double.parseDouble(arraygps[1]));
			
		}
		
		return resp;
	}

}
