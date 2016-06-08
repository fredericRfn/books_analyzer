package com.books_analyzer_ws.service;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class RabbitInterface {
	private final static String QUEUE_NAME = "process";
	
	public static void requestAnalysis(int id) {
		 ConnectionFactory factory = new ConnectionFactory();
		 factory.setHost("localhost");
		 Connection connection = factory.newConnection();
		 Channel channel = connection.createChannel();
		 
		 channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		 String message = Integer.valueOf(id).toString();
		 channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
		 System.out.println(" [x] Sent '" + message + "'");
		 channel.close();
		 connection.close();
	}

}
