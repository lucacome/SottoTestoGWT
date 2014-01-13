package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPediaResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaServiceAsync {
	void sendToServer(String tagmResp, List<String> dbprop, String type, AsyncCallback<DBPediaResponse> callback)
			throws IllegalArgumentException;

}