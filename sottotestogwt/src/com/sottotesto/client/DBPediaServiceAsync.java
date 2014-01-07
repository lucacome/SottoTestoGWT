package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.TagmeResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaServiceAsync {
	void sendToServer(TagmeResponse tagmResp, List<String> dbprop, AsyncCallback<DBPediaResponse> callback)
			throws IllegalArgumentException;

}