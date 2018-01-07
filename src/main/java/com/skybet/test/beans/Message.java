package com.skybet.test.beans;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5786700268474756086L;

	private Integer msgId;
	private String operation;
	private String type;
	private Date timestamp;
	
	public Message() {
		super();
	}
	public Message(Integer msgId, String operation, String type, Date timestamp) {
		super();
		this.msgId = msgId;
		this.operation = operation;
		this.type = type;
		this.timestamp = timestamp;
	}
	public Integer getMsgId() {
		return msgId;
	}
	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
