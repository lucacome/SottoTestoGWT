package com.sottotesto.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.HashMultimap;
import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;
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
		Debug.printDbgLine("EkpServiceImpl.java: sendToServer("+input+")");
		long StartTime = System.currentTimeMillis();

		JData jsonHT = new JData();
		JData jsonFD = new JData();
		EkpResponse result = new EkpResponse();
		Model m = null;
		Map<String,Multimap<String,String>> tag = new HashMap<String,Multimap<String,String>>();
		Multimap<String,String> linkmap = HashMultimap.create();

		m = ModelFactory.createDefaultModel();
		RDFReader arp = null;
		arp = m.getReader();
		String jresp = null;
		String type = "";
		List<String> linkList = new ArrayList<String>();
		String responseEkpTemp = "";

		try {
			HttpURLConnection connessione = (HttpURLConnection)new URL("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input+"?dbpedia-version=3.8").openConnection();
			connessione.setRequestMethod("GET");
			connessione.setRequestProperty("Accept-Charset", "utf-8");
			connessione.setRequestProperty("Accept", "application/rdf+xml; charset=utf-8");
			connessione.setDoOutput(true);

			result.setEncodedTag(input);
			result.setTag(URLDecoder.decode(input, "UTF-8"));
			InputStream stream = connessione.getInputStream();
			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());

			Debug.printDbgLine("URL="+connessione.getURL());
			Debug.printDbgLine("EkpServiceImpl.java: respcode for "+result.getTag()+"="+connessione.getResponseCode());
			try{
				if (connessione.getContentType().contains("application/rdf+xml") && connessione.getResponseCode() == 200){
					arp.read(m, stream, null);
				}
			}finally{
				stream.close();
				connessione.disconnect();
			}

		} catch (MalformedURLException e) {
			result.setCode(400);
			result.setError("MalformedURLException\n"+Utility.getErrorHtmlString(e));
			Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());			
		} catch (IOException e) {
			result.setCode(408);
			result.setError("IOException\n"+Utility.getErrorHtmlString(e));
			Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());
		}
		if (result.getCode() != 200){
			result.setRDF("Stringa vuota, Code="+result.getCode());
			result.setError("Risposta vuota");
			Debug.printErrLine("EkpServiceImpl.java: Error= Stringa vuota");
		}else{
			result.setRDF(responseEkpTemp);

			String about = "http://dbpedia.org/resource/"+input;
			Map<String, String> mapDataFD = new HashMap<String,String>();
			String jfd = null;
			jsonHT.id = about;
			jsonFD.id = about;
			mapDataFD.put("$type", "square");
			mapDataFD.put("$color", "#BD1B89");
			mapDataFD.put("$dim", "9");
			jsonFD.data = mapDataFD;
			Resource connection = null;
			Resource link = null;
			link = m.getResource(about);
			StmtIterator i = new StmtIteratorImpl(null);				
			linkmap.clear();
			Map<String,String> linklabel = new HashMap<String,String>();
			for (i = link.listProperties(); i.hasNext(); ) {
				Statement s = i.next();
				if (s.getPredicate().getLocalName().contains("label")){
					try {
						jsonHT.name = URLDecoder.decode(s.getObject().toString().replace("@en", ""),"UTF-8");
					} catch (UnsupportedEncodingException e) {
						Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());
					}
					jsonFD.name = jsonHT.name;	
				}
				if (s.getPredicate().getLocalName().contains("type")){
					type=s.getObject().toString();
					if (!type.isEmpty() && type.contains("http://dbpedia.org/ontology/"))
						result.setType(type.substring(type.lastIndexOf('/')+1));
					else if (!type.isEmpty() && type.contains("http://www.w3.org/2002/07/owl#"))
						result.setType(type.substring(type.lastIndexOf('#')+1));
					else
						type = "";

				}
				linkmap.put(s.getPredicate().getLocalName(), s.getObject().toString());
				Resource oth = null;
				oth = m.getResource(s.getObject().toString());
				StmtIterator a = null;

				for (a = oth.listProperties(); a.hasNext(); ){
					Statement z = a.next();
					if (z.getPredicate().getLocalName().contains("label") && z.getObject().toString().contains("@en"))
						linklabel.put(s.getObject().toString(), z.getObject().toString().replace("@en", ""));
				}	
			}

			RDFReader arp2 = null;
			Model m2 = ModelFactory.createDefaultModel();
			arp2 = m2.getReader();

			String type2 = m.getNsPrefixURI("j.0");
			if (type.isEmpty()){
				if (type2 != null){
					result.setType(type2.substring(type2.lastIndexOf('/')+1).replace(".owl#", ""));
				}
			}
			
			if (type2 != null){
				HttpURLConnection connessione2 = null;
				InputStream stream2 = null;
				try {
					connessione2 = (HttpURLConnection)new URL(type2).openConnection();
					connessione2.setRequestMethod("GET");
					connessione2.setDoOutput(true);
					connessione2.setRequestProperty("Accept", "application/rdf+xml");
					stream2 = connessione2.getInputStream();

					arp2.read(m2, stream2, type2);
					connessione2.disconnect();
					stream2.close();
				} catch (MalformedURLException e) {
					Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());
				} catch (IOException e) {
					Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());
				}
			}


			for (Object key : linkmap.keySet()){
				if (key.toString().contains("type"))
					jsonHT.type = key.toString();
				else if (key.toString().contains("label")){
					//non fa niente
				}else{
					linkList.add(key.toString());
					Map<String, String> mapNodeHT = new HashMap<String,String>();

					mapNodeHT.put("nodeTo", key.toString());
					jsonHT.adjacencies.add(mapNodeHT);
					Collection<String> boh = linkmap.asMap().get(key);
					Iterator<String> iter;
					iter = boh.iterator();
					JData jsonHTsub = new JData();
					String jlast = null;

					if (!m2.isEmpty()){
						connection = m2.getResource(type2+key.toString());
					}else
						connection = null;

					StmtIterator i2 = null;
					String comment = "";
					String label = "";
					String range = "";

					if (connection != null)
						for (i2 = connection.listProperties(); i2.hasNext(); ) {
							Statement s2 = i2.next();
							if (s2.getObject().toString().contains("@en") && s2.getPredicate().toString().contains("comment")){
								comment = s2.getObject().toString().replace("@en", "");
							}
							if (s2.getObject().toString().contains("@en") && s2.getPredicate().toString().contains("label")){
								try {
									label = URLDecoder.decode(s2.getObject().toString().replace("@en", ""),"UTF-8");
								} catch (UnsupportedEncodingException e) {
									Debug.printErrLine("EkpServiceImpl.java: Error="+e.getClass().getName());
								}
							}
							if (s2.getPredicate().toString().contains("range")){
								range = s2.getObject().toString().replace("http://dbpedia.org/ontology/", "");
							}
						}


					jsonHTsub.id = key.toString();
					jsonHTsub.name = range;
					jsonHTsub.data.put("$type", "triangle");
					jsonHTsub.data.put("$color", "#EB6F24");
					jsonHTsub.data.put("comment", comment);
					jsonHTsub.data.put("label", label);
					jsonHTsub.data.put("relation", key.toString());

					while (iter.hasNext()){
						Map<String, String> mapNodeHTsub = new HashMap<String,String>();
						String node= iter.next().toString();
						mapNodeHTsub.put("nodeTo", node);
						jsonHTsub.adjacencies.add(mapNodeHTsub);
						jsonFD.adjacencies.add(mapNodeHTsub);
						JData jsonHTsub2 = new JData();
						JData jsonFDsub = new JData();
						Gson templastj = new GsonBuilder().disableHtmlEscaping().create();
						Gson tempfd = new GsonBuilder().disableHtmlEscaping().create();
						jsonHTsub2.id = node;
						jsonFDsub.id = node;
						jsonHTsub2.name = linklabel.get(node).toString();
						jsonFDsub.name = linklabel.get(node).toString();
						jsonFDsub.data.put("$dim", "9");
						jsonFDsub.data.put("$type", "circle");		
						jsonFDsub.adjacencies = null;
						jsonHTsub2.data.put("$type", "star");
						jsonHTsub2.data.put("$color", "#C74243");
						jsonHTsub2.adjacencies = null;

						if (jfd == null){
							jfd = tempfd.toJson(jsonFDsub);
						}else{
							jfd = jfd +","+tempfd.toJson(jsonFDsub);
						}

						if (jlast == null){
							jlast = templastj.toJson(jsonHTsub2);
						}else{
							jlast = jlast +","+templastj.toJson(jsonHTsub2);
						}
					}

					Gson tempj = new GsonBuilder().disableHtmlEscaping().create();
					String temresp;
					temresp = tempj.toJson(jsonHTsub);

					if (jresp == null)
						jresp = temresp;
					else
						jresp = jresp+","+temresp;

					jresp = jresp +","+jlast;
				}
			}
			jsonHT.setLink(linkmap);
			tag.clear();
			tag.put(input, jsonHT.getLink());
			Gson aa = new GsonBuilder().disableHtmlEscaping().create();
			Gson tempfd2 = new GsonBuilder().disableHtmlEscaping().create();

			String tempf = null;
			tempf = tempfd2.toJson(jsonFD);
			tempf = tempf +","+ jfd;

			if (jresp == null)
				jresp = aa.toJson(jsonHT);
			else
				jresp =  aa.toJson(jsonHT)+ "," + jresp ;

			result.jdataHT=jresp;
			result.jdataFD=tempf;
			result.linkList=linkList;
		}
		result.setTime(Utility.calcTimeTookMs(StartTime));
		Debug.printDbgLine("EkpServiceImpl.java: END ("+input+")Time="+Utility.calcTimeTookMs(StartTime));

		return result;

	}
}