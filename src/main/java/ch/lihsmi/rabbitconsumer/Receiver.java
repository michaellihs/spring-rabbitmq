package ch.lihsmi.rabbitconsumer;

import java.util.concurrent.CountDownLatch;

public class Receiver {

    private MessageLogger messageLogger = MessageLogger.getInstance();

    public void receiveMessage(String message) {
        messageLogger.log(message);
    }

}