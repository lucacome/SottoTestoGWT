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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
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
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.FieldVerifier;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.TreeData;
import com.sottotesto.shared.TreeDataProperties;
import com.sottotesto.shared.Utility;

//prove exhibit e neo4j
/*import com.sottotesto.client.jit.JITGraph;
import com.sottotesto.client.jit.NativeJITGraph;
import com.sottotesto.shared.HTMLInjector;*/

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {
	
	private int panelsMaxWidth;
	
	//title panel items
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label textAreaLabel;   //label for text area
	private Label errorLabel;      //errorlabel for textarea misuse
	private Label serverResponseLabel;
	private ContentPanel titleContentPanel;
	private HTML titleHTML;
	private HorizontalLayoutContainer searchPanelHC;
	private VerticalPanel textAreaVP, searchAreaVP;
	
	
	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
	private final DBPediaQueryAsync dbpqService = GWT.create(DBPediaQuery.class);
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
	String HTMLloadIconString="<img src='loading.gif'/>";
	String HTMLtagmeServiceStringWaiting="Tagme Service: Waiting.";
	String HTMLtagmeServiceStringCalling="Tagme Service: Calling ..."+HTMLloadIconString;
	String HTMLtagmeServiceStringOK="Tagme Service: <span style='color:green;'>SUCCESS</span>";
	String HTMLtagmeServiceStringFAIL="Tagme Service: <span style='color:red;'>FAILED</span>";
	String HTMLtagmeServiceStringSkipped="Tagme Service: <span style='color:orange;'>Skipped</span>";
	String HTMLdbpediaServiceStringWaiting="DBPedia Service: Waiting.";
	String HTMLdbpediaServiceStringCalling="DBPedia Service: Calling ..."+HTMLloadIconString;
	String HTMLdbpediaServiceStringOK="DBPedia Service: <span style='color:green;'>SUCCESS</span>";
	String HTMLdbpediaServiceStringFAIL="DBPedia Service: <span style='color:red;'>FAILED</span>";
	String HTMLdbpediaServiceStringSkipped="DBPedia Service: <span style='color:orange;'>Skipped</span>";
	String HTMLekpServiceStringWaiting="Ekp Service: Waiting.";
	String HTMLekpServiceStringCalling="Ekp Service: Calling ..."+HTMLloadIconString;
	String HTMLekpServiceStringOK="Ekp Service: <span style='color:green;'>SUCCESS</span>";
	String HTMLekpServiceStringFAIL="Ekp Service: <span style='color:red;'>FAILED</span>";
	String HTMLekpServiceStringSkipped="Ekp Service: <span style='color:orange;'>Skipped</span>";
	Button tagmeShowDataBTN;
	Button dbpediaShowDataBTN;
	Button ekpShowDataBTN;
	
	 
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
	
	//resources
	TextResource resourceHTMLmap;
	DataResource resourceHTMLdata;
	Frame frameHTML;
	
	//prove neo4j
	//static JITGraph jitGraph = new JITGraph();
	//private NativeJITGraph graph;
	
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
		
		//hide loading icon
		RootPanel.get("homeLoading").setVisible(false);
		
		Debug.printDbgLine("Sottotestogwt.java: HomePage loaded in "+(System.currentTimeMillis()-homeTimeStart)+"ms, waiting input ...");
		
		//PORVA JIT NEO4J
		/*
		VerticalPanel mainPanel = new VerticalPanel();
	    graph = new ForceDirected();//RGraph();
	    final Button button = new Button("Load Path");
	    button.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            jitGraph.getPathREST("3", new MyAsyncCallback(graph));
	        }
	    });
	    mainPanel.add((Widget)graph);
		mainPanel.add(button);
	    RootPanel.get("fondo").add(mainPanel);
		*/
		
		
	}
	
	
	//prova neo4j
	/*
	private static class MyAsyncCallback implements AsyncCallback<String> {
	    private NativeJITGraph graph;

	    public MyAsyncCallback(NativeJITGraph graph) {
	    	Debug.printDbgLine("JITGraph.java: MyAsyncCallback()");
	        this.graph = graph;
	    }

	    public void onSuccess(String data) {
	    	Debug.printDbgLine("JITGraph.java: MyAsyncCallback(): on success()");
	        graph.loadData(data);
	    }

	    public void onFailure(Throwable throwable) {
	    	Debug.printDbgLine("JITGraph.java: MyAsyncCallback(): onFailure()");
	    }
	}	
	*/
	
	//initialize items and load them on page
	private void initItems(){
		Debug.printDbgLine("Sottotestogwt.java: initItems()");

		panelsMaxWidth = RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*5/100);
		
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
				textAreaLabel = new Label();
				textAreaLabel.setText("Scrivi una frase:");
				textAreaLabel.addStyleName("margin-top:10px");
				textAreaDefText = "Enter something here...";
				textSendButton = "Send";
				sendButton = new Button(textSendButton);
				textArea = new TextArea();
				textArea.setText(textAreaDefText);		
				textArea.setSize("550px", "25px");				
				textArea.setFocus(true);
				textArea.selectAll();
				errorLabel = new Label();
				serverResponseLabel = new Label();
				sendButton.addStyleName("sendButton");
				
				titleHTML = new HTML();
				titleHTML.setHTML("<h1>Sottotesto Web App</h1>");
				titleHTML.setHeight("10px");
				titleContentPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
				titleContentPanel.setHeadingHtml("Search Area");
				titleContentPanel.setWidth(panelsMaxWidth);
				titleContentPanel.setHeight(100);
				
				textAreaVP = new VerticalPanel();
				textAreaVP.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
				textAreaVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
				textAreaVP.add(textArea);
				textAreaVP.add(errorLabel);
				
				searchPanelHC = new HorizontalLayoutContainer();
				searchPanelHC.setWidth(panelsMaxWidth);
				searchPanelHC.setBorders(true);
				searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
				searchPanelHC.add(textAreaVP, new HorizontalLayoutData(0.70, 1, new Margins(4)));
				searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 0.1, new Margins(14)));			
				
				searchAreaVP = new VerticalPanel();
				searchAreaVP.setWidth(String.valueOf(panelsMaxWidth)+"px");
				searchAreaVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
				searchAreaVP.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
				searchAreaVP.add(titleHTML);
				searchAreaVP.add(searchPanelHC);
				
				titleContentPanel.setWidget(searchPanelHC);
				
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
		//HTMLInjector.injectJsScriptExternal("http://api.simile-widgets.org/exhibit/3.0.0/exhibit-api.js");
		// First, we validate the input.
		errorLabel.setText("");
		String textToServer = textArea.getText();
		if (!FieldVerifier.isValidTagRequest(textToServer)) {
			errorLabel.setText("Inserisci qualcosa!");
			return;
		}
			
		sendButton.setEnabled(false);
		serverResponseLabel.setText("");	
		
		//reinit service status panel items
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);
		dbpediaShowDataBTN.setVisible(false);
		ekpShowDataBTN.setVisible(false);
		tagmeShowDataBTN.setVisible(false);
		
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
						tagmeShowDataBTN.setVisible(true); //allow to see what gone wrong
						
						//skip all other services
						HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
						dbpediaShowDataBTN.setVisible(false);
						HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
						ekpShowDataBTN.setVisible(false);
						rc.showError();
					}

					public void onSuccess(TagmeResponse result) {
						Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onSuccess()");
						
						sendButton.setEnabled(true); //allow user for a new search
						
						tagmeResp = result; //save the result				
												
						if (!(tagmeResp.getCode()==200)){
							//Tagme ha avuto qualche problema!
							
							HtmlTagmeService.setHTML(HTMLtagmeServiceStringFAIL); //show the fail
							tagmeShowDataBTN.setVisible(true); //allow to see what gone wrong
							
							//skip all other services
							HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
							dbpediaShowDataBTN.setVisible(false);
							HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
							ekpShowDataBTN.setVisible(false);
							rc.showError();
						}
						else if (tagmeResp.getTitleTag().size()==0){
							//Tagme OK, ma non ha taggato nulla!
							
							HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK+" (but 0!)");
							tagmeShowDataBTN.setVisible(true);
							
							//skip all other services
							HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringSkipped);
							dbpediaShowDataBTN.setVisible(false);
							HtmlEkpService.setHTML(HTMLekpServiceStringSkipped);
							ekpShowDataBTN.setVisible(false);
							rc.showError();
						}
						else {
							//Tagme OK
							
							HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK); //show the success
							tagmeShowDataBTN.setVisible(true); //allow to see the data	
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
			dbpediaShowDataBTN.setVisible(true); //allow to see what gone wrong
		}

		public void onSuccess(DBPediaResponse result) {	
			//Debug.printDbgLine("Sottotestogwt.java: DBPedia result="+result.getQueryResultXML());
			
			dbpediaResp = result;
			
			if (dbpediaResp.getCode()!=200) HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL);
			else HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);
			
			dbpediaShowDataBTN.setVisible(true);
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
			ekpShowDataBTN.setVisible(true); //allow to see what gone wrong		
			
			if (ekpRemainingCallNum>0){callEkp();}
		}

		public void onSuccess(EkpResponse result) {			
			Debug.printDbgLine("Sottotestogwt.java: Ekp output="+result.getMessage());
			
			EkpResponse ekpRespTmp = new EkpResponse();
			ekpRespTmp = result;
			
			String jsonHT = "["+result.jdataHT+"]";
			String jsonFD = "["+result.jdataFD+"]";	
			
			callDBPediaQuery(ekpRespTmp);
			
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
			ekpShowDataBTN.setVisible(true); //allow to see data
			
			if (ekpRemainingCallNum>0){callEkp();}
			else {rc.loadTree(treeStore); rc.getTree().expandAll();}
		}});		
	}
	
	private void callDBPediaQuery(EkpResponse resp){
        Debug.printDbgLine("Sottotestogwt.java: callDBPediaQuery()");

        dbpqService.sendToServer(resp, new AsyncCallback<List<DBPQueryResp>>() {
        public void onFailure(Throwable caught) {
                //set the error

        }

        public void onSuccess(List<DBPQueryResp> result) {
                //Debug.printDbgLine("Sottotestogwt.java: DBPedia result="+result.getQueryResultXML());
                Iterator<DBPQueryResp> iter = result.iterator();
                Debug.printDbgLine("DBPQ Size="+result.size());

                DBPQueryResp temp = new DBPQueryResp();

                while (iter.hasNext()){
                	temp = iter.next();
                	rc.addMarkerToList(temp);
                	Debug.printDbgLine("entity="+temp.getEntity()+", link="+temp.getLink()+", name="+temp.getName()+", gps="+temp.getLat()+", "+temp.getLng());
                }
                
                rc.addDBpediaMarkeListToMap(result);
        }});

}

	
	private void InitServiceStatusPanel(){
		Debug.printDbgLine("Sottotestogwt.java: initServiceStatusPanel()");
		
		//main panel
		serviceStatusPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		serviceStatusPanel.setHeadingText("Service Status");
		serviceStatusPanel.setPixelSize(panelsMaxWidth, 100);
		//serviceStatusPanel.setCollapsible(true);
		serviceStatusPanelHC = new HorizontalLayoutContainer();
		
		//tagme service
		HtmlTagmeService = new HTML();
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringWaiting);
		tagmeShowDataBTN = new Button();
		tagmeShowDataBTN.setText("View Data");
		tagmeShowDataBTN.setVisible(false);
		tagmeStatusVP = new VerticalPanel();
		tagmeStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		tagmeStatusVP.add(HtmlTagmeService);
		tagmeStatusVP.add(tagmeShowDataBTN);		
		tagmeStatusVP.setBorderWidth(0);
		tagmeShowDataBTN.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){Utility.showTagmeDataDB(tagmeResp);}});
		
		//dbpedia service
		HtmlDBPediaService = new HTML();
		HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringWaiting);
		dbpediaShowDataBTN = new Button();
		dbpediaShowDataBTN.setText("View Data");
		dbpediaShowDataBTN.setVisible(false);
		dbpediaStatusVP = new VerticalPanel();
		dbpediaStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dbpediaStatusVP.add(HtmlDBPediaService);
		dbpediaStatusVP.add(dbpediaShowDataBTN);		
		dbpediaStatusVP.setBorderWidth(0);
		dbpediaShowDataBTN.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){Utility.showDBPediaDataDB(dbpediaResp);}});
		
		//ekp service
		HtmlEkpService = new HTML();
		HtmlEkpService.setHTML(HTMLekpServiceStringWaiting);
		ekpShowDataBTN = new Button();
		ekpShowDataBTN.setText("View Data");
		ekpShowDataBTN.setVisible(false);
		ekpStatusVP = new VerticalPanel();
		ekpStatusVP.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		ekpStatusVP.add(HtmlEkpService);
		ekpStatusVP.add(ekpShowDataBTN);		
		ekpStatusVP.setBorderWidth(0);
		ekpShowDataBTN.addClickHandler(new ClickHandler(){public void onClick(ClickEvent event){Utility.showEkpDataDB(ekpResp);}});
		
		//add items to panel
		serviceStatusPanelHC.add(tagmeStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(dbpediaStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(ekpStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.setBorders(true);
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
		
		
		sendButton = new Button("Nuova ricerca");		
		sendButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Window.Location.reload();
			}
		});
		
		searchPanelHC.clear();
		searchPanelHC = new HorizontalLayoutContainer();
		searchPanelHC.setWidth(panelsMaxWidth);
		searchPanelHC.setBorders(true);
		searchPanelHC.add(textAreaLabel, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		searchPanelHC.add(htmlPhrase, new HorizontalLayoutData(0.70, 1, new Margins(4)));
		searchPanelHC.add(sendButton, new HorizontalLayoutData(0.15, 1, new Margins(14)));
		
		titleContentPanel.clear();
		titleContentPanel.setHeadingHtml("Analisi frase");
		
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
	
}


