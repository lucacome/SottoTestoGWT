package com.sottotesto.client;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.server.testing.Parent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ContentPanel.ContentPanelAppearance;
import com.sencha.gxt.widget.core.client.FramedPanel.FramedPanelAppearance;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.LayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.FieldVerifier;
import com.sottotesto.shared.TagmeResponse;
import com.sottotesto.shared.EkpResponse;
import com.sottotesto.shared.Utility;
/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {
	
	 // The message displayed to the user when the server cannot be reached or returns an error.
	private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
	
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label errorLabel;      //errorlabel for textarea misuse
	private Label serverResponseLabel;
	private Label textAreaLabel;
	
	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
	
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
	String HTMLtagmeServiceStringSkipped="Tagme Service: <span style='color:yellow;'>Skipped</span>";
	String HTMLdbpediaServiceStringWaiting="DBPedia Service: Waiting.";
	String HTMLdbpediaServiceStringCalling="DBPedia Service: Calling ..."+HTMLloadIconString;
	String HTMLdbpediaServiceStringOK="DBPedia Service: <span style='color:green;'>SUCCESS</span>";
	String HTMLdbpediaServiceStringFAIL="DBPedia Service: <span style='color:red;'>FAILED</span>";
	String HTMLdbpediaServiceStringSkipped="DBPedia Service: <span style='color:yellow;'>Skipped</span>";
	String HTMLekpServiceStringWaiting="Ekp Service: Waiting.";
	String HTMLekpServiceStringCalling="Ekp Service: Calling ..."+HTMLloadIconString;
	String HTMLekpServiceStringOK="Ekp Service: <span style='color:green;'>SUCCESS</span>";
	String HTMLekpServiceStringFAIL="Ekp Service: <span style='color:red;'>FAILED</span>";
	String HTMLekpServiceStringSkipped="Ekp Service: <span style='color:yellow;'>Skipped</span>";
	Button tagmeShowDataBTN;
	Button dbpediaShowDataBTN;
	Button ekpShowDataBTN;
	
	//service responses variables
	private TagmeResponse tagmeResp; //response of TAGME service
	private DBPediaResponse dbpediaResp; //response of DBPEDIA service
	private EkpResponse ekpResp; //response of EKP service
	
	
	
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
					
					callTagme();					
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
	}
	
	
	
	//initialize items and load them on page
	private void initItems(){
		Debug.printDbgLine("Sottotestogwt.java: initItems()");
		
		
		
		tagmeResp = new TagmeResponse();
		dbpediaResp = new DBPediaResponse();
		ekpResp = new EkpResponse();
		
		textAreaLabel = new Label();
		textAreaLabel.setText("Scrivi una frase:");
		textAreaDefText = "Enter something here...";
		textSendButton = "Send";
		
		
		
		sendButton = new Button(textSendButton);
		textArea = new TextArea();
		textArea.setText(textAreaDefText);		
		errorLabel = new Label();
		serverResponseLabel = new Label();
		

		// We can add style names to widgets
		sendButton.addStyleName("sendButton");

				
		// Add items to the RootPanel (Use RootPanel.get() to get the entire body element)
		RootPanel.get("textAreaLabel").add(textAreaLabel);
		RootPanel.get("textAreaContainer").add(textArea);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);		
		RootPanel.get("tagmetext").add(serverResponseLabel);
		

		// Focus the cursor on the name field when the app loads
		textArea.setFocus(true);
		textArea.selectAll();
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
		// Then, we send the input to the server.
		
		RootPanel.get("homeLoading").setVisible(true);
		sendButton.setEnabled(false);
		serverResponseLabel.setText("");
		
		if (serviceStatusPanel != null) {serviceStatusPanel.clear();
										 serviceStatusPanel.removeFromParent();										 
		}
		InitServiceStatusPanel();
		RootPanel.get("homeLoading").setVisible(false);
		HtmlTagmeService.setHTML(HTMLtagmeServiceStringCalling);
		
		tagmeService.sendToServer(textToServer, new AsyncCallback<TagmeResponse>() {
					public void onFailure(Throwable caught) {
						Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onFailure()");
					
						//set the error
						tagmeResp = new TagmeResponse();
						tagmeResp.setCode(-1);
						tagmeResp.setError("Error Callig service Module:"+
										   "<br>Cause: "+caught.getCause()+
										   "<br><br>Message: "+caught.getMessage()+
										   "<br><br>StackTrace: "+caught.getStackTrace().toString());
						
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
						tagmeResp = result;				
												
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
							
							sendButton.setEnabled(true); //allow user to send new text
							
							HtmlTagmeService.setHTML(HTMLtagmeServiceStringOK); //show the success
							tagmeShowDataBTN.setVisible(true); //allow to see the data
														
							//chiamiamo DBPedia
							HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringCalling);
							List<String> dbproperty = new ArrayList<String>();
							List<String> dbproperty2 = new ArrayList<String>();
							dbproperty.add("birthDate");
							dbproperty.add("title");
							dbproperty.add("name");
							dbproperty2.add("placeOfBirth");
							callDBPedia(dbproperty);
							callDBPedia(dbproperty2);
							
							//chiamiamo Ekp
							HtmlEkpService.setHTML(HTMLekpServiceStringCalling);
							List<String> titletagme = tagmeResp.getTitleTag();
							Iterator<String> itertitle =  titletagme.iterator();
							while (itertitle.hasNext()){
							callEkp(itertitle.next());
							}
						}
						
						
					}
				});
	}
	
	private void callDBPedia(List<String> dbprop){
		Debug.printDbgLine("Sottotestogwt.java: callDBPedia()");		
		
		dbpediaService.sendToServer(tagmeResp, dbprop, new AsyncCallback<DBPediaResponse>() {
		public void onFailure(Throwable caught) {
			//set the error
			dbpediaResp = new DBPediaResponse();
			dbpediaResp.setCode(-1);
			dbpediaResp.setQueryResultXML("Error Callig service Module:"+
							   "<br>Cause: "+caught.getCause()+
							   "<br><br>Message: "+caught.getMessage()+
							   "<br><br>StackTrace: "+caught.getStackTrace().toString());
			
			HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringFAIL); //show the fail
			dbpediaShowDataBTN.setVisible(true); //allow to see what gone wrong
		}

		public void onSuccess(DBPediaResponse result) {	
			Debug.printDbgLine("Sottotestogwt.java: DBPedia result="+result.getQueryResultXML());
			
			dbpediaResp = result;
			
			HtmlDBPediaService.setHTML(HTMLdbpediaServiceStringOK);
			dbpediaShowDataBTN.setVisible(true);
		}});
		
	}
	
	private void callEkp(String input){
		Debug.printDbgLine("Sottotestogwt.java: callEkp()");
		Debug.printDbgLine("Sottotestogwt.java: Ekp input="+input);

		ekpService.sendToServer(input, new AsyncCallback<EkpResponse>() {
		public void onFailure(Throwable caught) {
			//set the error
			ekpResp = new EkpResponse();
			ekpResp.setCode(-1);
			ekpResp.setError("Error Callig service Module:"+
							   "<br>Cause: "+caught.getCause()+
							   "<br><br>Message: "+caught.getMessage()+
							   "<br><br>StackTrace: "+caught.getStackTrace().toString());
			
			HtmlEkpService.setHTML(HTMLekpServiceStringFAIL); //show the fail
			ekpShowDataBTN.setVisible(true); //allow to see what gone wrong			
		}

		public void onSuccess(EkpResponse result) {			
			Debug.printDbgLine("Sottotestogwt.java: Ekp output="+result.getMessage());
			
			ekpResp = result;
			
			HtmlEkpService.setHTML(HTMLekpServiceStringOK); //show the fail
			ekpShowDataBTN.setVisible(true); //allow to see what gone wrong		
		}});
		
		
	}
	
	private void InitServiceStatusPanel(){
		Debug.printDbgLine("Sottotestogwt.java: initServiceStatusPanel()");
		
		//main panel
		serviceStatusPanel = new ContentPanel(GWT.<ContentPanelAppearance> create(FramedPanelAppearance.class));
		serviceStatusPanel.setHeadingText("Service Status");
		serviceStatusPanel.setPixelSize(RootPanel.get().getOffsetWidth()-(RootPanel.get().getOffsetWidth()*10/100), 90);
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
		
		serviceStatusPanelHC.add(tagmeStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(dbpediaStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.add(ekpStatusVP, new HorizontalLayoutData(0.33, 1, new Margins(4)));
		serviceStatusPanelHC.setBorders(true);
		serviceStatusPanel.setWidget(serviceStatusPanelHC);		
		
		RootPanel.get("serviceStatusPanel").add(serviceStatusPanel);
		
	}
	
	
}
