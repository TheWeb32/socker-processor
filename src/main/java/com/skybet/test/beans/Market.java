package com.skybet.test.beans;

import java.util.Date;

public class Market extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108744703917766631L;
	private String eventId;
	private String marketId;
	private String name;
	private Boolean displayed;
	private Boolean suspended;
	
	public Market() {
		super();
	}
	public Market(Integer messageId, String operation, String type, Date timestamp, String eventId, String name, String marketId, Boolean displayed, Boolean suspended) {
		super(messageId, operation, type, timestamp);
		this.eventId = eventId;
		this.name = name;
		this.marketId = marketId;
		this.displayed = displayed;
		this.suspended = suspended;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public Boolean getDisplayed() {
		return displayed;
	}
	public void setDisplayed(Boolean displayed) {
		this.displayed = displayed;
	}
	public Boolean getSuspended() {
		return suspended;
	}
	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}
	@Override
	public String toString() {
		return "Market [eventId=" + eventId + ", marketId=" + marketId + ", name=" + name + ", displayed=" + displayed
				+ ", suspended=" + suspended + ", getMsgId()=" + getMsgId() + ", getOperation()=" + getOperation()
				+ ", getType()=" + getType() + ", getTimestamp()=" + getTimestamp() + "]";
	}
}
