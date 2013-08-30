package com.sottotesto.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.DBPQueryResp;

@RemoteServiceRelativePath("dbpq")
public interface DBPediaQuery extends RemoteService {
	DBPQueryResp sendToServer(DBPQueryResp resp) throws IllegalArgumentException;
}