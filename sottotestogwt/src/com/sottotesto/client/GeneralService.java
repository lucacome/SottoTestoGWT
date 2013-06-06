package com.sottotesto.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tagme")
public interface GeneralService extends RemoteService {
	String sendToServer(String name) throws IllegalArgumentException;
}
