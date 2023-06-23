package com.email;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class Message {
	
	public String id;
	public long timestamp;
	public String sender;
	public List<String> subscribers;
	public String topic;
	public String content;
	public byte[] attachments;
}
