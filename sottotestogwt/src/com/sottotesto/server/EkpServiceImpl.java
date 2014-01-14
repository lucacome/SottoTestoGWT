package com.sottotesto.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
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
		String inputbello = input;

		try {
			input = URLEncoder.encode(input, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


		try {

			//open TAGME connection
			HttpURLConnection connessione = null;
			InputStream stream = null;
			connessione = (HttpURLConnection)new URL("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+input+"?dbpedia-version=3.8").openConnection();
			connessione.setRequestMethod("GET");
			connessione.setDoOutput(true);
			connessione.setRequestProperty("Accept", "application/rdf+xml");

			stream = connessione.getInputStream();

			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());

			result.setTag(inputbello);
			Debug.printDbgLine("URL="+connessione.getURL());
			Debug.printDbgLine("EkpServiceImpl.java: respcode="+connessione.getResponseCode());
			//Debug.printDbgLine("EkpServiceImpl.java: respmessage="+connessione.getResponseMessage());
			String responseEkpTemp = "";
			if (result.getContentType().contains("application/rdf+xml") && result.getCode() == 200){
				//				Scanner inputs = new Scanner(stream);	
				//				while (inputs.hasNextLine())
				//					responseEkpTemp += inputs.nextLine();

				//				inputs.close();
				arp.read(m, stream, null);
				connessione.disconnect();
				stream.close();
			}
			Debug.printDbgLine("EkpServiceImpl.java: resp="+responseEkpTemp);
			if (result.getCode() != 200){
				result.setRDF("Stringa vuota, Code="+result.getCode());
			}else{
				result.setRDF(responseEkpTemp);

				//				InputStream in = new ByteArrayInputStream(responseEkpTemp.getBytes("UTF-8"));
				//		arp.read(m, in, null);
				String about = "http://dbpedia.org/resource/"+inputbello;
				Map<String, String> mapDataFD = new HashMap<String,String>();
				String jfd = null;

				jsonHT.id = about;
				jsonFD.id = about;
				mapDataFD.put("$type", "square");
				mapDataFD.put("$color", "#BD1B89");
				jsonFD.data = mapDataFD;

				type = m.getNsPrefixURI("j.0");

				//result.setType(type.replace("http://www.ontologydesignpatterns.org/aemoo/ekp//", "").replace(".owl#", ""));

				result.setType(type.substring(type.lastIndexOf('/')+1).replace(".owl#", ""));
				long homeTimeStart = System.currentTimeMillis();
				HttpURLConnection connessione2 = null;
				InputStream stream2 = null;
				connessione2 = (HttpURLConnection)new URL(type).openConnection();
				connessione2.setRequestMethod("GET");
				connessione2.setDoOutput(true);
				connessione2.setRequestProperty("Accept", "application/rdf+xml");

				stream2 = connessione2.getInputStream();
				RDFReader arp2 = null;
				Model m2 = ModelFactory.createDefaultModel();
				arp2 = m2.getReader();
				arp2.read(m2, stream2, type);
				connessione2.disconnect();
				stream2.close();
				//				String responseType = "";
				//				if (connessione2.getContentType().contains("application/rdf+xml")){
				//					Scanner inputs2 = new Scanner(stream2);	
				//					while (inputs2.hasNextLine())
				//						responseType += inputs2.nextLine();
				//
				//					inputs2.close();
				//					connessione2.disconnect();
				//					stream2.close();
				//				}
				long homeTimeStop = System.currentTimeMillis()-homeTimeStart;
				Debug.printDbgLine("TIME="+homeTimeStop);
				//Debug.printDbgLine("response="+responseType);
				//			InputStream in2 = new ByteArrayInputStream(responseType.getBytes("UTF-8"));

				Resource connection = null;

				Resource link = null;
				link = m.getResource(about);
				StmtIterator i = null;				
				linkmap.clear();
				Map<String,String> linklabel = new HashMap<String,String>();

				for (i = link.listProperties(); i.hasNext(); ) {
					Statement s = i.next();					
					//Debug.printDbgLine( "link has property " + s.getPredicate().getLocalName().replace("linksTo", "") + " with value " + s.getObject() );

					linkmap.put(s.getPredicate().getLocalName(), s.getObject().toString());
					Resource oth = null;
					//Debug.printDbgLine(s.getObject().toString());
					oth = m.getResource(s.getObject().toString());
					StmtIterator a = null;

					for (a = oth.listProperties(); a.hasNext(); ){
						Statement z = a.next();
						if (z.getPredicate().getLocalName().contains("label") && z.getObject().toString().contains("@en"))
							linklabel.put(s.getObject().toString(), z.getObject().toString().replace("@en", ""));
						//Debug.printDbgLine("PIPPO="+z.getPredicate().getLocalName()+"\nPLUTO="+z.getObject().toString().replace("@en", ""));
					}	
				}

				for (Object key : linkmap.keySet()){
					//Debug.printDbgLine(key.toString());
					if (key.toString().contains("type"))
						jsonHT.type = key.toString();
					else if (key.toString().contains("label")){
						jsonHT.name = linkmap.asMap().get(key).toString().replace("@en", "");
						jsonFD.name = jsonHT.name;
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





						connection = m2.getResource(type+key.toString());

						StmtIterator i2 = null;

						String comment = "";
						String label = "";
						String range = "";

						for (i2 = connection.listProperties(); i2.hasNext(); ) {
							Statement s2 = i2.next();	


							//Debug.printDbgLine("BOH"+s2+"\n");
							//	Debug.printDbgLine("BOH2"+s2.getLiteral());
							if (s2.getObject().toString().contains("@en") && s2.getPredicate().toString().contains("comment")){
								Debug.printDbgLine("Comment="+s2.getObject().toString().replace("@en", ""));
								comment = s2.getObject().toString().replace("@en", "");
							}

							if (s2.getObject().toString().contains("@en") && s2.getPredicate().toString().contains("label")){
								Debug.printDbgLine("label="+s2.getObject().toString().replace("@en", ""));
								label = s2.getObject().toString().replace("@en", "");
							}
							if (s2.getPredicate().toString().contains("range")){
								Debug.printDbgLine("range="+s2.getObject().toString().replace("http://dbpedia.org/ontology/", ""));
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
						//Debug.printDbgLine("1");

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
							jsonHTsub2.name = linklabel.get(node).toString();//node.replace("http://dbpedia.org/resource/", "").replace("_", " ");
							jsonFDsub.name = linklabel.get(node).toString();
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

							//Debug.printDbgLine(key.toString()+"="+node);
						}

						Gson tempj = new GsonBuilder().disableHtmlEscaping().create();

						String temresp;



						temresp = tempj.toJson(jsonHTsub);
						//Debug.printDbgLine(temresp);
						if (jresp == null)
							jresp = temresp;
						else
							jresp = jresp+","+temresp;
						//Debug.printDbgLine("jresp="+jresp);
						jresp = jresp +","+jlast;
						//Debug.printDbgLine("jresp+jrasp="+jresp);
					}
					//Debug.printDbgLine("dopo");
				}
				jsonHT.setLink(linkmap);
				//Debug.printDbgLine(jsonHT.getLink().toString());
				tag.clear();
				tag.put(inputbello, jsonHT.getLink());
				//		jdata.setTag(tag);

				//	Gson ekpj = new GsonBuilder().disableHtmlEscaping().create();
				Gson aa = new GsonBuilder().disableHtmlEscaping().create();
				Gson tempfd2 = new GsonBuilder().disableHtmlEscaping().create();

				//JsonElement jj = aa.toJsonTree(tag);


				//jj.getAsJsonObject().add(input, ekpj.toJsonTree(jdata.getLink().asMap()));

				//jresp = aa.toJson(jj);


				String tempf = null;




				tempf = tempfd2.toJson(jsonFD);

				tempf = tempf +","+ jfd;
				//Debug.printDbgLine("jsonfd="+tempf);


				if (jresp == null)
					jresp = aa.toJson(jsonHT);
				else
					jresp =  aa.toJson(jsonHT)+ "," + jresp ;

				Debug.printDbgLine("EkpServiceImpl: json response="+jresp);
				result.jdataHT=jresp;
				result.jdataFD=tempf;
				result.linkList=linkList;
				Debug.printDbgLine(linkList.toString());


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