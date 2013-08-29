package com.sottotesto.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.sottotesto.client.DBPediaQuery;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;

public class DBPediaQueryImpl extends RemoteServiceServlet implements DBPediaQuery{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5721089738641161752L;

	@Override
	public List<DBPQueryResp> sendToServer(EkpResponse resp)	throws IllegalArgumentException {


		String jsonHT = "["+resp.jdataHT+"]";
		Debug.printDbgLine("1");
		Gson pa = new Gson();
		JsonParser parser = new JsonParser();

		JsonArray array = parser.parse(jsonHT).getAsJsonArray();
		Debug.printDbgLine("2");
		List<DBPQueryResp> response = new ArrayList<DBPQueryResp>();
		Debug.printDbgLine("3");
		DBPQueryResp dbpqresp = new DBPQueryResp();
		Debug.printDbgLine("4");

		JData jd = pa.fromJson(array.get(0), JData.class);
		Debug.printDbgLine("5");
		String dblink = jd.id;

		String entity = jd.name;
		ResultSet results = null;

		String spq = "PREFIX grs: <http://www.georss.org/georss/point>\n"+
				"\n"+
				"select ?grs\n"+
				"where {\n"+ 
				"<"+dblink+"> grs: ?grs\n"+
				"}\n"+
				"";

		try {
			Query query2 = QueryFactory.create(spq); //s2 = the query above
			QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query2 );
			results = qExe.execSelect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

		int sizeq = 0;
		sizeq = ResultSetFormatter.toList(results).size();
		String gps = "";
		if (sizeq > 0)
			gps = ResultSetFormatter.toList(results).get(0).getLiteral("grs").getString();

		Debug.printDbgLine("6");
		if (! gps.isEmpty()){
			dbpqresp.setEntity(entity);
			String arraygps[] = gps.split(" ");
			dbpqresp.setGps(Double.parseDouble(arraygps[0]), Double.parseDouble(arraygps[1]));
			response.add(dbpqresp);
		}
		String linkdb = "";
		for ( int i=1; i<array.size(); i++){
			JData jdata = new JData();
			jdata =	pa.fromJson(array.get(i), JData.class);
			//Debug.printDbgLine("prova="+jdata.id);
			if (jdata.id.contains("http")){
				//Debug.printDbgLine("Jdata contiene http");
				ResultSet results2 = null;

				String spq2 = "PREFIX grs: <http://www.georss.org/georss/point>\n"+
						"\n"+
						"select ?grs\n"+
						"where {\n"+ 
						"<"+jdata.id+"> grs: ?grs\n"+
						"}\n"+
						"";
				String gpsi = "";

				try {
					Query query = QueryFactory.create(spq2); //s2 = the query above
					QueryExecution qExe2 = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
					results2 = qExe2.execSelect();

					int sizeq2 = 0;
					if (results2.hasNext()){
						QuerySolution sol = results2.nextSolution();
						Literal l = sol.getLiteral("grs");
						gpsi = l.toString().replace("@en", "");
						//sizeq2 = ResultSetFormatter.toList(results2).size();
					//	Debug.printDbgLine("Size secondo="+l);
						//	        		Debug.printDbgLine(ResultSetFormatter.asText(results2));
						//	        		if (sizeq2 > 0)
						//	        			gpsi = ResultSetFormatter.toList(results2).get(0).getLiteral("grs").getString();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



				//Debug.printDbgLine("25");
				if (! gpsi.isEmpty()){
					dbpqresp = new DBPQueryResp();
					dbpqresp.setEntity(entity);
					dbpqresp.setName(jdata.name);
					dbpqresp.setLink(linkdb);
					String arraygps[] = gpsi.split(" ");
					dbpqresp.setGps(Double.parseDouble(arraygps[0]), Double.parseDouble(arraygps[1]));
					response.add(dbpqresp);
				//	Debug.printDbgLine("GPSI="+gpsi);
				}

			}else{
				Debug.printDbgLine("Jdata NON contiene http");

				linkdb = jdata.id;
			}

		}


		return response;
	}

}
