package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GHTServiceAsync {
	void sendToServer(String jsonHT, List<String> selectedLinks, AsyncCallback<String> callback)
			throws IllegalArgumentException;

}