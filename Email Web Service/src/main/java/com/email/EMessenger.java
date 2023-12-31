package com.email;

import java.util.List;
import java.util.Map;

public interface EMessenger {
	
	/***
	* Stores the message on the server
	* @param message Message to be stored
	*/
	void postMessage(Message message);
	
	/***
	* Lists identifiers of all messages stored on the server on specified topic
	* @param topic Topic of the messages
	* @return List of messages' identifiers
	*/
	List listMessages(String topic);
	
	/***
	* Lists identifiers and timestamps of all messages stored on the server on specified
	topic
	* @param topic Topic of the messages
	* @return Map of messages' identifiers and timestamps, null if there are no
	*/
	Map listMessagesWithTimestamps(String topic);
	
	/***
	* Retrieves a message with specified identifier
	* @param id Message identifier
	* @return Message
	*/
	Message retrieveMessage(String id);
	
	/***
	* Lists all topics present in the stored messages
	* @return List of unique topics
	*/
	List listTopics();
	
	/***
	* Subscribes the user to the topic
	* @param username Name of the user
	* @param topic Topic of the messages
	* @return True is the user was properly subscribed, False otherwise
	*/
	boolean subscribe(String username, String topic);
	
	/***
	* Unsubscribes the user from the topic
	* @param username Name of the user
	* @param topic Topic of the messages
	* @return True is the user was properly unsubscribed, False otherwise
	*/
	boolean unsubscribe(String username, String topic);
	
	/***
	* Lists usernames of users subcribed to a specified topic.
	* @param topic Topic of the messages
	* @return Names of users subscribed to the topic
	*/
	List listSubscribers(String topic);
}
