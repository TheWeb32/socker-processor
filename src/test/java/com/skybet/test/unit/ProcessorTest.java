package com.skybet.test.unit;

import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.fail;

import com.skybet.test.processor.Processor;

public class ProcessorTest {

	private Processor processor;
	
	@Before
	public void init() {
        BufferedReader reader = mock(BufferedReader.class);
        Socket rawClient = mock(Socket.class);
	    try {
	    	when(rawClient.isConnected()).thenReturn(true);
			when(reader.readLine()).thenReturn("|2054|create|event|1497359166352|ee4d2439-e1c5-4cb7-98ad-9879b2fd84c2|Football|Sky Bet League Two|\\|Accrington\\| vs \\|Cambridge\\||1497359216693|0|1|");
		} catch (IOException e) {
			
		}
		processor = new Processor();
		processor.setRawClient(rawClient);
		processor.setReader(reader);
		processor.setSaveMessageService(new SaveMessageServiceImplTest(rawClient));
	}
	
    @Test
    public void testProcessor() {
    	try {
			processor.execute();
		} catch (IOException e) {
			fail("Error on closing");
		}
    }
	
}
