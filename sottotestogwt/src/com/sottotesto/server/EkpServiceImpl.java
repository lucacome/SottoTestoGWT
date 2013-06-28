package com.sottotesto.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;



import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multiset;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
		String jresp = null;


		try {

			//open TAGME connection
			HttpURLConnection connessione = null;
			InputStream stream = null;
			connessione = (HttpURLConnection)new URL("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input).openConnection();
			connessione.setRequestMethod("GET");
			connessione.setDoOutput(true);
			connessione.setRequestProperty("Accept", "application/rdf+xml");

			stream = connessione.getInputStream();

			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());

			result.setTag(input);
			Debug.printDbgLine("URL="+connessione.getURL());
			Debug.printDbgLine("EkpServiceImpl.java: respcode="+connessione.getResponseCode());
			//Debug.printDbgLine("EkpServiceImpl.java: respmessage="+connessione.getResponseMessage());
			String responseEkpTemp = "";
			if (result.getContentType().contains("application/rdf+xml") && result.getCode() > 0){		
				Scanner inputs = new Scanner(stream);	
				while (inputs.hasNextLine())
					responseEkpTemp += inputs.nextLine();

				inputs.close();
				connessione.disconnect();
				stream.close();
			}
			//Debug.printDbgLine("EkpServiceImpl.java: resp="+responseEkpTemp);
			if (responseEkpTemp.isEmpty()){
				result.setRDF("Stringa vuota");
			}else{
				result.setRDF(responseEkpTemp);

				InputStream in = new ByteArrayInputStream(responseEkpTemp.getBytes("UTF-8"));
				arp.read(m, in, null);
				String about = "http://dbpedia.org/resource/"+input;
				//jeppo.addProperty("id", about);
				//jeppo.addProperty("name", input.replace("_", " "));
				jdata.id = about;
				Map<String, String> dop3 = new HashMap<String,String>();
				dop3.put("$type", "square");
				dop3.put("$color", "#BD1B89");
				jdata.data.add(dop3);

				
				Resource link = null;
				link = m.getResource(about);
				StmtIterator i = null;				
				linkmap.clear();
				String jcompleto = null;

				for (i = link.listProperties(); i.hasNext(); ) {
					Statement s = i.next();					
					//Debug.printDbgLine( "link has property " + s.getPredicate().getLocalName().replace("linksTo", "") + " with value " + s.getObject() );
					linkmap.put(s.getPredicate().getLocalName(), s.getObject().toString());
				}
				for (Object key : linkmap.keySet()){
					if (key.toString().contains("type"))
						jdata.type = key.toString();
					else if (key.toString().contains("label"))
						jdata.name = input;
					else{
					//Debug.printDbgLine("uhm");
					Map<String, String> dop = new HashMap<String,String>();
					dop.put("nodeTo", key.toString().replace("linksTo", ""));
					//dop.put("data", "cacca");
					jdata.adjacencies.add(dop);
					Collection<String> boh = linkmap.asMap().get(key);
					Iterator<String> iter;
					iter = boh.iterator();
					JData jsub = new JData();
					jsub.id = key.toString().replace("linksTo", "");
					jsub.name = jsub.id;
					jsub.data.add(dop3);
					while (iter.hasNext()){
						Map<String, String> dop2 = new HashMap<String,String>();
						dop2.put("nodeTo", iter.next().toString());
						jsub.adjacencies.add(dop2);
						//Debug.printDbgLine(key.toString()+"="+iter.next().toString());
					}
					Gson tempj = new GsonBuilder().disableHtmlEscaping().create();
					String temresp;
					temresp = tempj.toJson(jsub);
					//Debug.printDbgLine(temresp);
					if (jresp == null)
						jresp = temresp;
					else
						jresp = jresp+","+temresp;
					}
					//Debug.printDbgLine("dopo");
				}
				jdata.setLink(linkmap);
				//Debug.printDbgLine(jdata.getLink().toString());
				tag.clear();
				tag.put(input, jdata.getLink());
		//		jdata.setTag(tag);

				Gson ekpj = new GsonBuilder().disableHtmlEscaping().create();
				Gson aa = new GsonBuilder().disableHtmlEscaping().create();
				
		
				//JsonElement jj = aa.toJsonTree(tag);
				

				//jj.getAsJsonObject().add(input, ekpj.toJsonTree(jdata.getLink().asMap()));

				//jresp = aa.toJson(jj);
				if (jresp == null)
					jresp = aa.toJson(jdata);
				else
					jresp =  jresp + "," + aa.toJson(jdata);

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