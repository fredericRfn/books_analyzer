package com.books_analyzer_ws.service;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;

import java.io.IOException;

import com.rabbitmq.client.Channel;

public class RabbitInterface {
	private final static String QUEUE_NAME = "process";
	
	public static boolean requestAnalysis(String id) {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("54.191.210.230");
		Connection connection;
		try {
			 connection = factory.newConnection();
			 Channel channel = connection.createChannel();
			 
			 channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			 String message = Integer.valueOf(id).toString();
			 channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
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
