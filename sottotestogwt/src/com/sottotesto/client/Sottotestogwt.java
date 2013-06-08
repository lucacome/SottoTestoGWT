package com.sottotesto.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.Debug;
import com.sottotesto.shared.FieldVerifier;
import com.sottotesto.shared.TagmeResponse;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Sottotestogwt implements EntryPoint {
	
	 // The message displayed to the user when the server cannot be reached or returns an error.
	private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";
	
	// Create remote service proxyes to talk to the server-side services
	private final TagmeServiceAsync tagmeService = GWT.create(TagmeService.class);	
	private final DBPediaServiceAsync dbpediaService = GWT.create(DBPediaService.class);
	private final EkpServiceAsync ekpService = GWT.create(EkpService.class);
	
	private String textAreaDefText; //default text written on textarea
	private String textSendButton;  //default tet written on send button
	private Button sendButton;     //button to call tagme
	private TextArea textArea;     //textarea for user input
	private Label errorLabel;      //errorlabel for textarea misuse
	private Label serverResponseLabel;
	
	private TagmeResponse tagmeResp; //response of TAGME service
	
	//This is the entry point method.
	public void onModuleLoad() {
		Debug.printDbgLine("**********************************************************");
		Debug.printDbgLine("NEW RUN!");
		Debug.printDbgLine("**********************************************************");
		Debug.printDbgLine("Sottotestogwt.java: onModuleLoad()");
		
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
		Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): creating handlers...");
		HomeInputHandler hihandler = new HomeInputHandler();
		sendButton.addClickHandler(hihandler);
		textArea.addKeyUpHandler(hihandler);
	}
	
	
	
	//initialize items and load them on page
	private void initItems(){
		Debug.printDbgLine("Sottotestogwt.java: initItems()");
		
		tagmeResp = new TagmeResponse();
		
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
		sendButton.setEnabled(false);
		serverResponseLabel.setText("");
		
		tagmeService.sendToServer(textToServer, new AsyncCallback<TagmeResponse>() {
					public void onFailure(Throwable caught) {
						Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onFailure()");
						// Show the RPC error message to the user
						serverResponseLabel.addStyleName("serverResponseLabelError");
						serverResponseLabel.setText("Remote Procedure Call - Failure");
					}

					public void onSuccess(TagmeResponse result) {
						Debug.printDbgLine("Sottotestogwt.java: callTagme(): tagmeService:onSuccess()");
						tagmeResp = result;				
						
						if (!(tagmeResp.getCode()==200)){
							//Tagme ha avuto qualche problema!
							String errMess = "TAGME FAILED\n";
							errMess += "Code: "+String.valueOf(tagmeResp.getCode())+"\n";
							errMess += "Message: "+tagmeResp.getMessage()+"\n";
							errMess += "Error: "+tagmeResp.getError();
							serverResponseLabel.setText(errMess);							
						}
						else {
							serverResponseLabel.setText(String.valueOf(tagmeResp.getCode())+": "+tagmeResp.getJson());
							sendButton.setEnabled(true);
							
							//chiamiamo DBPedia
							callDBPedia();
							
							//chiamiamo Ekp
							//callEkp();
						}
						
						
					}
				});
	}
	
	private void callDBPedia(){
		Debug.printDbgLine("Sottotestogwt.java: callDBPedia()");		
		
		dbpediaService.sendToServer(tagmeResp.getJsonData(), new AsyncCallback<DBPediaResponse>() {
		public void onFailure(Throwable caught) {
			// Show the RPC error message to the user
			serverResponseLabel.setText("Error calling DBPedia Service");			
		}

		public void onSuccess(DBPediaResponse result) {			
			String temp = serverResponseLabel.getText();
			serverResponseLabel.setText(temp + new HTML(result.getQueryResult()) );			
		}});
		
	}
	
	private void callEkp(){
		Debug.printDbgLine("Sottotestogwt.java: callEkp()");
				
		/*
		dbpediaService.sendToServer(tagmeResp.getJson(), new AsyncCallback<DBPediaResponse>() {
		public void onFailure(Throwable caught) {
			// Show the RPC error message to the user
			serverResponseLabel.setText("Error calling DBPedia Service");			
		}

		public void onSuccess(DBPediaResponse result) {			
			String temp = serverResponseLabel.getText();
			serverResponseLabel.setText(temp + new HTML(result.getQueryResult()) );			
		}});
		*/ 
		
	}
}




//FOR FUTURE REFERENCE:
/*
// Create the popup dialog box
final DialogBox dialogBox = new DialogBox();
dialogBox.setText("Remote Procedure Call");
dialogBox.setAnimationEnabled(true);
final Button closeButton = new Button("Close");
// We can set the id of a widget by accessing its Element
closeButton.getElement().setId("closeButton");

VerticalPanel dialogVPanel = new VerticalPanel();
dialogVPanel.addStyleName("dialogVPanel");
dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//dialogVPanel.add(textToServerLabel);
dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//dialogVPanel.add(serverResponseLabel);
dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
dialogVPanel.add(closeButton);
dialogBox.setWidget(dialogVPanel);



// Add a handler to close the DialogBox
closeButton.addClickHandler(new ClickHandler() {
	public void onClick(ClickEvent event) {
		Debug.printDbgLine("Sottotestogwt.java: onModuleLoad(): closeButtonOnClick()");
		dialogBox.hide();
		sendButton.setEnabled(true);
		sendButton.setFocus(true);
	}
});*/