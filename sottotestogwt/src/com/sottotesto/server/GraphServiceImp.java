package com.sottotesto.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GraphService;
import com.sottotesto.shared.Debug;

public class GraphServiceImp extends RemoteServiceServlet implements GraphService	{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1786697847150590766L;

	public String sendToServer(List<String> fdfinal) throws IllegalArgumentException {
		
		Debug.printDbgLine("GraphServiceImpl.java: sendToServer()");


		String response = "";
		String jsonFD = "";
		
//		
//		Gson pa = new Gson();
//		JsonParser parser = new JsonParser();
//
//		JsonArray array = parser.parse(jsonFD).getAsJsonArray();
//
//		JData jd = pa.fromJson(array.get(0), JData.class);
//


		response = "pippo";
		Debug.printDbgLine("PIPPO_GRAPH= "+response);

		
		// TODO Auto-generated method stub
		return response;
	}

}
