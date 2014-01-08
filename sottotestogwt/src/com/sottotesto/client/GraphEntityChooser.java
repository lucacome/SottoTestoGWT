package com.sottotesto.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sottotesto.shared.Debug;

public class GraphEntityChooser {
	
	private static List<CheckBox> checkBoxes;
	
	public static void showDialog(List<String> entities, final ResultController rc){
		Debug.printDbgLine("Sottotestogwt.java: GraphEntityChooser()"); 
	    				
	    final ExtendedDialogBox dialogBox = new ExtendedDialogBox();
	    dialogBox.setText("Entity Chooser");
		dialogBox.setAnimationEnabled(false);
		dialogBox.getElement().setId("entityChooser");
		
		Label label = new Label();
		label.setText("Seleziona quali entita' analizzare per trovare dati in comune");

		VerticalPanel cbVerticalPanel = new VerticalPanel();
		cbVerticalPanel.addStyleName("dialogVPanel");		
		cbVerticalPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		cbVerticalPanel.add(label);
		
		//crea ed aggiungi i vari checkBox
		checkBoxes = new ArrayList<CheckBox>();
		for (String curEntity : entities){
			curEntity=curEntity.replace("_", " ");
	    	CheckBox cb = new CheckBox();
	    	cb.setValue(true);
	    	cb.setBoxLabel(curEntity);
	    	checkBoxes.add(cb);
	    	cbVerticalPanel.add(cb);
	    }					
		FlowLayoutContainer container = new FlowLayoutContainer();
		container.setScrollMode(ScrollMode.AUTO);		
		container.add(cbVerticalPanel);			
		
		
		Button abortButton = new Button("Annulla");		
		abortButton.addStyleName("dialogAbortButton");
		abortButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				dialogBox.removeFromParent();
			}
		});
		Button okButton = new Button("Confronta");		
		okButton.addStyleName("dialogOkButton");
		okButton.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				dialogBox.removeFromParent();
				
				rc.prepareToCallGraphService(getSelectedEntities());
			}
		});
		
		HorizontalPanel buttonHP = new HorizontalPanel();
		buttonHP.add(abortButton);
		buttonHP.add(okButton);
		
		cbVerticalPanel.add(buttonHP);
		
		dialogBox.setWidget(cbVerticalPanel);		
		dialogBox.center(); //must be done for updating data for resizing
		cbVerticalPanel.setWidth((RootPanel.get("body").getOffsetWidth()/3)+"px");
		if (cbVerticalPanel.getOffsetHeight()>(RootPanel.get("body").getOffsetHeight()*80/100))
			cbVerticalPanel.setHeight((RootPanel.get("body").getOffsetHeight()*80/100)+"px");
		
		dialogBox.show();
	}
	
	private static List<String> getSelectedEntities(){
		List<String> selectedEntities = new ArrayList<String>();
		
		String debugSelected="";
		
		for(CheckBox cb : checkBoxes){
			if (cb.getValue()){
				selectedEntities.add(cb.getBoxLabel().replace(" ", "_"));
				debugSelected+=cb.getBoxLabel()+" ";
			}
		}
				
		Debug.printDbgLine("GraphEntityChooser.java: getSelectedEntities(): "+debugSelected);
		
		return selectedEntities;
	}

	private static class ExtendedDialogBox extends DialogBox {

		private Node closeEventTarget = null;

		public ExtendedDialogBox() {
			// get the "dialogTopRight" class td
			Element dialogTopRight = getCellElement(0, 2);

			// close button image html
			dialogTopRight.setInnerHTML(
					"<div style=\"margin-left:-25px;margin-top: 2px; cursor:pointer;\">" + 
							"<img src=\"closeicon.gif\" height=\"20px\"/>" + 
					"</div>");

			// set the event target
			closeEventTarget = dialogTopRight.getChild(0).getChild(0);
		}

		@Override
		protected void onPreviewNativeEvent(NativePreviewEvent event) {
			super.onPreviewNativeEvent(event);

			NativeEvent nativeEvent = event.getNativeEvent();

			if (!event.isCanceled() 
					&& (event.getTypeInt() == Event.ONCLICK)
					&& isCloseEvent(nativeEvent))
			{
				this.hide();
				this.removeFromParent();
			}

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

		// see if the click target is the close button
		private boolean isCloseEvent(NativeEvent event) {
			return event.getEventTarget().equals(closeEventTarget); //compares equality of the underlying DOM elements
		} 
	}
	
}
