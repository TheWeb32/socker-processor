package com.skybet.test.beans;

import java.util.Date;

public class Outcome extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3108744703917766631L;
	private String marketId;
	private String outcomeId;
	private String name;
	private String price;
	private Boolean displayed;
	private Boolean suspended;
	
	public Outcome() {
		super();
	}
	public Outcome(Integer messageId, String operation, String type, Date timestamp, String marketId, String outcomeId, String name, String price, Boolean displayed, Boolean suspended) {
		super(messageId, operation, type, timestamp);
		this.marketId = marketId;
		this.outcomeId = outcomeId;
		this.name = name;
		this.price = price;
		this.displayed = displayed;
		this.suspended = suspended;
	}
	public String getMarketId() {
		return marketId;
	}
	public void setMarketId(String marketId) {
		this.marketId = marketId;
	}
	public String getOutcomeId() {
		return outcomeId;
	}
	public void setOutcomeId(String outcomeId) {
		this.outcomeId = outcomeId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
		return "Outcome [marketId=" + marketId + ", outcomeId=" + outcomeId + ", name=" + name + ", price=" + price
				+ ", displayed=" + displayed + ", suspended=" + suspended + ", getMsgId()=" + getMsgId()
				+ ", getOperation()=" + getOperation() + ", getType()=" + getType() + ", getTimestamp()="
				+ getTimestamp() + "]";
	}

}
