package com.skybet.test.processor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.skybet.test.errors.ConfigException;
import com.skybet.test.model.Modalities;
import com.skybet.test.services.impl.AdvancedSaveMessageServiceImpl;
import com.skybet.test.services.impl.BasicSaveMessageServiceImpl;
import com.skybet.test.services.impl.IntermediateSaveMessageServiceImpl;

public class Boot {
	
	private static Logger log = Logger.getLogger(Boot.class.getCanonicalName());

	public static void main(String[] args) throws UnknownHostException, IOException {
    	try {
			Config.PROCESSOR_MODE = System.getenv("PROCESSOR_MODE");
        	log.log(Level.INFO, "Setted as Processor Mode: " + Config.PROCESSOR_MODE);
			Config.PROVIDER_HOST = System.getenv("PROVIDER_HOST");
			Config.PROVIDER_PORT = Integer.parseInt(System.getenv("PROVIDER_PORT"));
        	log.log(Level.INFO, "Setted as Provider address: " + Config.PROVIDER_HOST + ":" + Config.PROVIDER_PORT);
			Config.RABBITMQ_HOST = System.getenv("RABBITMQ_DEFAULT_HOST");
			Config.RABBITMQ_PORT = Integer.parseInt(System.getenv("RABBITMQ_DEFAULT_PORT"));
			Config.RABBITMQ_DEFAULT_USER = System.getenv("RABBITMQ_DEFAULT_USER");
			Config.RABBITMQ_DEFAULT_PASS = System.getenv("RABBITMQ_DEFAULT_PASS");
			Config.RABBITMQ_DEFAULT_VHOST = System.getenv("RABBITMQ_DEFAULT_VHOST");
        	log.log(Level.INFO, "Setted as RabbitMQ address: " + Config.RABBITMQ_HOST + ":" + Config.RABBITMQ_PORT);
			Config.MONGODB_HOST = System.getenv("MONGODB_HOST");
			Config.MONGODB_PORT = Integer.parseInt(System.getenv("MONGODB_PORT"));
			Config.MONGODB_USER = System.getenv("MONGODB_USER");
			Config.MONGODB_PASS = System.getenv("MONGODB_PASS");
			Config.MONGODB_DB = System.getenv("MONGODB_DB");
        	log.log(Level.INFO, "Setted as MongoDB address: " + Config.MONGODB_HOST + ":" + Config.MONGODB_PORT);
    	}catch (Exception e) {
    		throw new ConfigException("Not all env vars present", e);
		}
		log.log(Level.INFO, "Processor started");
		Processor processor = new Processor();
	    GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		switch (Config.PROCESSOR_MODE) {
		case Modalities.BASIC:
			processor.setSaveMessageService(new BasicSaveMessageServiceImpl(gsonBuilder.create()));
			break;
		case Modalities.INTERMEDIATE:
			processor.setSaveMessageService(new IntermediateSaveMessageServiceImpl());
			break;
		case Modalities.ADVANCED:
			processor.setSaveMessageService(new AdvancedSaveMessageServiceImpl(gsonBuilder.create()));
			break;
		case Modalities.ADDITIONAL:
			
			break;
		default:
			log.log(Level.SEVERE, "Invalid mode: " + Config.PROCESSOR_MODE);
			return;
		}
		while(true) {
			processor.connect(Config.PROVIDER_HOST, Config.PROVIDER_PORT);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				log.log(Level.SEVERE, "Thread waiting error");
			}
			log.log(Level.INFO, "Provider Reconnection attempt");
		}
	}

}
