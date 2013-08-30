package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPQueryResp;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaQueryAsync {
	void sendToServer(DBPQueryResp resp, AsyncCallback<DBPQueryResp> callback)
			throws IllegalArgumentException;
	
}