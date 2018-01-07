package com.skybet.test.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.skybet.test.beans.Event;
import com.skybet.test.beans.Market;
import com.skybet.test.beans.Outcome;
import com.skybet.test.errors.ProcessorException;
import com.skybet.test.model.Types;
import com.skybet.test.services.SaveMessageService;

public class Processor {
	
	private static Logger log = Logger.getLogger(Processor.class.getCanonicalName());
	
	private SaveMessageService saveMessageService;
	private BufferedReader reader;

	private Socket rawClient;
	
	public SaveMessageService getSaveMessageService() {
		return saveMessageService;
	}

	public void setSaveMessageService(SaveMessageService saveMessageService) {
		this.saveMessageService = saveMessageService;
	}

	public BufferedReader getReader() {
		return reader;
	}

	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}

	public Socket getRawClient() {
		return rawClient;
	}

	public void setRawClient(Socket rawClient) {
		this.rawClient = rawClient;
	}

	public void connect(String host, Integer port) throws UnknownHostException, IOException {
		rawClient = null;
		try {
			rawClient = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(rawClient.getInputStream()));
			execute();
		}catch (Exception e) {
			log.log(Level.SEVERE, "Processor reader exception", e);
		}finally{
			if(rawClient != null)
				rawClient.close();
		}
	}
	
	public void execute() throws UnknownHostException, IOException {
		try {
			while(rawClient.isConnected()) {
				String line = reader.readLine();
				if(line == null)
					throw new ProcessorException("Seems that someone it's already connected to the provider: " + rawClient.getRemoteSocketAddress() + ":" + rawClient.getPort());
				StringTokenizer token = new StringTokenizer(line.replaceAll("(\\\\[|])+", "\""), "|");
				if(token.countTokens() >= 4) {
					Integer messageId = Integer.valueOf(token.nextToken());
					String operation = token.nextToken();
					String type = token.nextToken();
					Date timestamp = new Date(Long.valueOf(token.nextToken()));
					switch (type) {
					case Types.EVENT:
						try {
							if(token.countTokens() >= 7) {
								String eventId = token.nextToken();
								String category = token.nextToken();
								String subCategory = token.nextToken();
								String name = token.nextToken();
								Date startTime = new Date(Long.valueOf(token.nextToken()));
								Boolean displayed = Boolean.valueOf(token.nextToken());
								Boolean suspended = Boolean.valueOf(token.nextToken());
								saveMessageService.save(new Event(messageId, operation, type, timestamp, eventId, category, subCategory, name, startTime, displayed, suspended));
							}else{
								log.log(Level.SEVERE, "Invalid fields number: " + line);
							}
						}catch (Exception e) {
							log.log(Level.SEVERE, "Invalid fields format: " + line);
						}
						break;
					case Types.MARKET:
						try {
							if(token.countTokens() >= 5) {
								String eventId = token.nextToken();
								String marketId = token.nextToken();
								String name = token.nextToken();
								Boolean displayed = Boolean.valueOf(token.nextToken());
								Boolean suspended = Boolean.valueOf(token.nextToken());
								saveMessageService.save(new Market(messageId, operation, type, timestamp, eventId, name, marketId, displayed, suspended));
							}else{
								log.log(Level.SEVERE, "Invalid fields number: " + line);
							}
						}catch (Exception e) {
							log.log(Level.SEVERE, "Invalid fields format: " + line);
						}
						break;
					case Types.OUTCOME:
						try {
							if(token.countTokens() >= 6) {
								String marketId = token.nextToken();
								String outcomeId = token.nextToken();
								String name = token.nextToken();
								String price = token.nextToken();
								Boolean displayed = Boolean.valueOf(token.nextToken());
								Boolean suspended = Boolean.valueOf(token.nextToken());
								saveMessageService.save(new Outcome(messageId, operation, type, timestamp, marketId, outcomeId, name, price, displayed, suspended));
							}else{
								log.log(Level.SEVERE, "Invalid fields number: " + line);
							}
						}catch (Exception e) {
							log.log(Level.SEVERE, "Invalid fields format: " + line, e);
						}
						break;
					default:
						log.log(Level.SEVERE, "Found invalid type: " + type);
						break;
					}
				}else{
					log.log(Level.SEVERE, "Found invalid token type: " + line);
				}
			}
		}catch (ProcessorException e) {
			throw e;
		}catch (Exception e) {
			log.log(Level.SEVERE, "Processor reader exception", e);
		}finally{
			if(rawClient != null)
				rawClient.close();
		}
	}

}
