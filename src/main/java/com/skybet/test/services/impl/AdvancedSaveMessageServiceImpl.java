package com.skybet.test.services.impl;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.skybet.test.beans.Message;
import com.skybet.test.errors.ProcessorException;
import com.skybet.test.processor.Config;
import com.skybet.test.services.SaveMessageService;

public class AdvancedSaveMessageServiceImpl implements SaveMessageService {
	
	private static Logger log = Logger.getLogger(AdvancedSaveMessageServiceImpl.class.getCanonicalName());
	
	private Gson gson;

	private Channel channel;

	private Connection connection;
	
	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public AdvancedSaveMessageServiceImpl(Gson gson) {
		super();
		this.gson = gson;
	}

	private Connection getRabbitMQConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(Config.RABBITMQ_DEFAULT_USER);
        factory.setPassword(Config.RABBITMQ_DEFAULT_PASS);
        factory.setVirtualHost(Config.RABBITMQ_DEFAULT_VHOST);
        factory.setHost(Config.RABBITMQ_HOST);
        factory.setPort(Config.RABBITMQ_PORT);
        return factory.newConnection();
	}

	@Override
	public void save(Message message) {
		int i = 0;
		while(i < 10) {
			try {
				if(connection == null) {
					connection = getRabbitMQConnection();
			        channel = connection.createChannel();
				}
		        channel.exchangeDeclare(Config.EXCHANGEMQ_MESSAGES_NAME, "direct");
		        channel.basicPublish(Config.EXCHANGEMQ_MESSAGES_NAME, "", null, gson.toJson(message).getBytes());
		        return;
			}catch (Exception e) {
				log.log(Level.SEVERE, "RabbitMQ error on connection", e);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					log.log(Level.SEVERE, "Thread waiting error");
				}
				log.log(Level.INFO, "RabbitMQ Reconnection attempt number " + i);
			}finally {
				if(channel != null)
					try {
						channel.close();
					} catch (Exception e1) {
						log.log(Level.SEVERE, "Error on RabbitMQ Exchange closing", e1);
					}
				if(connection != null)
					try {
						connection.close();
						connection = null;
					} catch (IOException e2) {
						log.log(Level.SEVERE, "Error on RabbitMQ Exchange closing", e2);
					}
			}
			i++;
			if(i > 10)
				throw new ProcessorException("RabbitMQ it's not available");
		}
	}
}