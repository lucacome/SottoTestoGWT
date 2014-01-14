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

public class Global {
	
	static private int optionsDBwidth=220;
	static private int optionsDBheight=250;
	
	private static Dialog optionsDialog;
	static VerticalLayoutContainer optionsVC = new VerticalLayoutContainer();
	
	
	final static Slider rohSlider = new Slider();
	private static double roh = 0.06;
	
	final static Slider markerDelaySlider = new Slider();
	private static int mapMarkerDelay = 50;
	
	public static void init(){
		initOptionsDB();
	}
	
	private static void initOptionsDB(){
		
		Label rohLabel = new Label("Imposta il valore ROH");
		String rohTitle = "Piu' ROH e' basso e piu' TAGME trovera' entita' nelle frasi";
		rohLabel.setTitle(rohTitle);
		rohSlider.setMinValue(1);
		rohSlider.setMaxValue(20);
		rohSlider.setValue((int)roh*100);
		rohSlider.setTitle("default value: 6 / 100");
		rohSlider.setIncrement(1);
		rohSlider.setMessage("{0} / 100");

		Label markerDelayLabel = new Label("Imposta il dealy tra i Marker");
		String markerDelayTitle = "Intervallo di caduta tra i marker sulla mappa";
		markerDelayLabel.setTitle(markerDelayTitle);
		markerDelaySlider.setMinValue(0);
		markerDelaySlider.setMaxValue(100);
		markerDelaySlider.setValue(mapMarkerDelay);
		markerDelaySlider.setTitle("default value: 50ms");
		markerDelaySlider.setIncrement(1);
		markerDelaySlider.setMessage("{0}ms");
		
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
		optionsDialog.setHeadingText("OPZIONI GLOBALI");
		optionsDialog.setId("optionsDB");
		optionsDialog.setHideOnButtonClick(true);
		optionsDialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		optionsDialog.setWidget(optionsFC);
		
		optionsDialog.getButtonById(PredefinedButton.OK.name()).addSelectHandler(new SelectHandler() {			
			@Override
			public void onSelect(SelectEvent event) {
				// TODO Auto-generated method stub
				roh = (double) rohSlider.getValue()/100.0;
				mapMarkerDelay = markerDelaySlider.getValue();
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
	
	private static void reloadOptions(){
		rohSlider.setValue((int)(roh*100.0));
		markerDelaySlider.setValue(mapMarkerDelay);
	}

	public static double getRoh(){return roh;}
	public static int getMapMarkerDelay(){return mapMarkerDelay;}
}
