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

		String entlink = resp.getLink();
		//long homeTimeStart2 = System.currentTimeMillis();
		String tempDecode = "";
		boolean sameStr = false;

		List <String> allTag = new ArrayList<String>();

		try {
			tempDecode = URLDecoder.decode(entlink.replace("http://dbpedia.org/resource/", ""), "UTF-8");
			if (tempDecode.equals(entlink.replace("http://dbpedia.org/resource/", "")))
				sameStr = true;
			else
				allTag.add(tempDecode);

		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		allTag.add(entlink.replace("http://dbpedia.org/resource/", ""));

		if (!sameStr)
			for (String s : allTag){

				String redirect = "PREFIX dbo: <http://dbpedia.org/ontology/> \n"+
						"SELECT ?uri WHERE {\n" +
						"<http://dbpedia.org/resource/"+ s +">  dbo:wikiPageRedirects ?uri .\n"+
						" }";

				try {
					Query query3 = QueryFactory.create(redirect); //s2 = the query above
					QueryExecution qExe3 = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query3 );
					ResultSet results3 = qExe3.execSelect();

					if (results3.hasNext()){
						QuerySolution sol = results3.nextSolution();
						entlink = sol.getResource("uri").toString();
						Debug.printDbgLine("REDIRECT - DBPediaQuery= "+entlink);
						break;
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		//long homeTimeStop2 = System.currentTimeMillis()-homeTimeStart2;
		//Debug.printDbgLine("DBPedia Nuove chiamate="+homeTimeStop2);
		ResultSet results = null;

		String gps = "";
		String spq = "";
		if (sameStr){
			spq = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
					"PREFIX dbo: <http://dbpedia.org/ontology/> "+
					"PREFIX grs: <http://www.georss.org/georss/point>\n"+
					"select ?abstract ?grs\n"+
					"where {\n"+
					"{\n"+
					"<"+entlink+"> rdfs:comment ?abstract .\n"+
					"<"+entlink+"> grs: ?grs .\n"+
					"}\n"+
					"union \n"+
					"{\n"+
					"<"+entlink+"> dbo:wikiPageRedirects ?uri .\n"+
					"?uri rdfs:comment ?abstract .\n"+
					"?uri grs: ?grs .\n"+
					"}\n"+
					"}";
		}
		else{
			spq = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
					"PREFIX grs: <http://www.georss.org/georss/point>\n"+
					"select ?abstract ?grs\n"+
					"where {\n"+
					"<"+entlink+"> rdfs:comment ?abstract .\n"+
					"<"+entlink+"> grs: ?grs .\n"+
					"}";

		}
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
			resp.setSuccess(false);
			resp.setError(e.getClass().getName());
		}
		return resp;
	}

}
