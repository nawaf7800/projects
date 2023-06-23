package com.email;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;


@WebService(serviceName = "EmailService")
public class EmailService implements EMessenger {
	
	List<Message> messages = new ArrayList<Message>();

	@Override
	@WebMethod(operationName = "post")
	public void postMessage(@WebParam(name = "message") Message message) {
		
		messages.add(message);
	}

	@Override
	@WebMethod(operationName = "listMessages")
	public List listMessages(@WebParam(name = "topic") String topic) {
		List<String> mes = new ArrayList<String>();
		
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).topic.equals(topic))
				mes.add(messages.get(i).id);
		return mes;
	}

	@Override
	@WebMethod(operationName = "listMessagesWithTimestamps")
	public Map listMessagesWithTimestamps(@WebParam(name = "topic") String topic) {
		Map map = new HashMap(); 
		
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).topic.equals(topic))
				map.put(messages.get(i).id, messages.get(i).timestamp);
		return map;
	}

	@Override
	@WebMethod(operationName = "retrieveMessage")
	public Message retrieveMessage(@WebParam(name = "id") String id) {
		
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).id.equals(id))
				return messages.get(i);
		
		return null;
	}

	@Override
	@WebMethod(operationName = "listTopics")
	public List listTopics() {
		
		List<String> topics = new ArrayList<String>();
		for(int i = 0; i < messages.size(); i++)
			topics.add(messages.get(i).topic);
		return topics;
	}

	@Override
	@WebMethod(operationName = "subscribe")
	public boolean subscribe(@WebParam(name = "username") String username, @WebParam(name = "topic") String topic) {
		
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).topic.equals(topic)) {
				messages.get(i).subscribers.add(username);
				return true;
			}
		return false;
	}

	@Override
	@WebMethod(operationName = "unsubscribe")
	public boolean unsubscribe(@WebParam(name = "username") String username, @WebParam(name = "topic") String topic) {
		
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).topic.equals(topic)) {
				messages.get(i).subscribers.remove(username);
				return true;
			}
		return false;
	}

	@Override
	@WebMethod(operationName = "listSubscribers")
	public List listSubscribers(@WebParam(name = "topic") String topic) {
		for(int i = 0; i < messages.size(); i++)
			if(messages.get(i).topic.equals(topic))
				return messages.get(i).subscribers;
		return null;
	}
}
