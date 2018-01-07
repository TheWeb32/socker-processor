package com.skybet.test.processor;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.GsonBuilder;
import com.skybet.test.model.Modalities;
import com.skybet.test.services.impl.AdvancedSaveMessageServiceImpl;
import com.skybet.test.services.impl.BasicSaveMessageServiceImpl;
import com.skybet.test.services.impl.IntermediateSaveMessageServiceImpl;

public class Boot {
	
	private static Logger log = Logger.getLogger(Boot.class.getCanonicalName());

	public static void main(String[] args) throws UnknownHostException, IOException {
		if(args.length >= 3) {
			Config.PROVIDER_HOST = args[0];
			Config.PROVIDER_DATA_PORT = Integer.parseInt(args[1]);
			Config.PROCESSOR_MODE = args[2];
			if(Modalities.INTERMEDIATE.equals(Config.PROCESSOR_MODE)) {
				Config.PROVIDER_DB_PORT = Integer.parseInt(args[3]);
			}
			if(Modalities.ADVANCED.equals(Config.PROCESSOR_MODE)) {
				Config.PROVIDER_RABBITMQ_PORT = Integer.parseInt(args[3]);
			}
		}else {
			log.log(Level.SEVERE, "No arguments passed. Insert provider host with port and choose a mode betweens: basic, intermediate, advanced, additional");
			return;
		}
		log.log(Level.INFO, "Processor started with arguments: " + Arrays.asList(args));
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
			processor.connect(Config.PROVIDER_HOST, Config.PROVIDER_DATA_PORT);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				log.log(Level.SEVERE, "Thread waiting error");
			}
			log.log(Level.INFO, "Provider Reconnection attempt");
		}
	}

}
