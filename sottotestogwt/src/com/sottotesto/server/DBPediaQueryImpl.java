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
		String entlink = resp.getLink();
		String spq = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
				"PREFIX grs: <http://www.georss.org/georss/point>\n"+
				"select ?abstract ?grs\n"+
				"where {\n"+
				"<"+entlink+"> rdfs:comment ?abstract .\n"+
				"<"+entlink+"> grs: ?grs .\n"+
				"}";


		try {
			Query query = QueryFactory.create(spq); //s2 = the query above
			QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
			results = qExe.execSelect();
			Literal abs =  null;
			for (; results.hasNext() ;){
				QuerySolution sol = results.nextSolution();
				Literal l = sol.getLiteral("grs");
				String langu = sol.getLiteral("abstract").getLanguage();
				if(langu.contains("en")){		
					gps = l.toString().replace("@en", "");
					if (!gps.isEmpty()){
						abs = sol.getLiteral("abstract");
						resp.setAbstract(abs.getString());
						String arraygps[] = gps.split(" ");
						resp.setGps(Double.parseDouble(arraygps[0]), Double.parseDouble(arraygps[1]));
					}else
						abs=null;
				}else
					abs = null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.printDbgLine("ERRORE DBPEDIA!   "+e.getCause());
		}
		return resp;
	}

}
