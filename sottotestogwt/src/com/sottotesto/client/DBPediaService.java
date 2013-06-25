package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.DBPediaResponse;
import com.sottotesto.shared.TagmeResponse;

/**
 * The client side stub for the RPC service.
 */

@RemoteServiceRelativePath("dbpedia")
public interface DBPediaService extends RemoteService {
	DBPediaResponse sendToServer(TagmeResponse tagmResp, List<String> dbprop) throws IllegalArgumentException;
}