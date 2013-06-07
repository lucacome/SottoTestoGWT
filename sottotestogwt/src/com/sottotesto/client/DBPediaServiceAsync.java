package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPediaResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaServiceAsync {
	void sendToServer(String input, AsyncCallback<DBPediaResponse> callback)
			throws IllegalArgumentException;
}
