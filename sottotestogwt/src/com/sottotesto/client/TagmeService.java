package com.sottotesto.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sottotesto.shared.TagmeResponse;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tagme")
public interface TagmeService extends RemoteService {
	TagmeResponse sendToServer(String name) throws IllegalArgumentException;
}