//PROVA EXHIBIT
		/*
		String jsonItaly = "{\"items\": [{\"id\": \"it\",\"code\": \"it\",\"type\": \"Flag\",\"label\": \"Italy\"},{\"id\": \"it\",\"latlng\": \"42.8333,12.8333\"}],\"types\": {\"Flag\": {\"pluralLabel\":\"Flags\"}}}";
		String jsonDivString = "";//"<div id=\"jsondata\" style=\"display:none\">"+jsonItaly+"</div>";
		String htmlPage = resourceHTMLmap.getText();		
		Debug.printDbgLine("htmlPage="+htmlPage); 		
		String htmlPageFull = htmlPage+jsonDivString;		
			htmlPageFull = URL.encode(htmlPageFull);
		Debug.printDbgLine("htmlPageFullEncoded="+htmlPageFull);
		HTML mapHTML = new HTML(); //mapHTML.setHTML(resourceHTMLmap.getText()+jsonDivString);
		SafeHtml mapSafeHTML = SafeHtmlUtils.fromTrustedString(jsonDivString+htmlPage);
		Debug.printDbgLine("Sottotestogwt.java: mapHTML="+mapHTML.getHTML());
		Debug.printDbgLine("Sottotestogwt.java: mapSafeHTML="+mapSafeHTML.asString());
		//mapHTML.setHTML((HTML)mapSafeHTML.asString());
		Debug.printDbgLine("Sottotestogwt.java: mapHTML="+mapHTML.getHTML());
		HTMLPanel panelHTML = new HTMLPanel(mapSafeHTML);
		//RootPanel.get("jsondata").add(new HTML(jsonItaly));
		HTMLInjector.modifyDiv("jsondata", jsonItaly);		
		//String resURL = resourceHTMLdata.getSafeUri().asString();
		String resURLwar = "map.html";
		frameHTML = new Frame(resURLwar);
		frameHTML.setPixelSize(RootPanel.get().getOffsetWidth(), 500);
		//RootPanel.get("fondo").add(frameHTML);
		 * */		
		//HTMLInjector.injectLinkExhibit("#jsondata");
		//HTMLInjector.injectJsScriptExternal("http://api.simile-widgets.org/exhibit/3.0.0/exhibit-api.js");
		//ExhibitLoad("testa di cazzo");


//PROVA MAPPA
		/*
		SimplePanel mapPanel = new SimplePanel() ;
		mapPanel.setSize("1000px", "500px");
		MapOptions options  = MapOptions.create();
      options.setCenter(LatLng.create(39.509, -98.434)); 
      options.setZoom(6);
      options.setMapTypeId(MapTypeId.ROADMAP);
      options.setDraggable(true);
      options.setMapTypeControl(true);
      options.setScaleControl(true);
      options.setScrollwheel(true);
      GoogleMap theMap = GoogleMap.create(mapPanel.getElement(), options) ;
      mapPanel.setVisible(true);
		Marker marker = Marker.create();
		marker.setPosition(LatLng.create(42.8333, 12.8333));
		marker.setMap(theMap);
	    RootPanel.get("fondo").add( mapPanel ) ;
		*/


