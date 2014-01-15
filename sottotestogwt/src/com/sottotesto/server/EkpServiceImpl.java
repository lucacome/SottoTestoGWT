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
			if (input.contains("(")){
				String[] a = input.split("\\(");
				String temp = "";
				input = URLEncoder.encode(a[0], "UTF-8");
				temp = "(" + URLEncoder.encode(a[1].substring(0, a[1].length()-1), "UTF-8") + ")";
				input = input + temp;
			}
			else
				input = URLEncoder.encode(input, "UTF-8");

		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}



		try {
			long homeTimeStart2 = System.currentTimeMillis();
			//open TAGME connection
			InputStream stream = null;
			HttpURLConnection connessione = null;
			connessione = (HttpURLConnection)new URL("http://wit.istc.cnr.it:9090/ekp/get/http://dbpedia.org/resource/"+inputbello+"?dbpedia-version=3.8").openConnection();
			connessione.setRequestMethod("GET");

			connessione.setRequestProperty("Accept-Charset", "utf-8");
			connessione.setRequestProperty("Accept", "application/rdf+xml; charset=utf-8");

			connessione.setDoOutput(true);
			stream = connessione.getInputStream();
			result.setCode(connessione.getResponseCode());
			result.setMessage(connessione.getResponseMessage());
			result.setContentType(connessione.getContentType());

			result.setTag(inputbello);
			Debug.printDbgLine("URL="+connessione.getURL());
			Debug.printDbgLine("EkpServiceImpl.java: respcode="+connessione.getResponseCode());

			String responseEkpTemp = "";
			if (result.getContentType().contains("application/rdf+xml") && result.getCode() == 200){
				//
				//				BufferedReader inputReader = new BufferedReader(new InputStreamReader(connessione.getInputStream()));
				//				StringBuilder sb = new StringBuilder();
				//				String inline = "";
				//				while ((inline = inputReader.readLine()) != null) {
				//					sb.append(inline);
				//				}
				//				responseEkpTemp = URLDecoder.decode(sb.toString(), "UTF-8");
				//				Debug.printDbgLine(responseEkpTemp);
				//				connessione.disconnect();
				//				m.read(new StringReader(responseEkpTemp), null);
				//arp.read(m, new StringReader(responseEkpTemp), null);
				arp.read(m, stream, null);
				stream.close();
				connessione.disconnect();

			}
			long homeTimeStop2 = System.currentTimeMillis()-homeTimeStart2;
			Debug.printDbgLine("TIME2="+homeTimeStop2);
			//			Debug.printDbgLine("EkpServiceImpl.java: resp="+responseEkpTemp);
			if (result.getCode() != 200){
				result.setRDF("Stringa vuota, Code="+result.getCode());
			}else{
				Debug.printDbgLine("1");
				result.setRDF(responseEkpTemp);

				String about = "http://dbpedia.org/resource/"+input;
				Map<String, String> mapDataFD = new HashMap<String,String>();
				String jfd = null;
				Debug.printDbgLine("2");
				jsonHT.id = about;
				jsonFD.id = about;
				mapDataFD.put("$type", "square");
				mapDataFD.put("$color", "#BD1B89");
				jsonFD.data = mapDataFD;


				Resource connection = null;

				Resource link = null;
				link = m.getResource(about);
				StmtIterator i = null;				
				linkmap.clear();
				Map<String,String> linklabel = new HashMap<String,String>();

				for (i = link.listProperties(); i.hasNext(); ) {
					Statement s = i.next();					
					//Debug.printDbgLine( "link has property " + s.getPredicate().getLocalName().replace("linksTo", "") + " with value " + s.getObject() );
					//Debug.printDbgLine("DIO="+s);
					if (s.getPredicate().getLocalName().contains("label")){
						jsonHT.name = s.getObject().toString().replace("@en", "");
						jsonFD.name = jsonHT.name;	
					}
					if (s.getPredicate().getLocalName().contains("type")){
						type=s.getObject().toString();
						if (!type.isEmpty() && type.contains("http://dbpedia.org/ontology/"))
							result.setType(type.substring(type.lastIndexOf('/')+1));
						else
							type = "";

					}
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


				RDFReader arp2 = null;
				Model m2 = ModelFactory.createDefaultModel();
				arp2 = m2.getReader();



				String type2 = m.getNsPrefixURI("j.0");
				if (type.isEmpty()){
					if (type2 != null)
						result.setType(type2.substring(type2.lastIndexOf('/')+1).replace(".owl#", ""));

				}

				Debug.printDbgLine(type);

				if (type2 != null){
					long homeTimeStart = System.currentTimeMillis();
					HttpURLConnection connessione2 = null;
					InputStream stream2 = null;
					connessione2 = (HttpURLConnection)new URL(type2).openConnection();
					connessione2.setRequestMethod("GET");
					connessione2.setDoOutput(true);
					connessione2.setRequestProperty("Accept", "application/rdf+xml");
					stream2 = connessione2.getInputStream();

					arp2.read(m2, stream2, type2);
					connessione2.disconnect();
					stream2.close();

					long homeTimeStop = System.currentTimeMillis()-homeTimeStart;
					Debug.printDbgLine("TIME="+homeTimeStop);
				}




				for (Object key : linkmap.keySet()){
					//Debug.printDbgLine(key.toString());
					if (key.toString().contains("type"))
						jsonHT.type = key.toString();
					else if (key.toString().contains("label")){
						//						jsonHT.name = linkmap.asMap().get(key).toString().replace("@en", "");
						//						jsonFD.name = jsonHT.name;
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
								label = Utility.toUTF8(s2.getObject().toString().replace("@en", ""));
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