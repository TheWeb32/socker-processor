package com.skybet.test.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bson.Document;

import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.skybet.test.beans.Event;
import com.skybet.test.beans.Market;
import com.skybet.test.beans.Outcome;
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
		initializeDB();
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
	
	private static void initializeDB() {
		MongoClient client = null;
		try {
			client = getMongoDBConnection();
			MongoDatabase db = client.getDatabase(Config.MONGODB_DB);
			MongoIterable<String> coll = db.listCollectionNames();
			for(String collName : coll) {
				if(Event.class.getCanonicalName().equals(collName))
					return;
				if(Market.class.getCanonicalName().equals(collName))
					return;
				if(Outcome.class.getCanonicalName().equals(collName))
					return;
			}
			InputStream inputStream = null;
			try {
				inputStream = Boot.class.getResourceAsStream("/com/skybet/test/configs/skybet.schema.js");
				String jsStr = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
				db.runCommand(new Document("$eval", jsStr));
			}finally {
				if(client != null)
					client.close();
			}
		}catch (Exception e) {
			throw new ConfigException("Impossible to initialize MongoDB", e);
		}finally {
			if(client != null)
				client.close();
		}
	}
	
	private static MongoClient getMongoDBConnection() {
		MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
		optionsBuilder.maxWaitTime(Integer.MAX_VALUE);
		optionsBuilder.connectTimeout(Integer.MAX_VALUE);
		optionsBuilder.socketTimeout(Integer.MAX_VALUE);
		return new MongoClient(new ServerAddress(Config.MONGODB_HOST, Config.MONGODB_PORT));
	}

}
