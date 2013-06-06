package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GeneralServiceAsync {
	void sendToServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
