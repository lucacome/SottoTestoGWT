package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ghtservice")
public interface GHTService extends RemoteService {
	String sendToServer(String jsonHT, List<String> selectedLinks) throws IllegalArgumentException;
}