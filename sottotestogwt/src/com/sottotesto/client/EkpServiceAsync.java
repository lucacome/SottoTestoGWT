package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.EkpResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface EkpServiceAsync {
	void sendToServer(String input, String data, AsyncCallback<EkpResponse> callback)
			throws IllegalArgumentException;
}