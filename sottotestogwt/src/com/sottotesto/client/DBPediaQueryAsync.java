package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.EkpResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaQueryAsync {
	void sendToServer(EkpResponse resp, AsyncCallback<List<DBPQueryResp>> callback)
			throws IllegalArgumentException;
	
}