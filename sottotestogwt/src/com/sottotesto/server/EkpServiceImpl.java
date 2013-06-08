package com.sottotesto.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.EkpService;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;

public class EkpServiceImpl extends RemoteServiceServlet implements EkpService {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EkpResponse sendToServer(String input) throws IllegalArgumentException {
		Debug.printDbgLine("EkpServiceImpl.java: sendToServer()");
		
		EkpResponse result = new EkpResponse();
				
		return result;
		
	}
}
