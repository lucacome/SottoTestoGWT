package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.DBPQueryResp;
import com.sottotesto.shared.EkpResponse;

@RemoteServiceRelativePath("listservice")
public interface ListService extends RemoteService {
	List<DBPQueryResp> sendToServer(EkpResponse resp) throws IllegalArgumentException;
}