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
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sottotesto.client.jit.JITGraph;
import com.sottotesto.client.jit.NativeJITGraph;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.FieldVerifier;
import com.sottotesto.shared.HTMLInjector;
import com.sottotesto.shared.STResources;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.Utility;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {
	
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label textAreaLabel;   //label for text area
	private Label errorLabel;      //errorlabel for textarea misuse
	private Label serverResponseLabel;
	
	
	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
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
	
	//service responses variables
	private TagmeResponse tagmeResp; //response of TAGME service
	private DBPediaResponse dbpediaResp; //response of DBPEDIA service
	private List<EkpResponse> ekpResp; //response of EKP service
	
	//resources
	TextResource resourceHTMLmap;
	DataResource resourceHTMLdata;
	Frame frameHTML;
	
	static JITGraph jitGraph = new JITGraph();
	private NativeJITGraph graph;
	
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
		textArea.addKeyUpHandler(hihandler);
		
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
	
	/**
	 * callback class which displays the JSON object
	 */
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
	
	//initialize items and load them on page
	private void initItems(){
		Debug.printDbgLine("Sottotestogwt.java: initItems()");

		//init services responses
		tagmeResp = new TagmeResponse();
		dbpediaResp = new DBPediaResponse();
		ekpResp = new ArrayList<EkpResponse>();
		
		//init input section items
		textAreaLabel = new Label();
		textAreaLabel.setText("Scrivi una frase:");
		textAreaDefText = "Enter something here...";
		textSendButton = "Send";
		sendButton = new Button(textSendButton);
		textArea = new TextArea();
		textArea.setText(textAreaDefText);		
		textArea.setSize("400px", "150px");
		errorLabel = new Label();
		serverResponseLabel = new Label();
		sendButton.addStyleName("sendButton");
		
		//init Resources
		resourceHTMLmap = STResources.INSTANCE.HTMLmap();
		
		// Add items to the RootPanel (Use RootPanel.get() to get the entire body element)
		RootPanel.get("textAreaLabel").add(textAreaLabel);
		RootPanel.get("textAreaContainer").add(textArea);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);		
		RootPanel.get("tagmetext").add(serverResponseLabel);
		
		
		
		//TEST BUTTON: FOR TESTING... OF COURSE
		Button testButton = new Button();
		testButton.setText("Test");
		testButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				Debug.printDbgLine("Sottotestogwt.java: testButtonClick()");
				//showGraph("nulla");
				ResultController rc = new ResultController();
				rc.init();
			}
		});
		RootPanel.get("sendButtonContainer").add(testButton);
		
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
		//RootPanel.get("fondo").add(frameHTML);*/		
		//HTMLInjector.injectLinkExhibit("#jsondata");
		//HTMLInjector.injectJsScriptExternal("http://api.simile-widgets.org/exhibit/3.0.0/exhibit-api.js");
		//ExhibitLoad("testa di cazzo");
		
		
		
		// Focus the cursor on the name field when the app loads
		textArea.setFocus(true);
		textArea.selectAll();
		
		
	}	
	
	private native void showGraph(String name)/*-{
	  name = {
        "id": "1",
        "name": "Main",
        "children": [{
            "id": "2",
            "name": "Child1",
            "data": {
                "band": "Nine Inch Nails",
                "relation": "member of band"
            	},
            "children": [{
	        "id": "3",
            	"name": "SubChild1",
            	"data": {
                	"band": "Nine Inch Nails",
                	"relation": "member of band"
            		},
		"children": []
            }]
        }],
        "data": []
    };
    
    var json = name;
	new $wnd.init(json);
}-*/;
	
	//send input from textarea to tagme
	private void callTagme() {
		Debug.printDbgLine("Sottotestogwt.java: callTagme()");
		HTMLInjector.injectJsScriptExternal("http://api.simile-widgets.org/exhibit/3.0.0/exhibit-api.js");
		// First, we validate the input.
		errorLabel.setText("");
		String textToServer = textArea.getText();
		if (!FieldVerifier.isValidTagRequest(textToServer)) {
			errorLabel.setText("Inserisci qualcosa!");
			return;
		}
		
		 
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
		
		//reinitialize graphic
		RootPanel.get("homeLoading").setVisible(true);
		sendButton.setEnabled(false);
		serverResponseLabel.setText("");		
		if (serviceStatusPanel != null) {serviceStatusPanel.hide();
										 serviceStatusPanel.clear();
										 serviceStatusPanel.removeFromParent();										 
		}
		InitServiceStatusPanel();
		RootPanel.get("homeLoading").setVisible(false);
		
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
						}
						else {
							//Tagme OK
							
							HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK); //show the success
							tagmeShowDataBTN.setVisible(true); //allow to see the data
							
							//initialize results panel
							rc = new ResultController();
							rc.init();
														
							//chiamiamo DBPedia							
							List<String> dbproperty = new ArrayList<String>();
							List<String> dbproperty2 = new ArrayList<String>();
							dbproperty.add("birthDate");
							dbproperty.add("title");
							dbproperty.add("name");
							dbproperty2.add("placeOfBirth");
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
			
			String tem = "["+result.jdata+"]";
			rc.setJsonFD(tem);
			Debug.printDbgLine("MAIN="+tem);
			
			ekpResp.add(ekpRespTmp);
			if (ekpRespTmp.getCode()==200) HtmlEkpService.setHTML(HTMLekpServiceStringOK); //show the success
			else HtmlEkpService.setHTML(HTMLekpServiceStringFAIL); //show the fail
			ekpShowDataBTN.setVisible(true); //allow to see data
			
			if (ekpRemainingCallNum>0){callEkp();}
		}});		
	}
	
	private void InitServiceStatusPanel(){
		Debug.printDbgLine("Sottotestogwt.java: initServiceStatusPanel()");
		
		//main panel
		serviceStatusPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		serviceStatusPanel.setHeadingText("Service Status");
		serviceStatusPanel.setPixelSize(RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*5/100), 100);
		serviceStatusPanel.setCollapsible(true);
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
		
		//show the status panel
		RootPanel.get("serviceStatusPanel").add(serviceStatusPanel);		
	}	
}
