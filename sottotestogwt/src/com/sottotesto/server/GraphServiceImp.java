package com.sottotesto.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.GraphService;
import com.sottotesto.shared.Debug;

public class GraphServiceImp extends RemoteServiceServlet implements GraphService	{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1786697847150590766L;

	public String sendToServer(List<String> fdfinal, List<String> selectedEn) throws IllegalArgumentException {

		Debug.printDbgLine("GraphServiceImpl.java: sendToServer()");
		Debug.printDbgLine("Selezionati= "+selectedEn);
		Iterator<String> iterfd = fdfinal.listIterator();
		String response = "";
		List<JData> jdatalist = new ArrayList<JData>();
		List<JData> jlink = new ArrayList<JData>();
		JData data1 = new JData();
		JData data2 = new JData();
		JData finale = new JData();
		Gson aa = new GsonBuilder().disableHtmlEscaping().create();

		Debug.printDbgLine("Size="+selectedEn.size());


		while (iterfd.hasNext()){
			String temp = iterfd.next();
			Debug.printDbgLine(temp);
			Gson pa = new Gson();
			JsonParser parser = new JsonParser();
//			Debug.printDbgLine("G0.1");
			JsonArray array = parser.parse(temp).getAsJsonArray();
//			Debug.printDbgLine("G0.2");
			JData jd = pa.fromJson(array.get(0), JData.class);
			
	
		
			
//			for ( int i=1; i<array.size(); i++)
//				jd = pa.fromJson(array.get(i), JData.class);
//			
//			Debug.printDbgLine("ARRAY_SIZE= "+array.size());			

			for (String item : selectedEn){
				if(jd.id.equals(item)){
					Debug.printDbgLine("AGGIUNTO="+item);
					jdatalist.add(jd);
					for ( int i=1; i<array.size(); i++){
						JData jdl = pa.fromJson(array.get(i), JData.class);
						jlink.add(jdl);
						}
				}
				else
				{
					Debug.printDbgLine("NON AGGIUNTO");
				}

			}

			//response = response + temp +",";
//			Debug.printDbgLine("G0.20");

		}
		Iterator<JData> jiter = jdatalist.listIterator();
		Iterator<JData> jiter2 = jdatalist.listIterator();
//		Debug.printDbgLine("G1");
		data1 = jiter.next();
//		Debug.printDbgLine("G2");
		data2 = jiter.next();

			
			Iterator<Map<String, String>> iteradj = data1.adjacencies.iterator();

			while (iteradj.hasNext()){
				Map<String,String> tempa = iteradj.next();
				if (data2.adjacencies.contains(tempa)){
					finale.adjacencies.add(tempa);

				}

			}
			data1 = finale;
			finale = new JData();
			
			while (jiter.hasNext()){
				data2 = jiter.next();
				
				iteradj = data1.adjacencies.iterator();
				while (iteradj.hasNext()){
					Map<String,String> tempa = iteradj.next();
					if (data2.adjacencies.contains(tempa)){
//						Debug.printDbgLine("TEMPA="+tempa);
						finale.adjacencies.add(tempa);
//						Debug.printDbgLine("DATA2= "+data2.id);
//						Debug.printDbgLine("DATA2= "+tempa.get("nodeTo"));
					}

				}
				data1 = finale;
				finale = new JData();
			}

		String link = "";
		while (jiter2.hasNext()){
			data2 = jiter2.next();
			iteradj = data1.adjacencies.iterator();
			while (iteradj.hasNext()){
			Map<String,String> tempa2 = iteradj.next();
			if (data2.adjacencies.contains(tempa2)){
				finale.id = data2.id;
				finale.name = data2.name;
				finale.data = data2.data;
				finale.adjacencies = data1.adjacencies;
				 Iterator<Map<String, String>> iteradj2 = finale.adjacencies.iterator();
				 Iterator<JData> linkiter = jlink.iterator();
				if (link.isEmpty())
					while (iteradj2.hasNext()){
						
						String linkTo = iteradj2.next().get("nodeTo");
//						Debug.printDbgLine("GG= "+linkTo);
						while (linkiter.hasNext()){
							JData templ = new JData();
							templ = linkiter.next();
							String linkA = templ.id;
//							Debug.printDbgLine("GGA= "+linkA);
							if ( linkTo.equals(linkA)){
								Debug.printDbgLine("dio= "+linkA);
								if (link.isEmpty())
									link = aa.toJson(templ);
								else
									link = link + "," + aa.toJson(templ);
								break;
							}
						}
					}
				if (response.isEmpty())
					response += aa.toJson(finale);
				else
					response = response+ "," + aa.toJson(finale);
				break;
			}
			}
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
//		while (jiter.hasNext()){
//			Debug.printDbgLine("G3");
//			Iterator<Map<String, String>> iteradj = data1.adjacencies.iterator();
//			Debug.printDbgLine("G4");
//
//
//			while(jiter2.hasNext()){
//
//				JData data2 = jiter2.next();
//				while (iteradj.hasNext()){
//					Debug.printDbgLine("G5");
//
//					Map<String,String> tempa = iteradj.next();
//					Debug.printDbgLine("G6");
//
//					if (data2.adjacencies.contains(tempa)){
//						Debug.printDbgLine("GraphITER= "+tempa);
//						finale.adjacencies.add(tempa);
//						Debug.printDbgLine("CAZZO= "+data2.getLink());
//					}
//					Debug.printDbgLine("G8");
//					//data2 = jiter2.next();
//				}
//			}
//			if(jiter.hasNext()){
//			jiter.next();
//			jiter2 = jiter;
//			}
//		}

//		Debug.printDbgLine("G9");
//		Gson aa = new GsonBuilder().disableHtmlEscaping().create();

//		data1.id = "Nuova";
//		data1.name = "Nuova_name";
//
//		response = aa.toJson(data1);

		response = "["+ response + "," + link + "]";
		//response = jdatalist.get(1).id;
	//	Debug.printDbgLine("PIPPO_GRAPH= "+response);


		// TODO Auto-generated method stub
		return response;
	}

}
