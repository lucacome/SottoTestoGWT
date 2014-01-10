package com.sottotesto.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GraphService;
import com.sottotesto.shared.Debug;

public class GHTServiceImpl extends RemoteServiceServlet implements GraphService	{



	/**
	 * 
	 */
	private static final long serialVersionUID = 687447506834597456L;

	public String sendToServer(List<String> fdfinal, List<String> selectedEn) throws IllegalArgumentException {
		String response="";
		
		
		return response;
	}

}
