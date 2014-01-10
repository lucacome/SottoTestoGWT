package com.sottotesto.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GHTService;

public class GHTServiceImpl extends RemoteServiceServlet implements GHTService	{



	/**
	 * 
	 */
	private static final long serialVersionUID = 687447506834597456L;

	public String sendToServer(String jsonHT, List<String> selectedLink) throws IllegalArgumentException {
		String response="";
		
		
		return response;
	}

}
