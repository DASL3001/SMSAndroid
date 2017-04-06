package com.smspark.smsgatewayprototype;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.smspark.smsgatewayprototype.objects.SMS;

public class MessageQueue {
	private  BlockingQueue<SMS> queue = new ArrayBlockingQueue<SMS>(10);
	private static MessageQueue instance;
    
    private MessageQueue(){}
     
    public static synchronized MessageQueue getInstance(){
        if(instance == null){
            instance = new MessageQueue();
        }
        return instance;
    }

	public BlockingQueue<SMS> getQueue() {
		return queue;
	}
	public void addToQueue(SMS sms) throws InterruptedException{
		 queue.put(sms);		
	}
}
