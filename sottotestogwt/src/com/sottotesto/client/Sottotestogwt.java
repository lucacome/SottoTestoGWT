package com.sottotesto.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.Global;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.TreeData;
import com.sottotesto.shared.TreeDataProperties;
import com.sottotesto.shared.Utility;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {

	//title panel items
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label textAreaLabel;   //label for text area
	private Label errorLabel;      //errorlabel for textarea misuse
	private ContentPanel titleContentPanel;
	private HTML titleHTML;
	private HorizontalLayoutContainer searchPanelHC;
	private FlowPanel textAreaFP;
	private List<String> listFD = new ArrayList<String>();
	private DecoratedPopupPanel taggedEntityPopup;
	
	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
	private final DBPediaQueryAsync dbpqService = GWT.create(DBPediaQuery.class);
	private final ListServiceAsync listService = GWT.create(ListService.class);
	
	private int ekpRemainingCallNum = 0;
	private List<String> ekpRemainingCallInputs = new ArrayList<String>();


	//Service status panel items
	ServicePanelLogger spLogger;
	ContentPanel serviceStatusPanel = null;
	HorizontalLayoutContainer serviceStatusPanelHC;
	VerticalPanel tagmeStatusVP;
	VerticalPanel dbpediaStatusVP;
	VerticalPanel ekpStatusVP;
	HTML HtmlTagmeService;
	HTML HtmlDBPediaService;
	HTML HtmlEkpService;
	String HTMLserviceStringTitle="<span title=\"Click to show\ndata/log\">";
	String HTMLloadIconString="<img src='loading.gif'/>";
	String HTMLtagmeServiceStringWaiting=HTMLserviceStringTitle+"Tagme Service: Waiting."+"</span>";
	String HTMLtagmeServiceStringCalling=HTMLserviceStringTitle+"Tagme Service: Calling ..."+"</span>"+HTMLloadIconString;
	String HTMLtagmeServiceStringOK=HTMLserviceStringTitle+"Tagme Service: <span style='color:green;'>SUCCESS</span>"+"</span>";
	String HTMLtagmeServiceStringFAIL=HTMLserviceStringTitle+"Tagme Service: <span style='color:red;'>FAILED</span>"+"</span>";
	String HTMLtagmeServiceStringSkipped=HTMLserviceStringTitle+"Tagme Service: <span style='color:orange;'>Skipped</span>"+"</span>";
	String HTMLdbpediaServiceStringWaiting=HTMLserviceStringTitle+"DBPedia Service: Waiting."+"</span>";
	String HTMLdbpediaServiceStringCalling=HTMLserviceStringTitle+"DBPedia Service: Calling ..."+"</span>"+HTMLloadIconString;
	String HTMLdbpediaServiceStringOK=HTMLserviceStringTitle+"DBPedia Service: <span style='color:green;'>SUCCESS</span>"+"</span>";
	String HTMLdbpediaServiceStringFAIL=HTMLserviceStringTitle+"DBPedia Service: <span style='color:red;'>FAILED</span>"+"</span>";
	String HTMLdbpediaServiceStringSkipped=HTMLserviceStringTitle+"DBPedia Service: <span style='color:orange;'>Skipped</span>"+"</span>";
	String HTMLekpServiceStringWaiting=HTMLserviceStringTitle+"Ekp Service: Waiting."+"</span>";
	String HTMLekpServiceStringCalling=HTMLserviceStringTitle+"Ekp Service: Calling ..."+"</span>"+HTMLloadIconString;
	String HTMLekpServiceStringOK=HTMLserviceStringTitle+"Ekp Service: <span style='color:green;'>SUCCESS</span>"+"</span>";
	String HTMLekpServiceStringFAIL=HTMLserviceStringTitle+"Ekp Service: <span style='color:red;'>FAILED</span>"+"</span>";
	String HTMLekpServiceStringSkipped=HTMLserviceStringTitle+"Ekp Service: <span style='color:orange;'>Skipped</span>"+"</span>";


	//results items
	ResultController rc;

	//tree items
	TreeDataProperties treeProperties;
	TreeStore<TreeData> treeStore;
	TreeData tdForcedirected;
	TreeData tdHyperTree;
	TreeData tdMap;
	List<TreeData> tdEntriesList;
	int treeDataIdProvider;

	//service responses variables
	private TagmeResponse tagmeResp; //response of TAGME service
	private List<DBPediaResponse> dbpediaResps; //response of DBPEDIA service
	private List<EkpResponse> ekpResps; //response of EKP service
	private int ekpFailsNum;
	private int dbpqCallsDone, dbpqCallsToDo, dbpFailsNum;
	
	//resources
	TextResource resourceHTMLmap;
	DataResource resourceHTMLdata;
	Frame frameHTML;

	//This is the entry point method.
	public void onModuleLoad() {
		Debug.printDbgLine("**********************************************************");
		Debug.printDbgLine("NEW RUN!");
		Debug.printDbgLine("**********************************************************");
		Debug.printDbgLine("Sottotestogwt.java: onModuleLoad()");

		long homeTimeStart = System.currentTimeMillis();

		//show Loading Icon
		RootPanel.get("homeLoading").setVisible(true);

		Global.init();
		
		//Initialize items and load them on html page
		initItems();	




		//hide loading icon
		RootPanel.get("title").setVisible(false);
		RootPanel.get("homeLoading").setVisible(false);

		Debug.printDbgLine("Sottotestogwt.java: HomePage loaded in "+(System.currentTimeMillis()-homeTimeStart)+"ms, waiting input ...");
	}

	//initialize items and load them on page
	private void initItems(){
		Debug.printDbgLine("Sottotestogwt.java: initItems()");

		//init services responses
		tagmeResp = new TagmeResponse();
		dbpediaResps = new ArrayList<DBPediaResponse>();
		ekpResps = new ArrayList<EkpResponse>();

		//add search panel to page
		initSearchPanel();		
		RootPanel.get("searchContainer").add(titleContentPanel);

		//add service panel to page
		InitServiceStatusPanel();		

		//add results panel to page
		rc = new ResultController();
		rc.init();
		

	}		

	private void initSearchPanel(){
		//init input section items

		taggedEntityPopup = new DecoratedPopupPanel();
		taggedEntityPopup.ensureDebugId("cwBasicPopup-simplePopup");
		
		if (titleContentPanel != null) titleContentPanel.clear();

		textAreaLabel = new Label();
		textAreaLabel.getElement().setClassName("searchAreaLabel"); //for css styling
		textAreaLabel.setText("Scrivi una frase:");				
		textAreaDefText = "Enter something in english here...";
		textSendButton = "Cerca";
		sendButton = new Button(textSendButton);
		textArea = new TextArea();
		textArea.setStylePrimaryName("searchAreaTextArea");
		textArea.setText(textAreaDefText);		
		textArea.setSize("550px", "25px");				
		textArea.setFocus(true);
		textArea.selectAll();
		errorLabel = new Label();
		errorLabel.setStylePrimaryName("searchAreaErrorLabel");
		sendButton.addStyleName("sendButton");

		titleHTML = new HTML();
		titleHTML.setHTML("<h1>Sottotesto Web App</h1>");
		titleHTML.setHeight("10px");
		titleContentPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		titleContentPanel.setHeadingHtml("SOTTOTESTO WEBAPPLICATION");
		titleContentPanel.setWidth(Utility.getPanelsMaxWidth());
		titleContentPanel.setHeight(RootPanel.get("searchContainer").getOffsetHeight()+"px");

		textAreaFP = new FlowPanel();
		textAreaFP.setStylePrimaryName("searchAreaFlowPanel");
		//textAreaFP.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		//textAreaFP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		textAreaFP.add(textArea);
		textAreaFP.add(errorLabel);

		searchPanelHC = new HorizontalLayoutContainer();
		searchPanelHC.setId("searchHorContainer");
		searchPanelHC.setWidth(Utility.getPanelsMaxWidth());
		searchPanelHC.setBorders(false);
		searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		searchPanelHC.add(textAreaFP, new HorizontalLayoutData(0.70, 1, new Margins(4)));
		searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 1, new Margins(14)));			

		titleContentPanel.setWidget(searchPanelHC);
		titleContentPanel.setId("searchAreaPanel");
		
		ToolButton optionsTool = new ToolButton(ToolButton.GEAR);
		optionsTool.setTitle("Configura opzioni");
		optionsTool.addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				Global.showOptionsDialog();
			}
		});
		titleContentPanel.addTool(optionsTool);

		// Create a handler for the sendButton and nameField
		class HomeInputHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): sendButtonOnClick()");
				validateSearch();
				if (textArea.getText().length()>0)
					callTagme();		
			}
			public void onKeyUp(KeyUpEvent event) {				
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): textAreaOnKeyUpEnter()");
					textArea.setText(textArea.getText().replace("\n", ""));	
					if (sendButton.isEnabled()){
						validateSearch();
						if (textArea.getText().length()>0)
							callTagme();				
					}
				}
			}			
		}

		// Add a handler to send the name to the server
		Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): creating handlers");
		HomeInputHandler hihandler = new HomeInputHandler();
		sendButton.addClickHandler(hihandler);
		textArea.addClickHandler(new ClickHandler() {	
			@Override
			public void onClick(ClickEvent event) {
				if (textArea.getText().equals(textAreaDefText)) textArea.setText("");				
			}
		});
		textArea.addKeyUpHandler(new KeyUpHandler() {			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): textAreaOnKeyUpEnter()");
					textArea.setText(textArea.getText().replace("\n", ""));	
					if (sendButton.isEnabled()){
						validateSearch();
						if (textArea.getText().length()>0)
							callTagme();			
					}
				}
			}
		});

		//TEST BUTTON: FOR TESTING... OF COURSE		
		/*
				Button testButton = new Button();
				testButton.setText("Test");
				testButton.addClickHandler(new ClickHandler() {			
					@Override
					public void onClick(ClickEvent event) {
						Debug.printDbgLine("Sottotestogwt.java: testButtonClick()");						

					}
				});
				RootPanel.get("testContainer").add(testButton);
		 */
	}

	private void validateSearch(){
		if (textArea.getText().length()<=0) return;
		
		
	}

	//send input from textarea to tagme
	private void callTagme() {
		Debug.printDbgLine("Sottotestogwt.java: callTagme()");
		
		rc.clearCenterPanel();
		
		
		// First, we validate the input.
		errorLabel.setText("");
		String textToServer = textArea.getText();
		

		sendButton.setEnabled(false);

		//reinit service status panel items
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);

		//reinit results panel items
		rc.reInit();

		//actually call tagme service
		Utility.showLoadingBar("Calling TAGME Service");
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringCalling);		
		tagmeService.sendToServer(textToServer, Global.getRoh(), Global.getTagmeKey(), new AsyncCallback<TagmeResponse>() {
			public void onFailure(Throwable caught) {
				Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onFailure()");

				sendButton.setEnabled(true); //allow user for a new search

				//set the error
				tagmeResp = new TagmeResponse();
				tagmeResp.setCode(-1);
				tagmeResp.setError("Error Callig service Module:"+
						"<br><br>StackTrace: "+Utility.getErrorHtmlString(caught));

				HtmlTagmeService.setHTML(HTMLtagmeServiceStringFAIL); //show the fail
				
				//update log
				spLogger.addTAGMElog(tagmeResp);

				//skip all other services
				HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
				HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
				rc.showError();
				Utility.hideLoadingBar();
			}

			public void onSuccess(TagmeResponse result) {
				Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onSuccess()");

				sendButton.setEnabled(true); //allow user for a new search

				tagmeResp = result; //save the result	
				
				//update log
				spLogger.addTAGMElog(tagmeResp);

				if (!(tagmeResp.getCode()==200)){
					//Tagme ha avuto qualche problema!

					HtmlTagmeService.setHTML(HTMLtagmeServiceStringFAIL); //show the fail

					//skip all other services
					HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
					HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
					rc.showError();
					
					Utility.hideLoadingBar();
				}
				else if (tagmeResp.getTitleTag().size()==0){
					//Tagme OK, ma non ha taggato nulla!

					HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK+" (but 0!)");
					showTaggedResult();
					
					//skip all other services
					HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
					HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
					rc.showError();
					Utility.hideLoadingBar();
				}
				else {
					//Tagme OK
					
					HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK); //show the success
					showTaggedResult();
					

					//chiamiamo Ekp
					HtmlEkpService.setHTML(HTMLekpServiceStringCalling);
					List<String> titletagme = tagmeResp.getTitleTag();
					Iterator<String> itertitle =  titletagme.iterator();
					ekpRemainingCallNum=0;
					ekpRemainingCallInputs = new ArrayList<String>();
					while (itertitle.hasNext()){
						//							callEkp(itertitle.next());
						ekpRemainingCallNum++;
						ekpRemainingCallInputs.add(itertitle.next());
					}

					//intialize an empty tree
					initEmptyTree();

					dbpFailsNum=0;
					ekpResps = new ArrayList<EkpResponse>();
					callEkp();
				}						
			}
		});
	}

	private void callDBPedia(final String tagme, List<String> dbprop, String type){
		Debug.printDbgLine("Sottotestogwt.java: callDBPedia()");	

		Utility.showLoadingBar("Calling DBPedia Service");
		
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringCalling);		
		dbpediaService.sendToServer(tagme, dbprop, type, new AsyncCallback<DBPediaResponse>() {
			public void onFailure(Throwable caught) {
				//set the error
				DBPediaResponse badResp = new DBPediaResponse(); 
				badResp.setCode(-1);
				badResp.setEntity(tagme);
				badResp.setQueryResultXML("Error Callig service Module:"+
						"<br><br><b>StackTrace: </b><br>"+Utility.getErrorHtmlString(caught));

				dbpediaResps.add(badResp);
				spLogger.addDBPlog(badResp);
				dbpFailsNum++;
				HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL+" (x"+dbpFailsNum+")"); //show the fail
			}

			public void onSuccess(DBPediaResponse dbResp) {	
				Debug.printDbgLine("Sottotestogwt.java: callDBPedia(): onSuccess()");	
				
				spLogger.addDBPlog(dbResp);
				dbpediaResps.add(dbResp);
								
				if (dbResp.getCode()==200){
					if (dbpFailsNum==0) HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);
					else HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL+" (x"+dbpFailsNum+")");
				}
				else {
					dbpFailsNum++;
					HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL+" (x"+dbpFailsNum+")");					
				}
			}});

	}

	private void callEkp(){
		Debug.printDbgLine("Sottotestogwt.java: callEkp()");

		Utility.showLoadingBar("Calling EKP Service ("+ekpRemainingCallNum+" left)");
		
		String input="";

		if (ekpRemainingCallNum>0){
			input=ekpRemainingCallInputs.remove(0);
			ekpRemainingCallNum--;
		} else return;		

		final String curEntity = input; //used in case of failure
		
		Debug.printDbgLine("Sottotestogwt.java: Ekp input="+input);
		ekpService.sendToServer(input, new AsyncCallback<EkpResponse>() {
			public void onFailure(Throwable caught) {
				//set the error
				EkpResponse ekpRespTmp = new EkpResponse();
				ekpRespTmp.setCode(-1);
				ekpRespTmp.setTag(curEntity);
				ekpRespTmp.setError("Error Callig service Module:"+
						"<br><br>StackTrace: "+Utility.getErrorHtmlString(caught));

				//ekpResps.add(ekpRespTmp);

				//update log
				spLogger.addEKPlog(ekpRespTmp);
				ekpFailsNum++;
				
				if (ekpRemainingCallNum>0){callEkp();}
				else {
					HtmlEkpService.setHTML(HTMLekpServiceStringFAIL+" (x"+ekpFailsNum+")");
					rc.loadTree(treeStore);  //carica l'albero, mostra la mappa come prima cosa
					rc.getTree().expandAll();
					rc.setListFD(listFD); //ora che la lista completa, salvala nel resultcontroller
					rc.setEkpResponses(ekpResps);
					Utility.hideLoadingBar();
					callListService(ekpResps); //lancia il servizio di "cerca markers"
				}
			}

			public void onSuccess(EkpResponse result) {			
				Debug.printDbgLine("Sottotestogwt.java: Ekp output="+result.getMessage());

				//clean non-utf8 characters
				result.jdataHT = Utility.toUTF8(result.jdataHT);
				result.jdataFD = Utility.toUTF8(result.jdataFD);
				
				
				EkpResponse ekpRespTmp = new EkpResponse();
				ekpRespTmp = result;
				
				//update log
				spLogger.addEKPlog(ekpRespTmp);

				
				if (ekpRespTmp.getCode()==200) {
					
					String jsonHT = "["+result.jdataHT+"]";
					String jsonFD = "["+result.jdataFD+"]";	

					listFD.add(jsonFD);
					
					tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++),result.getTag().replaceAll("_", " "), TreeData.CLICK_ACTIONS.SHOWMAP));
					treeStore.add(tdMap, tdEntriesList.get(tdEntriesList.size()-1));
					
					tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++),result.getTag().replaceAll("_", " "), TreeData.CLICK_ACTIONS.SHOWGRAPH_HT));
					tdEntriesList.get(tdEntriesList.size()-1).setJsonHT(jsonHT);
					tdEntriesList.get(tdEntriesList.size()-1).setLinks(result.getLinks());
					treeStore.add(tdHyperTree, tdEntriesList.get(tdEntriesList.size()-1));		

					ekpResps.add(ekpRespTmp);
					
					//call dbpedia
					List<String> dbproperty = new ArrayList<String>();
					//dbproperty.add("birthDate");
					//dbproperty.add("title");
					//dbproperty.add("name");
					//dbproperty2.add("placeOfBirth");
					dbproperty.add("abstract");	
					callDBPedia(ekpRespTmp.getEncodedTag(), dbproperty, ekpRespTmp.getType());
				}
				else{ // not received 200
					ekpFailsNum++;
				}
				

				if (ekpRemainingCallNum>0){
					callEkp();
				}
				else {
					if(ekpFailsNum>0) HtmlEkpService.setHTML(HTMLekpServiceStringFAIL+" (x"+ekpFailsNum+")");
					else HtmlEkpService.setHTML(HTMLekpServiceStringOK);
					rc.loadTree(treeStore);  //carica l'albero, mostra la mappa come prima cosa
					rc.getTree().expandAll();
					rc.setListFD(listFD); //ora che la lista completa, salvala nel resultcontroller
					rc.setEkpResponses(ekpResps);
					Utility.hideLoadingBar();
					callListService(ekpResps); //lancia il servizio di "cerca markers"
				}
			}});		
	}

	private void updateDBpediaServiceLabel(DBPQueryResp resp){
		if (dbpqCallsDone >= dbpqCallsToDo){ //abbiamo finito le calls
			if (dbpFailsNum==0) HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);
			else HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL+" (x"+dbpFailsNum+")");
		}
		else{
			HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringCalling+" ("+resp.getCallNum()+"/"+resp.getMaxCalls()+")");
		}
	}

	private void callDBPediaQuery(final DBPQueryResp resp){
		dbpqService.sendToServer(resp, new AsyncCallback<DBPQueryResp>() {
			public void onFailure(Throwable caught) {
				dbpqCallsDone++; //update main counter	
				dbpFailsNum++;
				updateDBpediaServiceLabel(resp); //show the status
				Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery(): ONFAILURE() - call n."+resp.getCallNum()+"/"+resp.getMaxCalls());
			}

			public void onSuccess(DBPQueryResp result) {
				dbpqCallsDone++; //update main counter
				updateDBpediaServiceLabel(result); //show the status		
				
				spLogger.updateDBPQlog(result);
				
				Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery(): onSuccess() - call n."+result.getCallNum()+"/"+result.getMaxCalls()+" - "+result.getEntity()+" - "+result.getName()+" -> "+result.getLat()+","+result.getLng());

				//Debug.printDbgLine("entity="+result.getEntity()+", link="+result.getLink()+", name="+result.getName()+", gps="+result.getLat()+", "+result.getLng()+", relation="+result.getRelation()+", relatio="+result.getRelation()+", abstract="+result.getAbstract());
				if (result.getLat() != 0.0 && result.getLng() != 0.0)
					rc.addDBpediaMarkerSingleToMap(result);  
			}});

	}
	private void callListService(List<EkpResponse> respList){
		
		//initialize main counter
		dbpqCallsDone=0;
		dbpqCallsToDo=0;

		for (EkpResponse resp : respList)
			if(resp.getCode()==200)	{
				Debug.printDbgLine("Sottotestogwt.java: callListService() with "+resp.getTag());
				listService.sendToServer(resp, new AsyncCallback<List<DBPQueryResp>>() {
					public void onFailure(Throwable caught) {
						//set the error
						Debug.printDbgLine("Sottotestogwt.java: callListService(): onFailure()");
						HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL);
					}

					public void onSuccess(List<DBPQueryResp> result) {
						//update main counter
						dbpqCallsToDo+=result.size();

						Iterator<DBPQueryResp> iter = result.iterator();
						Debug.printDbgLine("Sottotestogwt.java: callListService(): onSuccess(): calling DBPQ "+result.size()+" times for "+result.get(0).getEntity());

						DBPQueryResp temp = new DBPQueryResp();
						int callNumber=1;
						while (iter.hasNext()){
							temp = iter.next();
							temp.setCallNum(callNumber);
							callNumber++;
							temp.setMaxCall(result.size());
							callDBPediaQuery(temp);
						}

					}});

			}
	}
	

	private void InitServiceStatusPanel(){
		Debug.printDbgLine("Sottotestogwt.java: initServiceStatusPanel()");

		//main panel
		serviceStatusPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		serviceStatusPanel.setHeadingText("Service Status");
		serviceStatusPanel.setId("serviceStatusPanel");
		serviceStatusPanel.setWidth(Utility.getPanelsMaxWidth());
		serviceStatusPanel.setHeight(RootPanel.get("servicesContainer").getOffsetHeight()+"px");
		serviceStatusPanelHC = new HorizontalLayoutContainer();
		spLogger = new ServicePanelLogger();
		
		//tagme service
		HtmlTagmeService = new HTML();
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringWaiting);
		HtmlTagmeService.setStylePrimaryName("serviceLabel");
		tagmeStatusVP = new VerticalPanel();
		tagmeStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		tagmeStatusVP.add(HtmlTagmeService);	
		tagmeStatusVP.setBorderWidth(0);
		HtmlTagmeService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlTagmeService.getHTML().contains("Waiting") && !HtmlEkpService.getHTML().contains("Skipped")) spLogger.showTagmeDataDB();}});

		//dbpedia service
		dbpFailsNum = 0;
		HtmlDBPediaService = new HTML();
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlDBPediaService.setStylePrimaryName("serviceLabel");
		dbpediaStatusVP = new VerticalPanel();
		dbpediaStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dbpediaStatusVP.add(HtmlDBPediaService);
		dbpediaStatusVP.setBorderWidth(0);
		HtmlDBPediaService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlDBPediaService.getHTML().contains("Waiting") && !HtmlEkpService.getHTML().contains("Skipped")) spLogger.showDBPediaDataDB();}});

		//ekp service
		ekpFailsNum = 0;
		HtmlEkpService = new HTML();
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);
		HtmlEkpService.setStylePrimaryName("serviceLabel");
		ekpStatusVP = new VerticalPanel();
		ekpStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		ekpStatusVP.add(HtmlEkpService);
		ekpStatusVP.setBorderWidth(0);
		HtmlEkpService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlEkpService.getHTML().contains("Waiting") && !HtmlEkpService.getHTML().contains("Skipped")) spLogger.showEkpDataDB();}});

		//add items to panel
		serviceStatusPanelHC.add(HtmlTagmeService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(HtmlEkpService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(HtmlDBPediaService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.setBorders(false);
		serviceStatusPanel.setWidget(serviceStatusPanelHC);		
		
		RootPanel.get("servicesContainer").clear();
		RootPanel.get("servicesContainer").add(serviceStatusPanel);

	}	

	private void initEmptyTree(){	
		Debug.printDbgLine("Sottotestogwt.java: initEmptyTree()");

		tdEntriesList = new ArrayList<TreeData>();

		treeDataIdProvider = 0; //tree entries id counter
		treeProperties = GWT.create(TreeDataProperties.class); // Generate the key provider and value provider for the Data class
		treeStore = new TreeStore<TreeData>(treeProperties.id()); // Create the store that the contains the data to display in the tree
		
		
		tdForcedirected = new TreeData(String.valueOf(treeDataIdProvider++),"Confronta entita'", TreeData.CLICK_ACTIONS.SHOWJOINEDGRAPH_FD);
		
		tdHyperTree = new TreeData(String.valueOf(treeDataIdProvider++),"Esplora entita'");
		tdMap = new TreeData(String.valueOf(treeDataIdProvider++),"Knowledge Map");

		treeStore.add(tdMap);
		treeStore.add(tdForcedirected);
		treeStore.add(tdHyperTree);

		//add 'all entity' entry to tree
		tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++), "Mappa completa", TreeData.CLICK_ACTIONS.SHOWMAP));
		treeStore.add(tdMap, tdEntriesList.get(tdEntriesList.size()-1));
	}

	
	private void showTaggedResult(){
		
		HorizontalPanel taggedPhraseHP = new HorizontalPanel();
		taggedPhraseHP.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		FlowLayoutContainer taggedPhraseFC = new FlowLayoutContainer();
		taggedPhraseFC.setScrollMode(ScrollMode.AUTO);
		taggedPhraseFC.setId("taggedPhraseFC");
		taggedPhraseFC.add(taggedPhraseHP);
		
		textAreaLabel.setText("Entita' rilevate:");
		addTaggedHtmls(createTaggedSearchString(),taggedPhraseHP);
		
		sendButton = new Button("Nuova ricerca");		
		sendButton.addStyleName("newSearchButton");
		sendButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				newSearch();
			}
		});

		searchPanelHC.clear();
		searchPanelHC = new HorizontalLayoutContainer();
		searchPanelHC.setId("searchHorContainer");
		searchPanelHC.setWidth(Utility.getPanelsMaxWidth());
		searchPanelHC.setBorders(false);
		searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		searchPanelHC.add(taggedPhraseFC, new HorizontalLayoutData(0.70, 1, new Margins(4)));
		searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 1, new Margins(14)));

		titleContentPanel.clear();
		titleContentPanel.setHeadingHtml("SOTTOTESTO WEBAPPLICATION");

		titleContentPanel.setWidget(searchPanelHC);
	}
	
	private void addTaggedHtmls(String taggedSearchString, HorizontalPanel hp){
		Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls2()");
		hp.clear();
		
		//CYCLE ALL STRING
		while(taggedSearchString.length()>0){
			if(taggedSearchString.substring(0,1).equals(" ")){ /* if we have a space*/
				taggedSearchString = taggedSearchString.substring(1, taggedSearchString.length());
			}
			else if(taggedSearchString.substring(0,1).equals("<")){ /* if we have a span*/
				//CREATE HTML ELEMENT CONTAINING SPAN
				Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls(): taggedSearchString: "+taggedSearchString);
				
				int spanEndIndex = taggedSearchString.indexOf("</span>")+7;
				Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls(): spanEndIndex: "+spanEndIndex);
				String curSubString = taggedSearchString.substring(0, spanEndIndex); //<span ... </span>
				Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls(): curSubString: "+curSubString);
				taggedSearchString = taggedSearchString.substring(spanEndIndex); //[</span>] ..... [end]
				Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls(): taggedSearchString trimmed: "+taggedSearchString);
				
				
				
				String curTagText = "";
				String curTitle = "";
				curTagText=curSubString.substring(curSubString.indexOf('>'), curSubString.length());
				curTagText=curTagText.substring(0, curTagText.indexOf('<'));
				if(tagmeResp.getSpotTag().contains(curTagText)) curTitle=tagmeResp.getTitleTag().get(tagmeResp.getSpotTag().indexOf(curTagText));
				else if(tagmeResp.getSpotSkipped().contains(curTagText)) curTitle=tagmeResp.getTitleTag().get(tagmeResp.getSpotSkipped().indexOf(curTagText));
				
				Debug.printDbgLine("Sottotestogwt.java: addTaggedHtmls(): curTagText: "+curTagText);
				HTML curHtml = new HTML();
				curHtml.setHTML(curSubString+"&nbsp;");
				curHtml.setTitle(curTitle);				
								
				//aggiungi eventi mouse all'html taggato
				curHtml.addMouseOverHandler(new MouseOverHandler() {				
					@Override
					public void onMouseOver(MouseOverEvent event) {		
						Element e = Element.as(event.getNativeEvent().getEventTarget());
						showTaggedPopup(e.getTitle()); //title corrisponde all'entita'
					}
				});
				//aggiungi eventi mouse all'html taggato
				curHtml.addMouseOutHandler(new MouseOutHandler() {				
					@Override
					public void onMouseOut(MouseOutEvent event) {
						hideTaggedPopup();				
					}
				});
				
				hp.add(curHtml);
			}
			else { // we have a simple char
				if(taggedSearchString.contains("<")){ // we'll have another span
					int spanBeginIndex = taggedSearchString.indexOf('<');
					String curString = taggedSearchString.substring(0, spanBeginIndex);
					curString.replaceAll(" ", "&nbsp;");
					HTML curHtml = new HTML(curString+"&nbsp;");
					hp.add(curHtml); 
					taggedSearchString = taggedSearchString.substring(spanBeginIndex, taggedSearchString.length());
				}
				else{ //no mor span -> single html until endo of line
					taggedSearchString.replaceAll(" ", "&nbsp;");
					HTML curHtml = new HTML(taggedSearchString);
					hp.add(curHtml);
					
					taggedSearchString="";
				}
			}
		}
	}
	
	

	private void showTaggedPopup(String entity){
		
		if (entity.length()>0){
			taggedEntityPopup.clear(); //clear popup from anything old
			HTML popupHtml = new HTML();
			boolean dataFound = false;

			// search for fitting dbpediaResp data
			entity = entity.replaceAll(" ", "_");
			for (DBPediaResponse curDbpResp : dbpediaResps){
				Debug.printDbgLine("Sottotestogwt.java: showTaggedPopup(): entity= "+entity+"; dbpediaEntity="+curDbpResp.getEntity());
				if (curDbpResp.getEntity().equals(entity)){		
					Debug.printDbgLine("Sottotestogwt.java: showTaggedPopup(): match found!");
					String dbpQ = curDbpResp.getQueryResultXML();
					if (dbpQ.length()<5) dbpQ="Nessun dato trovato per questa entita'";
					popupHtml.setHTML("<b>"+entity.replaceAll("_", " ")+"</b> ["+curDbpResp.getEntityType()+"]<br><br>"+dbpQ);
					dataFound=true;
					break;
				}
			}
			
			if (!dataFound){
				popupHtml.setHTML("<b>"+entity.replaceAll("_", " ")+"</b><br><br>Entita' ignorata dal servizio TAGME.");
			}

			taggedEntityPopup.setWidget(popupHtml);
			taggedEntityPopup.setPopupPosition(0, RootPanel.get("servicesContainer").getAbsoluteTop());
			taggedEntityPopup.show();
		}
	}
	private void hideTaggedPopup(){
		taggedEntityPopup.hide();
	}
	
	private String createTaggedSearchString(){
		String searchedText = textArea.getText();
		List<String> partialStrings = new ArrayList<String>();
		boolean spotTaken = false;
		int index=0;
		
		// CREATE ARRAY OF HTML SUBSTRINGS
		while (searchedText.length()>0){
			spotTaken = false;
			
			//search if first phrase is a tagged element
			index=0;
			for (String curSpotTag : tagmeResp.getSpotTag()){
				if(searchedText.indexOf(curSpotTag)==0){ // searchedText = "TAGGED bla bla bla"
					spotTaken=true;
					partialStrings.add("<span class=\"result_taggedWord\" title=\""+tagmeResp.getTitleTag().get(index)+"\">"+curSpotTag+"</span>");
					searchedText = searchedText.substring(curSpotTag.length(), searchedText.length()); //remove current spotTag from search phrase
					Debug.printDbgLine("ResultController.java: createTaggedSearchString(): added: "+partialStrings.get(partialStrings.size()-1));
					break;
				}
				index++;
			}
			
			//search if first phrase is a skipped element
			if (!spotTaken){	
				index=0;
				for (String curSpotSkipped : tagmeResp.getSpotSkipped()){
					if(searchedText.indexOf(curSpotSkipped)==0){ // searchedText = "SKIPPED bla bla bla"
						spotTaken=true;
						partialStrings.add("<span class=\"result_skippedWord\" title=\""+tagmeResp.getTitleSkipped().get(index)+"\">"+curSpotSkipped+"</span>");
						searchedText = searchedText.substring(curSpotSkipped.length(), searchedText.length()); //remove current spotTag from search phrase
						Debug.printDbgLine("ResultController.java: createTaggedSearchString(): added: "+partialStrings.get(partialStrings.size()-1));
						break;
					}
					index++;
				}
			}
			
			//if still no spot taken -> searchedText = "bla bla ?TAGGED? bla ?SKIPPED?"
			if (!spotTaken){
				int lowestIndex = 60000;
				int curIndex=0;
				
				//find lowest index among tagged elements
				for (String curSpotTag : tagmeResp.getSpotTag()){
					curIndex = searchedText.indexOf(curSpotTag);
					if(curIndex!=-1 && curIndex<lowestIndex){
						lowestIndex=curIndex;
					}
				}
				
				//find lowest index among skipped elements
				curIndex=0;
				for (String curSkipTag : tagmeResp.getSpotSkipped()){
					curIndex = searchedText.indexOf(curSkipTag);
					if(curIndex!=-1 && curIndex<lowestIndex){
						lowestIndex=curIndex;
					}
				}
				
				if (lowestIndex==60000){ //search phrase has no more skipped/tagged items!
					spotTaken=true;
					partialStrings.add(searchedText);
					searchedText = "";
					Debug.printDbgLine("ResultController.java: createTaggedSearchString(): added: "+partialStrings.get(partialStrings.size()-1));
					break;
				}
				else{ //search phrase must be cut until next tagged/skipped element
					spotTaken=true;
					partialStrings.add(searchedText.substring(0, lowestIndex));
					searchedText = searchedText.substring(lowestIndex, searchedText.length()); //remove current spotTag from search phrase
					Debug.printDbgLine("ResultController.java: createTaggedSearchString(): added: "+partialStrings.get(partialStrings.size()-1));
				}
			
			}
		} // END while (searchedText.length()>0)
		
		
		// return joined partialStrings
		String htmlString = "";		
		for (String s : partialStrings){
			htmlString += s;
		}
		
		return htmlString;
	}

	//reinizializza i pannelli con una nuova ricerca
	private void newSearch(){

		//show Loading Icon
		RootPanel.get("homeLoading").setVisible(true);
		Utility.showLoadingBar("Reinitializing Panels");

		listFD = new ArrayList<String>();

		//brutal way: reload page (really long wait)
		//Window.Location.reload();

		//little less brutal way: reinit all items (almost as long)
		/*
		RootPanel.get("searchContainer").clear();				
		RootPanel.get("servicesContainer").clear();
		RootPanel.get("resultsContainer").clear();
		initItems();
		 */


		//reinit search area
		RootPanel.get("searchContainer").clear();
		initSearchPanel();
		RootPanel.get("searchContainer").add(titleContentPanel);

		//reinit service status panel items
		InitServiceStatusPanel();

		//reinit results panel items
		rc.init();

		//hide Loading Icon
		RootPanel.get("homeLoading").setVisible(false);
		Utility.hideLoadingBar();
	}

}