package com.sottotesto.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
		String link = "";
		List<JData> jdatalist = new ArrayList<JData>();
		List<JData> jlink = new ArrayList<JData>();
		Iterator<JData> linkiter = jlink.iterator();
		JData data1 = new JData();
		JData data2 = new JData();
		JData finale = new JData();
		Gson aa = new GsonBuilder().disableHtmlEscaping().create();

		Debug.printDbgLine("Size="+selectedEn.size());

		//ciclo la lista di json

		while (iterfd.hasNext()){
			String temp = iterfd.next();
			//Debug.printDbgLine(temp);
			Gson pa = new Gson();
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(temp).getAsJsonArray();
			JData jd = pa.fromJson(array.get(0), JData.class);

			for (String item : selectedEn){
				if(jd.id.equals(item)){
					Debug.printDbgLine("AGGIUNTO="+item);
					jdatalist.add(jd);
					for ( int i=1; i<array.size(); i++){
						JData jdl = pa.fromJson(array.get(i), JData.class);
						jlink.add(jdl);
					}
				}
			}
		}

		Iterator<JData> jiter = jdatalist.listIterator();
		if (selectedEn.size() > 1){

			Iterator<JData> jiter2 = jdatalist.listIterator();
			data1 = jiter.next();
			data2 = jiter.next();


			Iterator<Map<String, String>> iteradj = data1.adjacencies.iterator();

			while (iteradj.hasNext()){
				Map<String,String> tempa = iteradj.next();
				if (data2.adjacencies.contains(tempa))
					finale.adjacencies.add(tempa);
			}

			data1 = finale;
			finale = new JData();

			while (jiter.hasNext()){
				data2 = jiter.next();

				iteradj = data1.adjacencies.iterator();
				while (iteradj.hasNext()){
					Map<String,String> tempa = iteradj.next();
					if (data2.adjacencies.contains(tempa))
						finale.adjacencies.add(tempa);
				}

				data1 = finale;
				finale = new JData();
			}

			link = "";
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
						linkiter = jlink.iterator();
						if (link.isEmpty())
							while (iteradj2.hasNext()){
								String linkTo = iteradj2.next().get("nodeTo");

								while (linkiter.hasNext()){
									JData templ = new JData();
									templ = linkiter.next();
									String linkA = templ.id;

									if ( linkTo.equals(linkA)){
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



			response = "["+ response + "," + link + "]";
		}
		else if (selectedEn.size() == 1){
			link = "";
			response = aa.toJson(jiter.next());
			linkiter = jlink.iterator();
			while (linkiter.hasNext()){
				JData templ = new JData();
				templ = linkiter.next();
				if (link.isEmpty())
					link = aa.toJson(templ);
				else
					link = link + "," + aa.toJson(templ);
			}
			response = "["+ response + "," + link + "]";
		}else
			response = "[ {\"id\":\"vuoto\", \"name\":\"seleziona qualcosa\"} ]";
		
		// TODO Auto-generated method stub
		return response;
	}

}
