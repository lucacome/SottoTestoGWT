package com.sottotesto.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GraphService;
import com.sottotesto.shared.Debug;

public class GraphServiceImp extends RemoteServiceServlet implements GraphService	{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1786697847150590766L;

	public String sendToServer(List<String> fdfinal, List<String> selectedEn) throws IllegalArgumentException {
		
		Debug.printDbgLine("GraphServiceImpl.java: sendToServer()");

		Iterator<String> iterfd = fdfinal.listIterator();
		String response = "";
		List<JData> jdatalist = new ArrayList<JData>();
		JData tempdata = new JData();
		
		while (iterfd.hasNext()){
			String temp = iterfd.next();
			Debug.printDbgLine(temp);
			Gson pa = new Gson();
			JsonParser parser = new JsonParser();

			JsonArray array = parser.parse(temp).getAsJsonArray();

			JData jd = pa.fromJson(array.get(0), JData.class);
			jdatalist.add(jd);
			//response = response + temp +",";
			
		}
		Iterator<JData> jiter = jdatalist.listIterator();
		Debug.printDbgLine("G1");
		tempdata = jiter.next();
		Debug.printDbgLine("G2");

		while (jiter.hasNext()){
			Debug.printDbgLine("G3");
			Iterator<Map<String, String>> iteradj = tempdata.adjacencies.iterator();
			Debug.printDbgLine("G4");
			JData tempdata2 = jiter.next();
			

			while (iteradj.hasNext()){
				Debug.printDbgLine("G5");

				Map<String,String> tempa = iteradj.next();
				Debug.printDbgLine("G6");

				if (tempdata2.adjacencies.contains(tempa))
					Debug.printDbgLine("GraphITER= "+tempa);
				Debug.printDbgLine("G8");

			}
			tempdata = jiter.next();
		}

		Debug.printDbgLine("G9");


//		finas.id = "Nuova";
//		finas.name = "Nuova_name";
		
		

		response = "["+ response + "]";
		//response = jdatalist.get(1).id;
		Debug.printDbgLine("PIPPO_GRAPH= "+response);

		
		// TODO Auto-generated method stub
		return response;
	}

}
