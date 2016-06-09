package com.books_analyzer_ws.service;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.Connection;

import java.io.IOException;

import com.rabbitmq.client.Channel;

public class RabbitInterface {
	private final static String QUEUE_NAME = "jobs_queue";
	
	public static boolean requestAnalysis(String id) {
		System.out.println("Inside RabbitMQ interface with id:" + id);
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("54.191.210.230");
		factory.setUsername("guest");
		factory.setPassword("guest");
		factory.setPort(15672);
		Connection connection;
		try {
			 connection = factory.newConnection();
			 Channel channel = connection.createChannel();	 
			 channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			 String message = id;
			 channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
			 System.out.println(" [x] Sent '" + message + "'");
			 channel.close();
			 connection.close();
			 return true;
		}
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
