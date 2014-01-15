package com.sottotesto.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GHTService;

public class GHTServiceImpl extends RemoteServiceServlet implements GHTService	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 687447506834597456L;

	public String sendToServer(String jsonHT, List<String> selectedLink) throws IllegalArgumentException {
		String response="";



		Gson pa = new Gson();
		JsonParser parser = new JsonParser();
		Gson aa = new GsonBuilder().disableHtmlEscaping().create();

		JsonArray array = parser.parse(jsonHT).getAsJsonArray();

		JData jd = pa.fromJson(array.get(0), JData.class);
		
		jd.adjacencies.clear();
		for (String s : selectedLink){
			Map<String, String> data = new HashMap<String, String>();
			data.put("nodeTo", s);
			jd.adjacencies.add(data);
		}

		
		response += aa.toJson(jd)+ ",";

		for ( int i=1; i<array.size(); i++){
			JData jdata = new JData();
			jdata =	pa.fromJson(array.get(i), JData.class);
			Iterator<String> linkiter = selectedLink.listIterator();
			while(linkiter.hasNext())
				if (jdata.id.contains(linkiter.next())){
					response += aa.toJson(jdata) + ",";
					for (int j = i +1 ; j<array.size(); j++){
						jdata =	pa.fromJson(array.get(j), JData.class);
						if (jdata.id.contains("http"))
							response += aa.toJson(jdata) + ",";
						else{
							i=j;
							break;
						}
					}
				}
		}
		response = "[" + response.substring(0, response.length()-1) + "]";
		//Debug.printDbgLine(response);

		return response;
	}

}
