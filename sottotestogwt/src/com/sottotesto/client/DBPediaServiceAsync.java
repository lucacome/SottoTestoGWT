package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.TagmeData;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface DBPediaServiceAsync {
	void sendToServer(TagmeData data, AsyncCallback<DBPediaResponse> callback)
			throws IllegalArgumentException;
}
