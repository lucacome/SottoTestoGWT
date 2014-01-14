package com.sottotesto.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sottotesto.client.TagmeService;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.TagmeData;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.Utility;

public class TagmeServiceImpl extends RemoteServiceServlet implements TagmeService {
	;

	/**
	 * 
	 */
	private static final long serialVersionUID = -727904838259956930L;

	public TagmeResponse sendToServer(String input) throws IllegalArgumentException {
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer()");

		long StartTime = System.currentTimeMillis();

		//JData.InitJData();



		TagmeResponse tagmeResp = new TagmeResponse();
		try {
			//config TAGME request parameters
			tagmeResp.setRho(0.06);
			URL url = new URL ("http://tagme.di.unipi.it/tag");
			String charset = "UTF-8";
			String param1name = "text";
			String param1value = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
			String param2name = "key";
			String param2value = "plclcd321";
			String param3name = "include_categories";
			String param3value = "true";	
			String query = String.format("%s=%s&%s=%s&%s=%s", URLEncoder.encode(param1name, charset), URLEncoder.encode(param1value, charset), URLEncoder.encode(param2name, charset), URLEncoder.encode(param2value, charset), URLEncoder.encode(param3name, charset), URLEncoder.encode(param3value, charset));

			//Debug.printDbgLine(query);
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
				tagmeResp.setTime(Utility.calcTimeTookMs(StartTime));
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

			tagmeResp.setJson(Utility.toUTF8(responseTagTmp));

			//aggiungi la risposta formattata html
			responseTagTmp = "";
			tagmeResp.setJsonNL(tagmeResp.getJson().replaceAll(",", ",\n"));

			//liste per entita' e spotPlace con rho sufficiente
			List<String> titletag = new ArrayList<String>();
			List<String> spotTag = new ArrayList<String>();

			//liste per entita' e spotPlace con rho non sufficiente
			List<String> titleSkipped = new ArrayList<String>();
			List<String> spotSkipped = new ArrayList<String>();

			//converti Json -> gson
			Gson gson = new Gson();
			//JsonArray jarray = new JsonArray();
			tagmeResp.setJsonData(gson.fromJson(tagmeResp.getJson(), TagmeData.class));
			tagmeResp.setResNum(tagmeResp.getJsonData().annotations.size());
			for (int i=0; i<=tagmeResp.getResNum()-1; i++){
				if ( tagmeResp.getJsonData().annotations.get(i).rho > tagmeResp.getRho()){

					responseTagTmp = tagmeResp.getJsonData().annotations.get(i).title;
					//JData.jdata.add("title"+i, jarray);
					//JData.jdata.addProperty("title"+i, responseTagTmp);
					responseTagTmp = responseTagTmp.replaceAll(" ", "_");
					titletag.add(responseTagTmp);	

					responseTagTmp = tagmeResp.getJsonData().annotations.get(i).spot;
					spotTag.add(responseTagTmp);
					responseTagTmp = "";
				}else{
					Debug.printDbgLine("TagmeServiceImpl.java: "+ tagmeResp.getJsonData().annotations.get(i).title + " rho troppo basso("+tagmeResp.getJsonData().annotations.get(i).rho+") e max="+tagmeResp.getRho());
					responseTagTmp = tagmeResp.getJsonData().annotations.get(i).title;
					responseTagTmp = responseTagTmp.replaceAll(" ", "_");
					titleSkipped.add(responseTagTmp);	
					responseTagTmp = tagmeResp.getJsonData().annotations.get(i).spot;
					spotSkipped.add(responseTagTmp);
					responseTagTmp = "";
				}
			}
			tagmeResp.setTitleTag(titletag);
			tagmeResp.setSpotTag(spotTag);
			tagmeResp.setTitleSkipped(titleSkipped);
			tagmeResp.setSpotSkipped(spotSkipped);
			//JsonElement jelement = null;

			//jarray.add(jelement);

			//			Gson prova = new Gson();
			//			String prova2 = prova.toJson();
			//			Debug.printDbgLine("BOH"+prova2);
			//Debug.printDbgLine("BOH"+JData.jdata.toString());
		} catch (MalformedURLException e) {
			tagmeResp.setCode(-1);
			tagmeResp.setError("Error MalformedURLException");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			tagmeResp.setCode(-1);
			tagmeResp.setError("Error UnsupportedEncodingException");
			e.printStackTrace();
		} catch (IOException e) {
			tagmeResp.setCode(-1);
			tagmeResp.setError("Error IOException");
			e.printStackTrace();
		}

		tagmeResp.setTime(Utility.calcTimeTookMs(StartTime));
		Debug.printDbgLine("TagmeServiceImpl.java: sendToServer(): END -> "+String.valueOf(tagmeResp.getCode())+" ["+tagmeResp.getTime()+"ms]");
		return tagmeResp;
	}



}