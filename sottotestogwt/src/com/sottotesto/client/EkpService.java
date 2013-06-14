package com.sottotesto.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.EkpResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ekp")
public interface EkpService extends RemoteService {
	EkpResponse sendToServer(String name, String data) throws IllegalArgumentException;
}