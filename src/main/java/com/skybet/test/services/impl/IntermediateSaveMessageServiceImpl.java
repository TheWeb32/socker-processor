package com.skybet.test.services.impl;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.skybet.test.beans.Event;
import com.skybet.test.beans.Market;
import com.skybet.test.beans.Message;
import com.skybet.test.beans.Outcome;
import com.skybet.test.errors.ProcessorException;
import com.skybet.test.processor.Config;
import com.skybet.test.services.SaveMessageService;

public class IntermediateSaveMessageServiceImpl implements SaveMessageService {

	private static Logger log = Logger.getLogger(IntermediateSaveMessageServiceImpl.class.getCanonicalName());
	private MongoClient mongoClient;

	public IntermediateSaveMessageServiceImpl() {
		super();
	}

	private MongoClient getMongoDBConnection() {
		MongoCredential credential = MongoCredential.createScramSha1Credential(Config.MONGODB_USER, Config.MONGODB_HOST, Config.MONGODB_PASS.toCharArray());
		MongoClientOptions.Builder optionsBuilder = new MongoClientOptions.Builder();
		optionsBuilder.maxWaitTime(Integer.MAX_VALUE);
		optionsBuilder.connectTimeout(Integer.MAX_VALUE);
		optionsBuilder.socketTimeout(Integer.MAX_VALUE);
		MongoClientOptions options = optionsBuilder.build();
		return new MongoClient(new ServerAddress(Config.MONGODB_HOST, Config.MONGODB_PORT), credential, options);
	}

	private Document message2Document(Message message) {
		Document document = new Document();
		if(message instanceof Event) {
			Event event = (Event) message;
			document.append("eventId", event.getEventId());
			document.append("category", event.getCategory());
			document.append("startTime", event.getStartTime());
			document.append("subCategory", event.getSubCategory());
			document.append("name", event.getName());
			document.append("suspended", event.getSuspended());
			document.append("displayed", event.getDisplayed());
		}else if(message instanceof Market) {
			Market market = (Market) message;
			document.append("marketId", market.getMarketId());
			document.append("name", market.getName());
			document.append("suspended", market.getSuspended());
			document.append("displayed", market.getDisplayed());
		}else if(message instanceof Outcome) {
			Outcome outcome = (Outcome) message;
			document.append("outcomeId", outcome.getOutcomeId());
			document.append("price", outcome.getPrice());
			document.append("name", outcome.getName());
			document.append("suspended", outcome.getSuspended());
			document.append("displayed", outcome.getDisplayed());
		}
		return document;
	}
	
	@Override
	public void save(Message message) {
		int i = 0;
		while(i < 10) {
			try {
				if(mongoClient == null)
					mongoClient = getMongoDBConnection();
				MongoDatabase db = mongoClient.getDatabase("skybet");
				if(message instanceof Event) {
					Event event = (Event) message;
					MongoCollection<Document> eventsCollection = db.getCollection(Event.class.getCanonicalName());
					if("update".equals(message.getOperation())) {
						Document document = message2Document(event);
						if(eventsCollection.count(new Document("eventId", event.getEventId())) > 0) {
							document.remove("eventId");
							eventsCollection.updateOne(new Document("eventId", event.getEventId()), new Document("$set", document));
						}else{
							eventsCollection.insertOne(document);
						}
					}else if("create".equals(message.getOperation())) {
						Document document = message2Document(event);
						eventsCollection.insertOne(document);
					}
				}else if(message instanceof Market) {
					Market market = (Market) message;
					MongoCollection<Document> marketsCollection = db.getCollection(Market.class.getCanonicalName());
					if("update".equals(message.getOperation())) {
						Document document = message2Document(market);
						if(marketsCollection.count(new Document("marketId", market.getMarketId())) > 0) {
							document.remove("marketId");
							marketsCollection.updateOne(new Document("marketId", market.getMarketId()), new Document("$set", document));
						}else{
							log.log(Level.SEVERE, "Impossible to update an Market (ID: " + market.getMarketId() + ") that doesn't exists");
						}
					}else if("create".equals(message.getOperation())) {
						Document document = message2Document(market);
						marketsCollection.insertOne(document);
						MongoCollection<Document> eventsCollection = db.getCollection(Event.class.getCanonicalName());
						eventsCollection.updateOne(new Document("eventId", market.getEventId()), new Document("$push", new Document("markets", market.getMarketId())));
					}
				}else if(message instanceof Outcome) {
					Outcome outcome = (Outcome) message;
					MongoCollection<Document> outcomesCollection = db.getCollection(Outcome.class.getCanonicalName());
					if("update".equals(message.getOperation())) {
						Document document = message2Document(outcome);
						if(outcomesCollection.count(new Document("outcomeId", outcome.getOutcomeId())) > 0) {
							document.remove("outcomeId");
							outcomesCollection.updateOne(new Document("outcomeId", outcome.getOutcomeId()), new Document("$set", document));
						}else{
							log.log(Level.SEVERE, "Impossible to update an Outcome (ID: " + outcome.getOutcomeId() + ") that doesn't exists");
						}
					}else if("create".equals(message.getOperation())) {
						Document document = message2Document(outcome);
						outcomesCollection.insertOne(document);
						MongoCollection<Document> marketsCollection = db.getCollection(Market.class.getCanonicalName());
						marketsCollection.updateOne(new Document("marketId", outcome.getMarketId()), new Document("$push", new Document("outcomes", outcome.getOutcomeId())));
					}
				}
				return;
			}catch (MongoException e) {
				if(mongoClient != null)
					mongoClient.close();
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					log.log(Level.SEVERE, "Thread waiting error");
				}
				log.log(Level.INFO, "MongoDB Reconnection attempt number " + i);
			}
			i++;
		}
		if(i > 10)
			throw new ProcessorException("MongoDB it's not available");
	}

}