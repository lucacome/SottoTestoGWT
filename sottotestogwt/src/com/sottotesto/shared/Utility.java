package com.sottotesto.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Utility {

	public static long calcTimeTookMs(long StartTimeMs){
		return (System.currentTimeMillis()-StartTimeMs);
	}
	
	public static void showTagmeDataDB(TagmeResponse tagmeResp){
		Debug.printDbgLine("Utility.java: showTagmeDataDB()");
		
		final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
		dialogBox.setText("Tagme Data");
		dialogBox.setAnimationEnabled(true);
		dialogBox.getElement().setId("tagmeDataDB");
		final Button closeButton = new Button("Close");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Response time:</b> "+String.valueOf(tagmeResp.getTime())+"ms"));
		dialogVPanel.add(new HTML("<br><b>Code:</b> "+String.valueOf(tagmeResp.getCode())));
		dialogVPanel.add(new HTML("<br><b>Message:</b> "+tagmeResp.getMessage()));
		dialogVPanel.add(new HTML("<br><b>ContentType:</b> "+tagmeResp.getContentType()));
		
		if (tagmeResp.getCode() != 200)
			dialogVPanel.add(new HTML("<br><b>Error:</b> "+tagmeResp.getError()));
		else{
			dialogVPanel.add(new HTML("<br><b>Number of Json Resource:</b> "+tagmeResp.getResNum()));
			dialogVPanel.add(new HTML("<br><b>Json data:</b><br> "+tagmeResp.getJson().replaceAll(",",",<br>")));
		}
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		dialogBox.center();
		
		dialogBox.show();
		
		// Create a handler for the sendButton and nameField
		class DBInputHandler implements ClickHandler, KeyUpHandler {
					public void onClick(ClickEvent event) {
						dialogBox.hide();
						dialogBox.removeFromParent();
					}
					public void onKeyUp(KeyUpEvent event) {				
						if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER ||
							event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
							dialogBox.hide();
							dialogBox.removeFromParent();					
						}
					}			
				}
		
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Debug.printDbgLine("Utility.java: closeTagmeData()");
				dialogBox.hide();
				dialogBox.removeFromParent();
			}
		});
		
	}
	
	public static void showDBPediaDataDB(DBPediaResponse dbpediaResp){
		Debug.printDbgLine("Utility.java: showDBPediaDataDB()");
		
		final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
		dialogBox.setText("DBPedia Data");
		dialogBox.setAnimationEnabled(true);
		dialogBox.getElement().setId("dbPediaDataDB");
		final Button closeButton = new Button("Close");

		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<b>Response time:</b> "+String.valueOf(dbpediaResp.getTime())+"ms"));			
		dialogVPanel.add(new HTML("<br><b>Result Query:</b><br> "+dbpediaResp.getQueryResultXML()));		
		
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);
		dialogBox.center();
		
		dialogBox.show();
		
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Debug.printDbgLine("Utility.java: closeDBPediaData()");
				dialogBox.hide();
				dialogBox.removeFromParent();
			}
		});
		
	}
	
	
	private static class ExtendedDialogBox extends DialogBox {

	    @Override
	    protected void onPreviewNativeEvent(NativePreviewEvent event) {
	        super.onPreviewNativeEvent(event);
	        switch (event.getTypeInt()) {
	            case Event.ONKEYDOWN:
	                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE ||
	                	event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
	                    hide();
	                    removeFromParent();
	                }
	                break;
	        }
	    }
	}
	
}
