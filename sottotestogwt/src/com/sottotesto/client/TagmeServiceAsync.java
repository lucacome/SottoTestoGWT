package com.sottotesto.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.TagmeResponse;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface TagmeServiceAsync {
	void sendToServer(String input, AsyncCallback<TagmeResponse> callback)
			throws IllegalArgumentException;

}