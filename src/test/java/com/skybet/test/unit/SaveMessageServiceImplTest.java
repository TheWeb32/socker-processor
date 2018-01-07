package com.skybet.test.unit;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.Socket;
import java.util.Date;

import com.skybet.test.beans.Event;
import com.skybet.test.beans.Message;
import com.skybet.test.services.SaveMessageService;

public class SaveMessageServiceImplTest implements SaveMessageService {

	private Socket rawClient;

	public SaveMessageServiceImplTest(Socket rawClient) {
		this.rawClient = rawClient;
	}

	@Override
	public void save(Message message) {
		assertThat(message, instanceOf(Event.class));
		Event event = (Event) message;
		assertEquals(new Integer(2054), event.getMsgId());
		assertEquals("create", event.getOperation());
		assertEquals("event", event.getType());
		assertEquals(new Date(1497359166352L), event.getTimestamp());
		assertEquals("ee4d2439-e1c5-4cb7-98ad-9879b2fd84c2", event.getEventId());
		assertEquals("Football", event.getCategory());
    	when(rawClient.isConnected()).thenReturn(false);
	}
	
}