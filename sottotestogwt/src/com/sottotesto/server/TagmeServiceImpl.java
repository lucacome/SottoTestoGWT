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
import com.sottotesto.client.TagmeService;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.TagmeResponse;

public class TagmeServiceImpl extends RemoteServiceServlet implements TagmeService {
	
	public TagmeResponse sendToServer(String input) throws IllegalArgumentException {
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer()");
		

		TagmeResponse tagmeResp = new TagmeResponse();
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
				if (output != null) try { output.close(); } 
				catch (IOException err){ tagmeResp.setCode(-1);
										 tagmeResp.setError("Error closing OutputStream");
										 return tagmeResp;}			
				}
			
			//read TAGME response
			tagmeResp.setCode(connessione.getResponseCode());
			tagmeResp.setMessage(connessione.getResponseMessage());
			tagmeResp.setContentType(connessione.getContentType());			
			String responseTagTmp = "";
			if (tagmeResp.getContentType().contains("application/json")){		
				Scanner inputs = new Scanner(connessione.getInputStream());		
				while (inputs.hasNextLine())
					responseTagTmp += (inputs.nextLine());
				inputs.close();		
			}else{
				responseTagTmp = "empty";
			}

			tagmeResp.setJson(responseTagTmp);
			
			//aggiungi la risposta formattata html
			responseTagTmp = "";
			tagmeResp.setJsonNL(tagmeResp.getJson().replaceAll(",", ",\n"));
			
				
			//andava a dbpedia		
			//request.setAttribute("responsetag", responsetag);	
			//request.getRequestDispatcher("/tagme.jsp").forward(request, response);
		} catch (MalformedURLException e) {
			tagmeResp.setCode(-1);
			 tagmeResp.setError("Error MalformedURLException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			tagmeResp.setCode(-1);
			 tagmeResp.setError("Error UnsupportedEncodingException");
			e.printStackTrace();
		} catch (ProtocolException e) {
			tagmeResp.setCode(-1);
			 tagmeResp.setError("Error ProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			tagmeResp.setCode(-1);
			 tagmeResp.setError("Error IOException");
			e.printStackTrace();
		}
		
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer(): "+String.valueOf(tagmeResp.getCode()));
		
		return tagmeResp;
	}

}