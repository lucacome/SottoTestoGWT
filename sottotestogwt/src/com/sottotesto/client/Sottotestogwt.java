package com.sottotesto.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sottotesto.server.JData;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.FieldVerifier;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.TreeData;
import com.sottotesto.shared.TreeDataProperties;
import com.sottotesto.shared.Utility;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {

	private int panelsMaxWidth;

	//title panel items
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
//	private String finalFDstring;
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label textAreaLabel;   //label for text area
	private Label errorLabel;      //errorlabel for textarea misuse
	private ContentPanel titleContentPanel;
	private HTML titleHTML;
	private HorizontalLayoutContainer searchPanelHC;
	private FlowPanel textAreaFP;
	private List<String> listFD = new ArrayList<String>();

	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
	private final DBPediaQueryAsync dbpqService = GWT.create(DBPediaQuery.class);
	private final ListServiceAsync listService = GWT.create(ListService.class);
	private final GraphServiceAsync GraphService = GWT.create(GraphService.class);
	
	private int ekpRemainingCallNum = 0;
	private List<String> ekpRemainingCallInputs = new ArrayList<String>();


	//Service status panel items
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
	private DBPediaResponse dbpediaResp; //response of DBPEDIA service
	private List<EkpResponse> ekpResp; //response of EKP service
	int gpsCallsMax, gpsCallsEnded;

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

		panelsMaxWidth = RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*3/100);

		//init services responses
		tagmeResp = new TagmeResponse();
		dbpediaResp = new DBPediaResponse();
		ekpResp = new ArrayList<EkpResponse>();

		//add search panel to page
		initSearchPanel();		
		RootPanel.get("searchContainer").add(titleContentPanel);

		//add service panel to page
		InitServiceStatusPanel();
		RootPanel.get("servicesContainer").add(serviceStatusPanel);

		//add results panel to page
		rc = new ResultController();
		rc.init();
		RootPanel.get("resultsContainer").add(rc.getPanel());

	}		

	private void initSearchPanel(){
		//init input section items

		if (titleContentPanel != null) titleContentPanel.clear();

		textAreaLabel = new Label();
		textAreaLabel.getElement().setClassName("searchAreaLabel"); //for css styling
		textAreaLabel.setText("Scrivi una frase:");				
		textAreaDefText = "Enter something here...";
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
		titleContentPanel.setWidth(panelsMaxWidth);
		titleContentPanel.setHeight(RootPanel.get("searchContainer").getOffsetHeight()+"px");

		textAreaFP = new FlowPanel();
		textAreaFP.setStylePrimaryName("searchAreaFlowPanel");
		//textAreaFP.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		//textAreaFP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		textAreaFP.add(textArea);
		textAreaFP.add(errorLabel);

		searchPanelHC = new HorizontalLayoutContainer();
		searchPanelHC.setId("searchHorContainer");
		searchPanelHC.setWidth(panelsMaxWidth);
		searchPanelHC.setBorders(false);
		searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		searchPanelHC.add(textAreaFP, new HorizontalLayoutData(0.70, 1, new Margins(4)));
		searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 1, new Margins(14)));			

		titleContentPanel.setWidget(searchPanelHC);
		titleContentPanel.setId("searchAreaPanel");

		// Create a handler for the sendButton and nameField
		class HomeInputHandler implements ClickHandler, KeyUpHandler {
			public void onClick(ClickEvent event) {
				Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): sendButtonOnClick()");
				callTagme();
			}
			public void onKeyUp(KeyUpEvent event) {				
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): textAreaOnKeyUpEnter()");
					textArea.setText(textArea.getText().replace("\n", ""));	
					if (sendButton.isEnabled()){
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


	//send input from textarea to tagme
	private void callTagme() {
		Debug.printDbgLine("Sottotestogwt.java: callTagme()");
		// First, we validate the input.
		errorLabel.setText("");
		String textToServer = textArea.getText();
		if (!FieldVerifier.isValidTagRequest(textToServer)) {
			errorLabel.setText("Inserisci qualcosa!");
			return;
		}

		sendButton.setEnabled(false);

		//reinit service status panel items
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);

		//reinit results panel items
		rc.reInit();

		//actually call tagme service
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringCalling);		
		tagmeService.sendToServer(textToServer, new AsyncCallback<TagmeResponse>() {
			public void onFailure(Throwable caught) {
				Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onFailure()");

				sendButton.setEnabled(true); //allow user for a new search

				//set the error
				tagmeResp = new TagmeResponse();
				tagmeResp.setCode(-1);
				tagmeResp.setError("Error Callig service Module:"+
						"<br><br>StackTrace: "+Utility.getErrorHtmlString(caught));

				HtmlTagmeService.setHTML(HTMLtagmeServiceStringFAIL); //show the fail

				//skip all other services
				HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
				HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
				rc.showError();
			}

			public void onSuccess(TagmeResponse result) {
				Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onSuccess()");

				sendButton.setEnabled(true); //allow user for a new search

				tagmeResp = result; //save the result				

				if (!(tagmeResp.getCode()==200)){
					//Tagme ha avuto qualche problema!

					HtmlTagmeService.setHTML(HTMLtagmeServiceStringFAIL); //show the fail

					//skip all other services
					HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
					HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
					rc.showError();
				}
				else if (tagmeResp.getTitleTag().size()==0){
					//Tagme OK, ma non ha taggato nulla!

					HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK+" (but 0!)");

					//skip all other services
					HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
					HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
					rc.showError();
				}
				else {
					//Tagme OK

					HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK); //show the success
					showTaggedResult();

					//chiamiamo DBPedia							
					List<String> dbproperty = new ArrayList<String>();
					//dbproperty.add("birthDate");
					//dbproperty.add("title");
					//dbproperty.add("name");
					//dbproperty2.add("placeOfBirth");
					dbproperty.add("abstract");
					callDBPedia(dbproperty);
					//callDBPedia(dbproperty2);

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

					ekpResp = new ArrayList<EkpResponse>();
					callEkp();
					Debug.printDbgLine("SONO QUI");
				}						
			}
		});
	}

	private void callDBPedia(List<String> dbprop){
		Debug.printDbgLine("Sottotestogwt.java: callDBPedia()");	

		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringCalling);		
		dbpediaService.sendToServer(tagmeResp, dbprop, new AsyncCallback<DBPediaResponse>() {
			public void onFailure(Throwable caught) {
				//set the error
				dbpediaResp = new DBPediaResponse(); 
				dbpediaResp.setCode(-1);
				dbpediaResp.setQueryResultXML("Error Callig service Module:"+
						"<br><br><b>StackTrace: </b><br>"+Utility.getErrorHtmlString(caught));

				HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL); //show the fail
			}

			public void onSuccess(DBPediaResponse result) {	
				//Debug.printDbgLine("Sottotestogwt.java: DBPedia result="+result.getQueryResultXML());

				dbpediaResp = result;

				if (dbpediaResp.getCode()!=200) HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL);
				else HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);			
			}});

	}

	private void callEkp(){
		Debug.printDbgLine("Sottotestogwt.java: callEkp()");

		String input="";

		if (ekpRemainingCallNum>0){
			input=ekpRemainingCallInputs.remove(0);
			ekpRemainingCallNum--;
		} else return;		

		Debug.printDbgLine("Sottotestogwt.java: Ekp input="+input);
		ekpService.sendToServer(input, new AsyncCallback<EkpResponse>() {
			public void onFailure(Throwable caught) {
				//set the error
				EkpResponse ekpRespTmp = new EkpResponse();
				ekpRespTmp.setCode(-1);
				ekpRespTmp.setError("Error Callig service Module:"+
						"<br><br>StackTrace: "+Utility.getErrorHtmlString(caught));

				ekpResp.add(ekpRespTmp);
				HtmlEkpService.setHTML(HTMLekpServiceStringFAIL); //show the fail

				if (ekpRemainingCallNum>0){callEkp();}
			}

			public void onSuccess(EkpResponse result) {			
				Debug.printDbgLine("Sottotestogwt.java: Ekp output="+result.getMessage());

				EkpResponse ekpRespTmp = new EkpResponse();
				ekpRespTmp = result;

				String jsonHT = "["+result.jdataHT+"]";
				String jsonFD = "["+result.jdataFD+"]";	

				callListService(ekpRespTmp);
				listFD.add(jsonFD);
				
				//rc.setJsonHT(tem);
				//rc.setJsonFD(tem2);
				//Debug.printDbgLine("MAIN="+tem);
				
				// create tree entry for Maps
				tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++),result.getTag().replaceAll("_", " "), TreeData.CLICK_ACTIONS.SHOWMAP));
				treeStore.add(tdMap, tdEntriesList.get(tdEntriesList.size()-1));

				// create tree entry for frocedirected graph
				tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++),result.getTag().replaceAll("_", " "), TreeData.CLICK_ACTIONS.SHOWGRAPH_FD));
				tdEntriesList.get(tdEntriesList.size()-1).setJsonFD(jsonFD);
				treeStore.add(tdForcedirected, tdEntriesList.get(tdEntriesList.size()-1));

				// create tree entry for hypertree graph
				tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++),result.getTag().replaceAll("_", " "), TreeData.CLICK_ACTIONS.SHOWGRAPH_HT));
				tdEntriesList.get(tdEntriesList.size()-1).setJsonHT(jsonHT);
				treeStore.add(tdHyperTree, tdEntriesList.get(tdEntriesList.size()-1));		

				ekpResp.add(ekpRespTmp);
				if (ekpRespTmp.getCode()==200) HtmlEkpService.setHTML(HTMLekpServiceStringOK); //show the success
				else HtmlEkpService.setHTML(HTMLekpServiceStringFAIL); //show the fail

				if (ekpRemainingCallNum>0){
					callEkp();
				}
				else {
					rc.loadTree(treeStore); 
					rc.getTree().expandAll();
					callGraphService(listFD);
				}
			}});		
	}

	private void updateDBpediaServiceLabel(){
		gpsCallsEnded++;
		if (gpsCallsEnded >= gpsCallsMax){
			HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);
		}
		else{
			HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringCalling+" ("+gpsCallsEnded+"/"+gpsCallsMax+")");
		}
	}

	private void callDBPediaQuery(DBPQueryResp resp){
		Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery()");

		dbpqService.sendToServer(resp, new AsyncCallback<DBPQueryResp>() {
			public void onFailure(Throwable caught) {
				//set the error
				updateDBpediaServiceLabel();
				Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery(): onFailure() - call n."+gpsCallsEnded+"/"+gpsCallsMax);
			}

			public void onSuccess(DBPQueryResp result) {
				updateDBpediaServiceLabel();
				Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery(): onSuccess() - call n."+gpsCallsEnded+"/"+gpsCallsMax);

				Debug.printDbgLine("entity="+result.getEntity()+", link="+result.getLink()+", name="+result.getName()+", gps="+result.getLat()+", "+result.getLng()+", relation="+result.getRelation()+", relatio="+result.getRelation()+", abstract="+result.getAbstract());
				if (result.getLat() != 0.0 && result.getLng() != 0.0)
					rc.addDBpediaMarkerSingleToMap(result);  
			}});

	}
	private void callListService(EkpResponse resp){
		Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery()");

		gpsCallsMax=0;
		gpsCallsEnded=0;

		listService.sendToServer(resp, new AsyncCallback<List<DBPQueryResp>>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printDbgLine("Sottotestogwt.java: callListService(): onFailure()");
			}

			public void onSuccess(List<DBPQueryResp> result) {
				Debug.printDbgLine("Sottotestogwt.java: callListService(): onSuccess()");

				gpsCallsMax=result.size();

				Iterator<DBPQueryResp> iter = result.iterator();
				Debug.printDbgLine("Sottotestogwt.java: callListService(): onSuccess(): DBPQ Size="+result.size());

				DBPQueryResp temp = new DBPQueryResp();

				while (iter.hasNext()){
					temp = iter.next();
					callDBPediaQuery(temp);
				}

			}});

	}
	
	private void callGraphService(List<String> listFD){
		
		GraphService.sendToServer(listFD, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				//set the error
				Debug.printDbgLine("Sottotestogwt.java: callGraphService(): onFailure()");
			}

			public void onSuccess(String result) {
				Debug.printDbgLine("Sottotestogwt.java: callGraphService(): onSuccess()");
				Debug.printDbgLine(result);
			}
			});
		
	}


	private void InitServiceStatusPanel(){
		Debug.printDbgLine("Sottotestogwt.java: initServiceStatusPanel()");

		//main panel
		serviceStatusPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		serviceStatusPanel.setHeadingText("Service Status");
		serviceStatusPanel.setId("serviceStatusPanel");
		serviceStatusPanel.setWidth(panelsMaxWidth);
		serviceStatusPanel.setHeight(RootPanel.get("servicesContainer").getOffsetHeight()+"px");
		serviceStatusPanelHC = new HorizontalLayoutContainer();

		//tagme service
		HtmlTagmeService = new HTML();
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringWaiting);
		HtmlTagmeService.setStylePrimaryName("serviceLabel");
		tagmeStatusVP = new VerticalPanel();
		tagmeStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		tagmeStatusVP.add(HtmlTagmeService);	
		tagmeStatusVP.setBorderWidth(0);
		HtmlTagmeService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlTagmeService.getHTML().contains("Waiting")) Utility.showTagmeDataDB(tagmeResp);}});

		//dbpedia service
		HtmlDBPediaService = new HTML();
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlDBPediaService.setStylePrimaryName("serviceLabel");
		dbpediaStatusVP = new VerticalPanel();
		dbpediaStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dbpediaStatusVP.add(HtmlDBPediaService);
		dbpediaStatusVP.setBorderWidth(0);
		HtmlDBPediaService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlDBPediaService.getHTML().contains("Waiting")) Utility.showDBPediaDataDB(dbpediaResp);}});

		//ekp service
		HtmlEkpService = new HTML();
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);
		HtmlEkpService.setStylePrimaryName("serviceLabel");
		ekpStatusVP = new VerticalPanel();
		ekpStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		ekpStatusVP.add(HtmlEkpService);
		ekpStatusVP.setBorderWidth(0);
		HtmlEkpService.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){
			if(!HtmlEkpService.getHTML().contains("Waiting")) Utility.showEkpDataDB(ekpResp);}});

		//add items to panel
		serviceStatusPanelHC.add(HtmlTagmeService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(HtmlEkpService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(HtmlDBPediaService, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.setBorders(false);
		serviceStatusPanel.setWidget(serviceStatusPanelHC);		

	}	

	private void initEmptyTree(){	
		Debug.printDbgLine("Sottotestogwt.java: initEmptyTree()");

		tdEntriesList = new ArrayList<TreeData>();

		treeDataIdProvider = 0; //tree entries id counter
		treeProperties = GWT.create(TreeDataProperties.class); // Generate the key provider and value provider for the Data class
		treeStore = new TreeStore<TreeData>(treeProperties.id()); // Create the store that the contains the data to display in the tree
		tdForcedirected = new TreeData(String.valueOf(treeDataIdProvider++),"ForceDirected Graph");
		tdHyperTree = new TreeData(String.valueOf(treeDataIdProvider++),"Hypertree Graph");
		tdMap = new TreeData(String.valueOf(treeDataIdProvider++),"Knowledge Map");

		treeStore.add(tdMap);
		treeStore.add(tdForcedirected);
		treeStore.add(tdHyperTree);

		//add 'all entity' entry to tree
		tdEntriesList.add(new TreeData(String.valueOf(treeDataIdProvider++), "Mappa completa", TreeData.CLICK_ACTIONS.SHOWMAP));
		treeStore.add(tdMap, tdEntriesList.get(tdEntriesList.size()-1));
	}

	private void showTaggedResult(){
		textAreaLabel.setText("Entita' rilevate:");
		HTML htmlPhrase = new HTML();
		htmlPhrase.setHTML(createTaggedSearchString());
		htmlPhrase.setStylePrimaryName("taggedPhraseHTMLcontainer");

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
		searchPanelHC.setWidth(panelsMaxWidth);
		searchPanelHC.setBorders(false);
		searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		searchPanelHC.add(htmlPhrase, new HorizontalLayoutData(0.70, 1, new Margins(4)));
		searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 1, new Margins(14)));

		titleContentPanel.clear();
		titleContentPanel.setHeadingHtml("SOTTOTESTO WEBAPPLICATION");

		titleContentPanel.setWidget(searchPanelHC);
	}

	private String createTaggedSearchString(){
		Debug.printDbgLine("ResultController.java: createTaggedSearchString()");

		String result = "<span class=\"result_searchedPhrase\">"+textArea.getText()+"</span>";
		String curTag="";
		String curTitle="";
		Debug.printDbgLine("ResultController.java: createTaggedSearchString(): result = "+result);

		//CHECK TAGGED ENTRIES
		List<String> taggedEntries;
		taggedEntries = new ArrayList<String>(tagmeResp.getSpotTag());		
		Iterator<String> iterTags =  taggedEntries.iterator();		
		List<String> taggedTitles;
		taggedTitles = new ArrayList<String>(tagmeResp.getTitleTag());		
		Iterator<String> iterTitle =  taggedTitles.iterator();				
		while (iterTags.hasNext()){
			curTag=iterTags.next();	
			curTag=curTag.replaceAll("_", " ");
			curTitle=iterTitle.next();
			Debug.printDbgLine("ResultController.java: createTaggedSearchString(): curTag cleared= "+curTag);	

			result=result.replaceAll(curTag, "<span class=\"result_taggedWord\" title=\""+curTitle+"\">"+curTag+"</span>");
			Debug.printDbgLine("ResultController.java: createTaggedSearchString(): result mod = "+result);	
		}

		//CHECK TAGGED ENTRIES WITH LOW ROH
		List<String> skippedEntries;
		skippedEntries = new ArrayList<String>(tagmeResp.getSpotSkipped());		
		Iterator<String> iterSkippedTags =  skippedEntries.iterator();		
		List<String> skippedTitles;
		skippedTitles = new ArrayList<String>(tagmeResp.getTitleSkipped());		
		Iterator<String> iterSkippedTitle =  skippedTitles.iterator();				
		while (iterSkippedTags.hasNext()){
			curTag=iterSkippedTags.next();	
			curTag=curTag.replaceAll("_", " ");
			curTitle=iterSkippedTitle.next();
			Debug.printDbgLine("ResultController.java: createTaggedSearchString(): curTag cleared= "+curTag);	

			result=result.replaceAll(curTag, "<span class=\"result_skippedWord\" title=\""+curTitle+"\">"+curTag+"</span>");
			Debug.printDbgLine("ResultController.java: createTaggedSearchString(): result mod = "+result);	
		}

		return result;
	}

	//reinizializza i pannelli con una nuova ricerca
	private void newSearch(){

		//show Loading Icon
		RootPanel.get("homeLoading").setVisible(true);



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
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringWaiting);
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);

		//reinit results panel items
		RootPanel.get("resultsContainer").clear();
		rc.init();
		RootPanel.get("resultsContainer").add(rc.getPanel());

		//show Loading Icon
		RootPanel.get("homeLoading").setVisible(false);
	}

}