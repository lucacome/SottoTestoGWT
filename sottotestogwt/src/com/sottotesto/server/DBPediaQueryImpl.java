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
//		ResultSet results2 = null;
				Debug.printDbgLine("1");

		String gps = "";
		String entlink = resp.getLink();
		String spq = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
				"PREFIX grs: <http://www.georss.org/georss/point>  "+
				"select ?grs ?abstract "+
				"where { "+
				"<"+entlink+"> rdfs:comment ?abstract . "+
				"<"+entlink+"> grs: ?grs . "+
				"} \n ";
		Debug.printDbgLine("2");
		//		String spqabs = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		//				"select ?abstract\n"+
		//				"where {\n"+
		//				"<"+resp.getLink()+"> rdfs:comment ?abstract.\n"+
		////				"FILTER langMatches( lang(?abstract), 'en')"+
		//				"}";

		try {
			Query query = QueryFactory.create(spq); //s2 = the query above
			QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
			results = qExe.execSelect();
			Literal abs =  null;
//			Debug.printDbgLine("3");
			for (; results.hasNext() ;){
				QuerySolution sol = results.nextSolution();
//				Debug.printDbgLine("4");
				Literal l = sol.getLiteral("grs");
//				Debug.printDbgLine("GRS= "+l);
//				Debug.printDbgLine("5");
				String langu = sol.getLiteral("abstract").getLanguage();
				if(langu.contains("en")){				
//					Debug.printDbgLine("6");	
					
					gps = l.toString().replace("@en", "");
//					Debug.printDbgLine("7");
					if (!gps.isEmpty()){
//						Debug.printDbgLine("8");
						abs = sol.getLiteral("abstract");
						resp.setAbstract(abs.getString());
						

						String arraygps[] = gps.split(" ");
						resp.setGps(Double.parseDouble(arraygps[0]), Double.parseDouble(arraygps[1]));
					}else
						abs=null;
					//Debug.printDbgLine("ABS= "+abs);
				}else
					abs = null;



			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Debug.printDbgLine("ERRORE DBPEDIA!   "+e.getCause());
		}


		//		try {
		////			Debug.printDbgLine("4");
		//			Query query2 = QueryFactory.create(spqabs); //s2 = the query above
		////			Debug.printDbgLine("5");
		//			QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query2 );
		////			Debug.printDbgLine("6");
		//			results2 = qExe.execSelect();
		////			Debug.printDbgLine("7");
		//			Literal abs =  null;
		//
		//			for (; results2.hasNext() ;){
		////				Debug.printDbgLine("8");
		//				QuerySolution sol2 = results2.nextSolution();
		////				Debug.printDbgLine("9");
		//				String langu = sol2.getLiteral("abstract").getLanguage();
		//				if(langu.contains("en")){
		//					abs = sol2.getLiteral("abstract");
		//					resp.setAbstract(abs.getString());
		//					//Debug.printDbgLine("ABS= "+abs);
		//				}else
		//					abs = null;
		//
		//			}
		////			Debug.printDbgLine("11");
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			//e.printStackTrace();
		//			Debug.printDbgLine("ERRORE2 DBPEDIA!   "+e.getCause());
		//		}
		//		



		return resp;
	}

}
