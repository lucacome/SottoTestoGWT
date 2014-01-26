package com.sottotesto.shared;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class Global {
	
	static private int optionsDBwidth=240;
	static private int optionsDBheight=275;
	
	private static Dialog optionsDialog;
	static VerticalLayoutContainer optionsVC = new VerticalLayoutContainer();
	
	private static TextField tagmeKeyTF = new TextField();
	private static String tagmeKey="plclcd321";
	
	final static Slider rohSlider = new Slider();
	private static double roh = 0.06;
	
	final static Slider markerDelaySlider = new Slider();
	private static int mapMarkerDelay = 50;
	
	public static void init(){
		initOptionsDB();
	}
	
	private static void initOptionsDB(){
		
		Label tagmeKeyLabel = new Label("Tagme Key");
		String tagmeKeyTitle = "TAGME free key used to contact service";
		tagmeKeyLabel.setTitle(tagmeKeyTitle);
		tagmeKeyTF.setAllowBlank(false);
		tagmeKeyTF.setText(tagmeKey);
		tagmeKeyTF.setTitle("default value: plclcd321");
		
		Label rohLabel = new Label("Set TAGME roh value");
		String rohTitle = "LOWER = more tags; HIGHER = fewer tags";
		rohLabel.setTitle(rohTitle);
		rohSlider.setMinValue(1);
		rohSlider.setMaxValue(20);
		rohSlider.setValue((int)roh*100);
		rohSlider.setTitle("default value: 6 / 100");
		rohSlider.setIncrement(1);
		rohSlider.setMessage("{0} / 100");

		Label markerDelayLabel = new Label("Map Markers drop delay");
		String markerDelayTitle = "Milliseconds between marker drops on map";
		markerDelayLabel.setTitle(markerDelayTitle);
		markerDelaySlider.setMinValue(0);
		markerDelaySlider.setMaxValue(200);
		markerDelaySlider.setValue(mapMarkerDelay);
		markerDelaySlider.setTitle("default value: 50ms");
		markerDelaySlider.setIncrement(1);
		markerDelaySlider.setMessage("{0}ms");
		
		optionsVC.add(new HTML("<br>"));
		optionsVC.add(tagmeKeyLabel);
		optionsVC.add(tagmeKeyTF);
		optionsVC.add(new HTML("<br>"));
		optionsVC.add(rohLabel);
		optionsVC.add(rohSlider);
		optionsVC.add(new HTML("<br>"));
		optionsVC.add(markerDelayLabel);
		optionsVC.add(markerDelaySlider);
		optionsVC.add(new HTML("<br>"));
		optionsVC.add(new HTML("<br>"));
		
		
		FlowLayoutContainer optionsFC = new FlowLayoutContainer();
		optionsFC.setScrollMode(ScrollMode.AUTO);
		optionsFC.add(optionsVC);
		
		optionsDialog = new Dialog();
		optionsDialog.setHeadingText("GLOBAL OPTIONS");
		optionsDialog.setId("optionsDB");
		optionsDialog.setHideOnButtonClick(false);
		optionsDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		optionsDialog.setWidget(optionsFC);
		
		optionsDialog.getButtonById(PredefinedButton.OK.name()).addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				if(tagmeKeyTF.getText().isEmpty())return;
				else tagmeKey=tagmeKeyTF.getText();
				roh = (double) rohSlider.getValue()/100.0;
				mapMarkerDelay = markerDelaySlider.getValue();
				
				optionsDialog.hide();
			}
		});
		optionsDialog.getButtonById(PredefinedButton.CANCEL.name()).addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {				
				optionsDialog.hide();
			}
		});
	}
	
	public static void showOptionsDialog(){
		reloadOptions();
		
		optionsDialog.setWidth(optionsDBwidth);
		optionsDialog.setHeight(optionsDBheight);
		optionsDialog.show();
		optionsDialog.center();
	}
	
	private static int reloadOptions(){	
		tagmeKeyTF.setText(tagmeKey);
		tagmeKeyTF.clearInvalid();
		rohSlider.setValue((int)(roh*100.0));
		markerDelaySlider.setValue(mapMarkerDelay);
		
		return 0;
	}

	public static String getTagmeKey(){return tagmeKey;}
	public static double getRoh(){return roh;}
	public static int getMapMarkerDelay(){return mapMarkerDelay;}
}
