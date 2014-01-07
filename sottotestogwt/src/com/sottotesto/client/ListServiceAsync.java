package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.EkpResponse;

public interface ListServiceAsync {
	void sendToServer(EkpResponse resp, AsyncCallback<List<DBPQueryResp>> callback)
			throws IllegalArgumentException;

}