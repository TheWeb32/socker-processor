package com.skybet.test.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.skybet.test.beans.Message;
import com.skybet.test.services.SaveMessageService;

public class BasicSaveMessageServiceImpl implements SaveMessageService {
	
	private static Logger log = Logger.getLogger(BasicSaveMessageServiceImpl.class.getCanonicalName());
	
	private Gson gson;
	
	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public BasicSaveMessageServiceImpl() {
		super();
	}

	public BasicSaveMessageServiceImpl(Gson gson) {
		super();
		this.gson = gson;
	}

	@Override
	public void save(Message message) {
		log.log(Level.INFO, gson.toJson(message));
	}
	
}