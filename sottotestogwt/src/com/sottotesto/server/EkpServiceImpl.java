package com.sottotesto.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
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
		String temp = "";
		RDFReader rdfReader;
		Model m = ModelFactory.createDefaultModel();
		
		RDFReader arp = m.getReader();
		

	//	String url = "http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input;
		try {
			URL url = new URL ("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input);
		
			//open TAGME connection
			HttpURLConnection connessione = (HttpURLConnection) url.openConnection();
			connessione.setRequestMethod("GET");
			connessione.setDoOutput(true);
			connessione.setRequestProperty("Accept", "application/rdf+xml");

			//read TAGME response
			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());			
			String responseEkpTemp = "";
			if (result.getContentType().contains("application/rdf+xml")){		
				Scanner inputs = new Scanner(connessione.getInputStream());	
				while (inputs.hasNextLine())
					responseEkpTemp += (inputs.nextLine());
				
				inputs.close();		
			}else{
				responseEkpTemp = "empty";
			}
		    Debug.printDbgLine("EkpServiceImpl.java: resp="+responseEkpTemp);
		    result.setMessage(responseEkpTemp);
		    InputStream in = new ByteArrayInputStream(responseEkpTemp.getBytes("UTF-8"));
		    arp.read(m, in, null);
		    String about = "http://dbpedia.org/resource/"+input;
		    Resource link = m.getResource(about);
		    for (StmtIterator i = link.listProperties(); i.hasNext(); ) {
		        Statement s = i.next();
		     
		        Debug.printDbgLine( "link has property " + s.getPredicate() + " with value " + s.getObject() );
		    }
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

		return result;
		
	}
}