package com.sottotesto.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.sottotesto.client.EkpService;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.Utility;


public class EkpServiceImpl extends RemoteServiceServlet implements EkpService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EkpResponse sendToServer(String input) throws IllegalArgumentException {
		Debug.printDbgLine("EkpServiceImpl.java: sendToServer()");
		Debug.printDbgLine("EkpServiceImpl.java: input="+input);

		long StartTime = System.currentTimeMillis();

		JData jdata = new JData();
		EkpResponse result = new EkpResponse();
		Model m = null;
		Map<String,Multimap<String,String>> tag = new HashMap<String,Multimap<String,String>>();
		Multimap<String,String> linkmap = HashMultimap.create();

		m = ModelFactory.createDefaultModel();
		RDFReader arp = null;
		arp = m.getReader();

		try {

			//open TAGME connection
			HttpURLConnection connessione = null;
			InputStream stream = null;
			connessione = (HttpURLConnection)new URL("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input).openConnection();			
			connessione.setRequestMethod("GET");
			connessione.setDoOutput(true);
			connessione.setRequestProperty("Accept", "application/rdf+xml");

			stream = connessione.getInputStream();
			//read TAGME response
			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());
			result.setTag(input);
			Debug.printDbgLine("URL="+connessione.getURL());
			Debug.printDbgLine("EkpServiceImpl.java: respcode="+connessione.getResponseCode());
			//Debug.printDbgLine("EkpServiceImpl.java: respmessage="+connessione.getResponseMessage());
			String responseEkpTemp = "";
			if (result.getContentType().contains("application/rdf+xml")){		
				Scanner inputs = new Scanner(stream);	
				while (inputs.hasNextLine())
					responseEkpTemp += inputs.nextLine();

				inputs.close();
				connessione.disconnect();
				stream.close();
			}else{
				responseEkpTemp = "empty";
			}
			//Debug.printDbgLine("EkpServiceImpl.java: resp="+responseEkpTemp);
			if (responseEkpTemp.isEmpty()){
				result.setRDF("Stringa vuota");
			}else{
				result.setRDF(responseEkpTemp);
				InputStream in = new ByteArrayInputStream(responseEkpTemp.getBytes("UTF-8"));
				arp.read(m, in, null);
				String about = "http://dbpedia.org/resource/"+input;
				Resource link = null;
				link = m.getResource(about);
				StmtIterator i = null;				
				linkmap.clear();

				for (i = link.listProperties(); i.hasNext(); ) {
					Statement s = i.next();					
					//Debug.printDbgLine( "link has property " + s.getPredicate().getLocalName() + " with value " + s.getObject() );
					linkmap.put(s.getPredicate().getLocalName(), s.getObject().toString());
				}
				jdata.setLink(linkmap);
				//Debug.printDbgLine(jdata.getLink().toString());
				tag.clear();
				tag.put(input, jdata.getLink());
				jdata.setTag(tag);

				Gson ekpj = new GsonBuilder().disableHtmlEscaping().create();
				String jresp;
				Gson aa = new GsonBuilder().disableHtmlEscaping().create();

				JsonElement jj = aa.toJsonTree(tag);

				jj.getAsJsonObject().add(input, ekpj.toJsonTree(jdata.getLink().asMap()));

				jresp = aa.toJson(jj);

				Debug.printDbgLine("EkpServiceImpl: json response="+jresp);
				result.jdata=jresp;


			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			result.setCode(-1);
			result.setError("MalformedURLException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			result.setCode(-1);
			result.setError("UnsupportedEncodingException");
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			result.setCode(-1);
			result.setError("ProtocolException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			result.setCode(-1);
			result.setError("IOException");
			e.printStackTrace();
		}
		result.setTime(Utility.calcTimeTookMs(StartTime));

		return result;

	}
}