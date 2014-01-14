package com.sottotesto.server;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.ListService;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.EkpResponse;

public class ListServiceImpl extends RemoteServiceServlet implements ListService	{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6701602502729720643L;

	@Override
	public List<DBPQueryResp> sendToServer(EkpResponse resp)
			throws IllegalArgumentException {


		String jsonHT = "["+resp.jdataHT+"]";
		Gson pa = new Gson();
		JsonParser parser = new JsonParser();

		JsonArray array = parser.parse(jsonHT).getAsJsonArray();
		List<DBPQueryResp> response = new ArrayList<DBPQueryResp>();
		DBPQueryResp dbpqresp = new DBPQueryResp();

		JData jd = pa.fromJson(array.get(0), JData.class);

		String dblink = jd.id;

		String entity = jd.name;

		dbpqresp.setEntity(resp.getTag());
		dbpqresp.setLink(dblink);
		dbpqresp.setName(entity);
		response.add(dbpqresp);


		String relation = "";
		for ( int i=1; i<array.size(); i++){
			dbpqresp = new DBPQueryResp();
			JData jdata = new JData();
			jdata =	pa.fromJson(array.get(i), JData.class);
			if (jdata.id.contains("http")){
				dbpqresp.setEntity(entity);
				dbpqresp.setName(jdata.name);
				dbpqresp.setLink(jdata.id);
				dbpqresp.setRelation(relation);
				response.add(dbpqresp);
			}else{
				relation = jdata.id;
			}
		}
		// TODO Auto-generated method stub
		return response;
	}

}
