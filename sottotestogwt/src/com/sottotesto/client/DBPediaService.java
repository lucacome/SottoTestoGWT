package com.sottotesto.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.DBPediaResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("dbpedia")
public interface DBPediaService extends RemoteService {
	DBPediaResponse sendToServer(String name) throws IllegalArgumentException;
}
