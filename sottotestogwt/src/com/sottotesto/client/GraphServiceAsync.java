package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GraphServiceAsync {
	void sendToServer(List<String> fdfinal, List<String> selectedDbpLinks, AsyncCallback<String> callback)
			throws IllegalArgumentException;

}