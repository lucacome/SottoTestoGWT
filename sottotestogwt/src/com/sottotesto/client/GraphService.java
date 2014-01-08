package com.sottotesto.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("graphservice")
public interface GraphService extends RemoteService {
	String sendToServer(List<String> fdfinal) throws IllegalArgumentException;
}