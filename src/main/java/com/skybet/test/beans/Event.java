package com.skybet.test.beans;

import java.util.Date;

public class Event extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108744703917766631L;

	private String eventId;
	private String category;
	private String subCategory;
	private String name;
	private Date startTime;
	private Boolean displayed;
	private Boolean suspended;

	public Event() {
		super();
	}
	public Event(Integer messageId, String operation, String type, Date timestamp, String eventId, String category, String subCategory, String name, Date startTime, Boolean displayed,
			Boolean suspended) {
		super(messageId, operation, type, timestamp);
		this.eventId = eventId;
		this.category = category;
		this.subCategory = subCategory;
		this.name = name;
		this.startTime = startTime;
		this.displayed = displayed;
		this.suspended = suspended;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSubCategory() {
		return subCategory;
	}
	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
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
		return "Event [eventId=" + eventId + ", category=" + category + ", subCategory=" + subCategory + ", name="
				+ name + ", startTime=" + startTime + ", displayed=" + displayed + ", suspended=" + suspended
				+ ", getMsgId()=" + getMsgId() + ", getOperation()=" + getOperation() + ", getType()=" + getType()
				+ ", getTimestamp()=" + getTimestamp() + "]";
	}
	
}
