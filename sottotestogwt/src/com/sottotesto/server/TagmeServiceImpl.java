package com.sottotesto.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GeneralService;
import com.sottotesto.shared.Debug;

public class TagmeServiceImpl extends RemoteServiceServlet implements GeneralService {
	
	public String sendToServer(String input) throws IllegalArgumentException {
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer()");
		
		
		//config & view JSP
		String tagmeResp = "";
		try {
			//config TAGME request parameters
			URL url = new URL ("http://tagme.di.unipi.it/tag");
			String charset = "UTF-8";
			String param1name = "text";
			String param1value = input;
			String param2name = "key";
			String param2value = "plclcd321";
			String param3name = "include_categories";
			String param3value = "true";	
			String query = String.format("%s=%s&%s=%s&%s=%s", URLEncoder.encode(param1name, charset), URLEncoder.encode(param1value, charset), URLEncoder.encode(param2name, charset), URLEncoder.encode(param2value, charset), URLEncoder.encode(param3name, charset), URLEncoder.encode(param3value, charset));
			
			//open TAGME connection
			HttpURLConnection connessione = (HttpURLConnection) url.openConnection();
			connessione.setRequestMethod("POST");
			connessione.setDoOutput(true);

			//get TAGME response
			OutputStream output = null;
			try {
				output = connessione.getOutputStream();
				output.write(query.getBytes(charset));
				} 
			finally {
				if (output != null) try { output.close(); } catch (IOException err){}			
				}
			
			//read TAGME response
			int rspCode = connessione.getResponseCode();
			String messaggio = connessione.getResponseMessage(); 
			String contenttype = connessione.getContentType();	
			String responsetag = "";
			if (contenttype.contains("application/json")){		
				Scanner inputs = new Scanner(connessione.getInputStream());		
				while (inputs.hasNextLine())
					responsetag += (inputs.nextLine());
				inputs.close();		
			}else{
				responsetag = "nessuna stringa";
			}

			tagmeResp = "";
			String json = responsetag.replaceAll(",", ",<br>");
			tagmeResp = "Code: "+String.valueOf(rspCode)+"\n";
			tagmeResp += "messaggio: "+messaggio+"\n";
			tagmeResp += "contenttype: "+contenttype+"\n";
					tagmeResp += "json: "+json+"\n";
				
			//andava a dbpedia		
			//request.setAttribute("responsetag", responsetag);	
			//request.getRequestDispatcher("/tagme.jsp").forward(request, response);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer(): "+tagmeResp);
		
		return tagmeResp;
	}

}
